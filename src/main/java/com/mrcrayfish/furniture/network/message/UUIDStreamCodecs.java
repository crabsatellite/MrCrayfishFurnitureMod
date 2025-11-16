package com.mrcrayfish.furniture.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

/**
 * Utility class for UUID StreamCodec
 * Author: MrCrayfish
 */
public class UUIDStreamCodecs {
    public static final StreamCodec<FriendlyByteBuf, UUID> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public UUID decode(FriendlyByteBuf buffer) {
            return buffer.readUUID();
        }

        @Override
        public void encode(FriendlyByteBuf buffer, UUID uuid) {
            buffer.writeUUID(uuid);
        }
    };
}
