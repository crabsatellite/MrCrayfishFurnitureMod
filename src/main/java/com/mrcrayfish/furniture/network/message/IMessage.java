package com.mrcrayfish.furniture.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public interface IMessage<T extends CustomPacketPayload> {
    StreamCodec<FriendlyByteBuf, T> codec();

    void handle(T message, IPayloadContext context);

    static <T> void callServerConsumer(T message, IPayloadContext context, BiConsumer<ServerPlayer, T> consumer) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            consumer.accept(serverPlayer, message);
        }
    }
}

