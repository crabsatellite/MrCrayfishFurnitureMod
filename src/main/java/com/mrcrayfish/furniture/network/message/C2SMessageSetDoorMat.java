package com.mrcrayfish.furniture.network.message;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.network.play.ServerPlayHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Author: MrCrayfish
 */
public record C2SMessageSetDoorMat(BlockPos pos, String message) implements CustomPacketPayload, IMessage<C2SMessageSetDoorMat> {
    public static final CustomPacketPayload.Type<C2SMessageSetDoorMat> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(Reference.MOD_ID, "set_door_mat"));
    public static final StreamCodec<FriendlyByteBuf, C2SMessageSetDoorMat> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, C2SMessageSetDoorMat::pos,
            ByteBufCodecs.stringUtf8(64), C2SMessageSetDoorMat::message,
            C2SMessageSetDoorMat::new
    );

    @Override
    public CustomPacketPayload.Type<C2SMessageSetDoorMat> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, C2SMessageSetDoorMat> codec() {
        return CODEC;
    }

    @Override
    public void handle(C2SMessageSetDoorMat message, IPayloadContext context) {
        context.enqueueWork(() -> IMessage.callServerConsumer(message, context, ServerPlayHandler::handleSetDoorMatMessage));
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public String getMessage() {
        return this.message;
    }
}

