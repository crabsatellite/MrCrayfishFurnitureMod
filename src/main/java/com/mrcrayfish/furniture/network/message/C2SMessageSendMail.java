package com.mrcrayfish.furniture.network.message;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.network.play.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public record C2SMessageSendMail(UUID playerId, UUID mailBoxId) implements CustomPacketPayload, IMessage<C2SMessageSendMail> {
    public static final CustomPacketPayload.Type<C2SMessageSendMail> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "send_mail"));
    public static final StreamCodec<FriendlyByteBuf, C2SMessageSendMail> CODEC = StreamCodec.composite(
            UUIDStreamCodecs.STREAM_CODEC, C2SMessageSendMail::playerId,
            UUIDStreamCodecs.STREAM_CODEC, C2SMessageSendMail::mailBoxId,
            C2SMessageSendMail::new
    );

    @Override
    public CustomPacketPayload.Type<C2SMessageSendMail> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, C2SMessageSendMail> codec() {
        return CODEC;
    }

    @Override
    public void handle(C2SMessageSendMail message, IPayloadContext context) {
        context.enqueueWork(() -> IMessage.callServerConsumer(message, context, ServerPlayHandler::handleSendMailMessage));
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public UUID getMailBoxId() {
        return this.mailBoxId;
    }
}

