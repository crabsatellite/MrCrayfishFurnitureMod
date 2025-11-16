package com.mrcrayfish.furniture.core;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.client.MailBoxEntry;
import com.mrcrayfish.furniture.inventory.container.CrateMenu;
import com.mrcrayfish.furniture.inventory.container.FreezerMenu;
import com.mrcrayfish.furniture.inventory.container.MailBoxMenu;
import com.mrcrayfish.furniture.inventory.container.PostBoxMenu;
import com.mrcrayfish.furniture.tileentity.CrateBlockEntity;
import com.mrcrayfish.furniture.tileentity.FreezerBlockEntity;
import com.mrcrayfish.furniture.tileentity.MailBoxBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class ModContainers {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, Reference.MOD_ID);

    @SuppressWarnings("ConstantConditions")
    public static final DeferredHolder<MenuType<?>, MenuType<CrateMenu>> CRATE = register("crate", (IContainerFactory<CrateMenu>) (windowId, playerInventory, data) -> {
        CrateBlockEntity crateBlockEntity = (CrateBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new CrateMenu(windowId, playerInventory, crateBlockEntity, crateBlockEntity.isLocked());
    });

    public static final DeferredHolder<MenuType<?>, MenuType<PostBoxMenu>> POST_BOX = register("post_box", (IContainerFactory<PostBoxMenu>) (windowId, playerInventory, data) -> {
        CompoundTag compound = Objects.requireNonNull(data.readNbt());
        List<MailBoxEntry> entries = new ArrayList<>();
        ListTag mailBoxList = compound.getList("MailBoxes", Tag.TAG_COMPOUND);
        mailBoxList.forEach(nbt -> entries.add(new MailBoxEntry((CompoundTag) nbt)));
        return new PostBoxMenu(windowId, playerInventory, entries);
    });

    public static final DeferredHolder<MenuType<?>, MenuType<MailBoxMenu>> MAIL_BOX = register("mail_box", (IContainerFactory<MailBoxMenu>) (windowId, playerInventory, data) -> {
        MailBoxBlockEntity mailBoxBlockEntity = (MailBoxBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new MailBoxMenu(windowId, playerInventory, mailBoxBlockEntity);
    });

    public static final DeferredHolder<MenuType<?>, MenuType<FreezerMenu>> FREEZER = register("freezer", (IContainerFactory<FreezerMenu>) (windowId, playerInventory, data) -> {
        FreezerBlockEntity freezerBlockEntity = (FreezerBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new FreezerMenu(windowId, playerInventory, freezerBlockEntity);
    });

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String key, MenuType.MenuSupplier<T> supplier) {
        return REGISTER.register(key, () -> new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS));
    }
}

