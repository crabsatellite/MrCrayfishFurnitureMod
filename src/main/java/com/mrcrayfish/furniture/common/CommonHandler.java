package com.mrcrayfish.furniture.common;

import com.mrcrayfish.furniture.core.ModItems;
import com.mrcrayfish.furniture.tileentity.GrillBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Author: MrCrayfish
 */
public class CommonHandler {
    public static void setup() {
        DispenserBlock.registerBehavior(ModItems.SPATULA::get, (source, stack) ->
        {
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            BlockPos pos = source.pos().relative(direction).below();
            BlockEntity tileEntity = source.level().getBlockEntity(pos);
            if (tileEntity instanceof GrillBlockEntity) {
                ((GrillBlockEntity) tileEntity).flipItems();
            }
            return stack;
        });
    }
}
