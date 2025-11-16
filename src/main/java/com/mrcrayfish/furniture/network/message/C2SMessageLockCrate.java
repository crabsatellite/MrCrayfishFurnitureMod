package com.mrcrayfish.furniture.network.message;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.network.play.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Author: MrCrayfish
 */
public record C2SMessageLockCrate() implements CustomPacketPayload, IMessage<C2SMessageLockCrate> {
    public static final CustomPacketPayload.Type<C2SMessageLockCrate> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "lock_crate"));
    public static final StreamCodec<FriendlyByteBuf, C2SMessageLockCrate> CODEC = StreamCodec.unit(new C2SMessageLockCrate());

    @Override
    public CustomPacketPayload.Type<C2SMessageLockCrate> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, C2SMessageLockCrate> codec() {
        return CODEC;
    }

    @Override
    public void handle(C2SMessageLockCrate message, IPayloadContext context) {
        context.enqueueWork(() -> IMessage.callServerConsumer(message, context, ServerPlayHandler::handleLockCrateMessage));
    }
}

