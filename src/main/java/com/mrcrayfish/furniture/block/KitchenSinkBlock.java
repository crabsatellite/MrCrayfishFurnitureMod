package com.mrcrayfish.furniture.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.furniture.tileentity.KitchenSinkBlockEntity;
import com.mrcrayfish.furniture.util.VoxelShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class KitchenSinkBlock extends FurnitureHorizontalBlock implements EntityBlock {
    private final boolean bigSink;

    public final ImmutableMap<BlockState, VoxelShape> SHAPES;

    public KitchenSinkBlock(Properties properties, boolean bigSink) {
        super(properties);
        this.bigSink = bigSink;
        SHAPES = this.generateShapes(this.getStateDefinition().getPossibleStates());
    }

    private ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states) {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = new ImmutableMap.Builder<>();
        for (BlockState state : states) {
            List<VoxelShape> shapes = new ArrayList<>();
            Direction direction = state.getValue(DIRECTION);
            if (this.bigSink) {
                shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 15.0), Direction.SOUTH))[direction.get2DDataValue()]);
                shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(0.0, 9.0, 0.0, 16.0, 16.0, 16.0), Direction.SOUTH))[direction.get2DDataValue()]);
            } else {
                shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 15.0), Direction.SOUTH))[direction.get2DDataValue()]);
                shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0), Direction.SOUTH))[direction.get2DDataValue()]);
            }
            builder.put(state, VoxelShapeHelper.combineAll(shapes));
        }
        return builder.build();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return SHAPES.get(state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide()) {
            if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                IFluidHandler handler = FluidUtil.getFluidHandler(level, pos, null).orElse(null);
                if (handler.getFluidInTank(0).getAmount() > 0) {
                    if (!playerEntity.getAbilities().instabuild) {
                        // Create water potion using Minecraft 1.21.1 DataComponents API
                        ItemStack waterPotion = new ItemStack(Items.POTION);
                        waterPotion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
                        heldItem.shrink(1);
                        if (heldItem.isEmpty()) {
                            playerEntity.setItemInHand(hand, waterPotion);
                        } else if (!playerEntity.getInventory().add(waterPotion)) {
                            playerEntity.drop(waterPotion, false);
                        } else if (playerEntity instanceof ServerPlayer) {
                            playerEntity.inventoryMenu.sendAllDataToRemote();
                        }
                    }
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    handler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                    return ItemInteractionResult.SUCCESS;
                }
            }

            // Check if item can interact with fluid handler using FluidUtil
            if (FluidUtil.getFluidHandler(heldItem).isPresent()) {
                return FluidUtil.interactWithFluidHandler(playerEntity, hand, level, pos, result.getDirection()) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player playerEntity, BlockHitResult result) {
        if (!level.isClientSide()) {
            BlockPos waterPos = pos.below().below();
            if (this.isWaterSource(level, waterPos)) {
                IFluidHandler handler = FluidUtil.getFluidHandler(level, pos, null).orElse(null);
                if (handler.getFluidInTank(0).getAmount() != handler.getTankCapacity(0)) {
                    handler.fill(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    level.playSound(null, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

                    Direction direction = state.getValue(DIRECTION);
                    double posX = pos.getX() + 0.5 + direction.getNormal().getX() * 0.1;
                    double posY = pos.getY() + 1.15;
                    double posZ = pos.getZ() + 0.5 + direction.getNormal().getZ() * 0.1;
                    ((ServerLevel) level).sendParticles(ParticleTypes.FALLING_WATER, posX, posY, posZ, 10, 0.01, 0.01, 0.01, 0);

                    int adjacentSources = 0;
                    adjacentSources += this.isWaterSource(level, waterPos.north()) ? 1 : 0;
                    adjacentSources += this.isWaterSource(level, waterPos.east()) ? 1 : 0;
                    adjacentSources += this.isWaterSource(level, waterPos.south()) ? 1 : 0;
                    adjacentSources += this.isWaterSource(level, waterPos.west()) ? 1 : 0;
                    if (adjacentSources < 2) {
                        level.setBlockAndUpdate(waterPos, Blocks.AIR.defaultBlockState());
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    private boolean isWaterSource(Level level, BlockPos pos) {
        return level.getFluidState(pos).getType() == Fluids.WATER;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KitchenSinkBlockEntity(pos, state);
    }
}



