package com.mrcrayfish.furniture.network.message;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.network.play.ServerPlayHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Author: MrCrayfish
 */
public record C2SMessageOpenMailBox(BlockPos pos) implements CustomPacketPayload, IMessage<C2SMessageOpenMailBox> {
    public static final CustomPacketPayload.Type<C2SMessageOpenMailBox> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(Reference.MOD_ID, "open_mailbox"));
    public static final StreamCodec<FriendlyByteBuf, C2SMessageOpenMailBox> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, C2SMessageOpenMailBox::pos,
            C2SMessageOpenMailBox::new
    );

    @Override
    public CustomPacketPayload.Type<C2SMessageOpenMailBox> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, C2SMessageOpenMailBox> codec() {
        return CODEC;
    }

    @Override
    public void handle(C2SMessageOpenMailBox message, IPayloadContext context) {
        context.enqueueWork(() -> IMessage.callServerConsumer(message, context, ServerPlayHandler::handleOpenMailBoxMessage));
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

