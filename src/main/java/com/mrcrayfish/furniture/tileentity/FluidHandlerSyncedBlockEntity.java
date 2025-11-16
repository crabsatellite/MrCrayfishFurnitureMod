package com.mrcrayfish.furniture.tileentity;

import com.mrcrayfish.furniture.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class FluidHandlerSyncedBlockEntity extends BlockEntity {
    protected final FluidTank tank;

    public FluidHandlerSyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);
        this.tank = new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                FluidHandlerSyncedBlockEntity.this.syncFluidToClient();
            }
        };
    }

    public FluidTank getTank() {
        return this.tank;
    }

    private void syncFluidToClient() {
        BlockEntityUtil.sendUpdatePacket(this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        //TODO may need to implement fluid fix from Vehicle Mod. See FluidUtils#fixEmptyTag(NBTTagCompound tag)
        super.loadAdditional(tag, registries);
        this.tank.readFromNBT(registries, tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.tank.writeToNBT(registries, tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.loadAdditional(tag, registries);
        }
    }
}

