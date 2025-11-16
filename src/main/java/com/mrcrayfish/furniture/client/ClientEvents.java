package com.mrcrayfish.furniture.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.furniture.FurnitureConfig;
import com.mrcrayfish.furniture.Reference;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderOutline(RenderHighlightEvent.Block event) {
        if (!FurnitureConfig.CLIENT.drawCollisionShapes.get()) {
            return;
        }

        event.setCanceled(true);

        BlockHitResult result = event.getTarget();
        BlockPos pos = result.getBlockPos();
        BlockState state = Minecraft.getInstance().level.getBlockState(pos);
        VoxelShape collisionShape = state.getCollisionShape(Minecraft.getInstance().level, pos);
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double posX = camera.getPosition().x;
        double posY = camera.getPosition().y;
        double posZ = camera.getPosition().z;
        VertexConsumer builder = event.getMultiBufferSource().getBuffer(RenderType.lines());
        drawShape(event.getPoseStack(), builder, collisionShape, -posX + pos.getX(), -posY + pos.getY(), -posZ + pos.getZ(), 0.0F, 1.0F, 0.0F, 1.0F);
    }

    private static void drawShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape voxelShape, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        voxelShape.forAllEdges((x1, y1, z1, x2, y2, z2) ->
        {
            float fx1 = (float) (x1 + xIn);
            float fy1 = (float) (y1 + yIn);
            float fz1 = (float) (z1 + zIn);
            float fx2 = (float) (x2 + xIn);
            float fy2 = (float) (y2 + yIn);
            float fz2 = (float) (z2 + zIn);
            
            consumer.vertex(pose.pose(), fx1, fy1, fz1).color(red, green, blue, alpha).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), fx2, fy2, fz2).color(red, green, blue, alpha).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        });
    }
}



