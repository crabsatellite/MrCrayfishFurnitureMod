package com.mrcrayfish.furniture.block;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.furniture.common.mail.MailBox;
import com.mrcrayfish.furniture.common.mail.PostOffice;
import com.mrcrayfish.furniture.inventory.container.PostBoxMenu;
import com.mrcrayfish.furniture.util.VoxelShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class PostBoxBlock extends FurnitureHorizontalBlock {
    private static final Component TITLE = Component.translatable("container.cfm.post_box");

    public final VoxelShape SHAPE;

    public PostBoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH));
        SHAPE = this.generateShape();
    }

    private VoxelShape generateShape() {
        List<VoxelShape> shapes = new ArrayList<>();
        shapes.add(Block.box(1, 0, 1, 3, 6, 3));
        shapes.add(Block.box(13, 0, 1, 15, 6, 3));
        shapes.add(Block.box(13, 0, 13, 15, 6, 15));
        shapes.add(Block.box(1, 0, 13, 3, 6, 15));
        shapes.add(Block.box(1, 6, 1, 15, 23, 15));
        return VoxelShapeHelper.combineAll(shapes);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        if (player instanceof ServerPlayer serverPlayer) {
            // In 1.21.1, we need to get the mailboxes and pass them through the MenuProvider
            List<MailBox> mailBoxes = PostOffice.getMailBoxes(serverPlayer);
            serverPlayer.openMenu(new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> {
                List<com.mrcrayfish.furniture.client.MailBoxEntry> entries = new ArrayList<>();
                for (MailBox mailBox : mailBoxes) {
                    entries.add(new com.mrcrayfish.furniture.client.MailBoxEntry(
                        mailBox.getId(),
                        mailBox.getName(),
                        mailBox.getOwnerId(),
                        mailBox.getOwnerName()
                    ));
                }
                return new PostBoxMenu(windowId, playerInventory, ContainerLevelAccess.create(level, pos), entries);
            }, TITLE));
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> {
            return new PostBoxMenu(windowId, playerInventory, ContainerLevelAccess.create(level, pos), ImmutableList.of());
        }, TITLE);
    }
}


