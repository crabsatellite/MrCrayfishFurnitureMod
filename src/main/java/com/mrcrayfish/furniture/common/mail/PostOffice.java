package com.mrcrayfish.furniture.common.mail;

import com.mrcrayfish.furniture.FurnitureConfig;
import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.tileentity.MailBoxBlockEntity;
import com.mrcrayfish.furniture.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(modid = Reference.MOD_ID)
public class PostOffice extends SavedData {
    private static final String ID = Reference.MOD_ID + "_post_office";

    private final Map<UUID, Map<UUID, MailBox>> playerMailboxMap = new HashMap<>();

    public static PostOffice load(CompoundTag tag, HolderLookup.Provider provider) {
        PostOffice postOffice = new PostOffice();
        postOffice.read(tag, provider);
        return postOffice;
    }

    public void read(CompoundTag compound, HolderLookup.Provider provider) {
        this.playerMailboxMap.clear();
        if (compound.contains("PlayerMailBoxes", Tag.TAG_LIST)) {
            ListTag playerMailBoxesList = compound.getList("PlayerMailBoxes", Tag.TAG_COMPOUND);
            playerMailBoxesList.forEach(nbt ->
            {
                CompoundTag playerMailBoxesCompound = (CompoundTag) nbt;
                UUID playerId = playerMailBoxesCompound.getUUID("PlayerUUID");

                if (playerMailBoxesCompound.contains("MailBoxes", Tag.TAG_LIST)) {
                    Map<UUID, MailBox> mailBoxMap = new HashMap<>();
                    ListTag mailBoxList = playerMailBoxesCompound.getList("MailBoxes", Tag.TAG_COMPOUND);
                    mailBoxList.forEach(nbt2 ->
                    {
                        CompoundTag mailBoxCompound = (CompoundTag) nbt2;
                        UUID mailBoxId = mailBoxCompound.getUUID("MailBoxUUID");
                        MailBox mailBox = new MailBox(mailBoxCompound.getCompound("MailBox"), provider);
                        mailBoxMap.put(mailBoxId, mailBox);
                    });
                    this.playerMailboxMap.put(playerId, mailBoxMap);
                }
            });
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        ListTag playerMailBoxesList = new ListTag();
        this.playerMailboxMap.forEach((playerId, mailStorage) ->
        {
            if (!mailStorage.isEmpty()) {
                CompoundTag playerMailBoxesCompound = new CompoundTag();
                playerMailBoxesCompound.putUUID("PlayerUUID", playerId);

                ListTag mailBoxList = new ListTag();
                mailStorage.forEach((mailBoxId, mailBox) ->
                {
                    CompoundTag mailBoxCompound = new CompoundTag();
                    mailBoxCompound.putUUID("MailBoxUUID", mailBoxId);
                    mailBoxCompound.put("MailBox", mailBox.serializeNBT(provider));
                    mailBoxList.add(mailBoxCompound);
                });
                playerMailBoxesCompound.put("MailBoxes", mailBoxList);

                playerMailBoxesList.add(playerMailBoxesCompound);
            }
        });
        compound.put("PlayerMailBoxes", playerMailBoxesList);
        return compound;
    }

    public static void registerMailBox(ServerPlayer player, UUID mailBoxId, String name, BlockPos pos) {
        PostOffice office = get(player.server);
        Map<UUID, MailBox> mailBoxMap = office.playerMailboxMap.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>());
        mailBoxMap.put(mailBoxId, new MailBox(mailBoxId, name, player.getUUID(), player.getName().getString(), pos, player.level().dimension()));
        office.setDirty();
    }

    public static void unregisterMailBox(UUID playerId, UUID mailBoxId) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            PostOffice office = get(server);
            Map<UUID, MailBox> mailBoxMap = office.playerMailboxMap.computeIfAbsent(playerId, uuid -> new HashMap<>());
            mailBoxMap.remove(mailBoxId);
            office.setDirty();
        }
    }

    public static List<MailBox> getMailBoxes(ServerPlayer playerEntity) {
        PostOffice office = get(playerEntity.server);
        return office.playerMailboxMap.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toList());
    }

    public static boolean sendMailToPlayer(UUID playerId, UUID mailBoxId, Mail mail) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            PostOffice office = get(server);
            Map<UUID, MailBox> mailBoxMap = office.playerMailboxMap.computeIfAbsent(playerId, uuid -> new HashMap<>());
            if (mailBoxMap.containsKey(mailBoxId)) {
                if (mailBoxMap.get(mailBoxId).getMailCount() < FurnitureConfig.COMMON.maxMailQueue.get()) {
                    mailBoxMap.get(mailBoxId).addMail(mail);
                    office.setDirty();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets a supplier which provides mail for the specified mail box. Every time you call
     * {@link Supplier#get} it will remove mail from the queue. You should also not store this
     * supplier as it contains a MinecraftServer instance.
     *
     * @param playerId
     * @param mailBoxId
     * @return
     */
    public static Supplier<Mail> getMailForPlayerMailBox(UUID playerId, UUID mailBoxId) {
        return () ->
        {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                PostOffice office = get(server);
                if (office.playerMailboxMap.containsKey(playerId)) {
                    Map<UUID, MailBox> mailBoxMap = office.playerMailboxMap.get(playerId);
                    if (mailBoxMap.containsKey(mailBoxId)) {
                        MailBox mailBox = mailBoxMap.get(mailBoxId);
                        List<Mail> mailStorage = mailBox.getMailStorage();
                        if (!mailStorage.isEmpty()) {
                            office.setDirty();
                            return mailStorage.remove(0);
                        }
                    }
                }
            }
            return null;
        };
    }

    public static boolean setMailBoxName(UUID playerId, UUID mailBoxId, String name) {
        name = name.trim();
        if (name.trim().isEmpty())
            return false;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            PostOffice office = get(server);
            if (!office.playerMailboxMap.containsKey(playerId))
                return false;

            Map<UUID, MailBox> mailBoxMap = office.playerMailboxMap.get(playerId);
            if (!mailBoxMap.containsKey(mailBoxId))
                return false;

            MailBox mailBox = mailBoxMap.get(mailBoxId);
            mailBox.setName(name);

            ServerLevel level = server.getLevel(mailBox.getLevelResourceKey());
            if (level == null || !level.isLoaded(mailBox.getPos()))
                return false;

            if (!(level.getBlockEntity(mailBox.getPos()) instanceof MailBoxBlockEntity mailBoxBlockEntity))
                return false;

            mailBoxBlockEntity.setMailBoxName(name);
            BlockEntityUtil.sendUpdatePacket(mailBoxBlockEntity);
            return true;
        }
        return false;
    }

    public static boolean isRegistered(UUID playerId, UUID mailBoxId) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            PostOffice office = get(server);
            if (office.playerMailboxMap.containsKey(playerId)) {
                return office.playerMailboxMap.get(playerId).containsKey(mailBoxId);
            }
        }
        return false;
    }

    private static PostOffice get(MinecraftServer server) {
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        return Objects.requireNonNull(level).getDataStorage().computeIfAbsent(new SavedData.Factory<>(PostOffice::new, PostOffice::load), ID);
    }

    /*
     * Cleans up invalid mail boxes or mail boxes removed by other means than a player.
     */
    @SubscribeEvent
    public static void onTick(LevelTickEvent.Post event) {
        // LevelTickEvent.Post is server-side only and fires at the end of each level tick
        Level level = event.getLevel();
        if (level.isClientSide())
            return;

        MinecraftServer server = level.getServer();
        if (server != null && server.getTickCount() % 1200 == 0) {
            PostOffice office = get(server);
            office.playerMailboxMap.values().forEach(map ->
            {
                Predicate<MailBox> removePredicate = mailBox ->
                {
                    BlockPos pos = mailBox.getPos();
                    ServerLevel serverLevel = server.getLevel(mailBox.getLevelResourceKey());
                    if (serverLevel != null) {
                        if (serverLevel.isLoaded(pos)) {
                            if (serverLevel.getBlockEntity(pos) instanceof MailBoxBlockEntity mailBoxBlockEntity) {
                                return mailBoxBlockEntity.getId() == null || !Objects.equals(mailBoxBlockEntity.getId(), mailBox.getId());
                            }
                            return true;
                        }
                        return false;
                    }
                    return true;
                };
                if (map.values().removeIf(removePredicate)) {
                    office.setDirty();
                }
            });
        }
    }
}


