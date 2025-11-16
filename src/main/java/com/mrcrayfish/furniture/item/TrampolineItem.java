package com.mrcrayfish.furniture.item;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.tileentity.TrampolineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class TrampolineItem extends BlockItem implements CreativeItem {
    public TrampolineItem(Block blockIn, Item.Properties builder) {
        super(blockIn, builder);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        CustomData customData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if (!customData.isEmpty()) {
            CompoundTag blockEntityTag = customData.copyTag();
            DyeColor color = DyeColor.byId(blockEntityTag.getInt("Color"));
            if (level.getBlockEntity(pos) instanceof TrampolineBlockEntity blockEntity) {
                blockEntity.setColour(color);
            }
        }
        return true;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if (!customData.isEmpty()) {
            CompoundTag blockEntityTag = customData.copyTag();
            DyeColor color = DyeColor.byId(blockEntityTag.getInt("Color"));
            return String.format("block.%s.%s_trampoline", Reference.MOD_ID, color.getName());
        }
        return super.getDescriptionId(stack);
    }

    @Override
    public void fill(Consumer<ItemStack> output) {
        for (DyeColor color : DyeColor.values()) {
            ItemStack stack = new ItemStack(this);
            CompoundTag blockEntityTag = new CompoundTag();
            blockEntityTag.putInt("Color", color.getId());
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
            output.accept(stack);
        }
    }
}
