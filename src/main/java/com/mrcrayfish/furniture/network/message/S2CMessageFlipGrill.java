package com.mrcrayfish.furniture.network.message;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.network.play.ClientPlayHandler;
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
public record S2CMessageFlipGrill(BlockPos pos, int position) implements CustomPacketPayload, IMessage<S2CMessageFlipGrill> {
    public static final CustomPacketPayload.Type<S2CMessageFlipGrill> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "flip_grill"));
    public static final StreamCodec<FriendlyByteBuf, S2CMessageFlipGrill> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, S2CMessageFlipGrill::pos,
            ByteBufCodecs.INT, S2CMessageFlipGrill::position,
            S2CMessageFlipGrill::new
    );

    @Override
    public CustomPacketPayload.Type<S2CMessageFlipGrill> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, S2CMessageFlipGrill> codec() {
        return CODEC;
    }

    @Override
    public void handle(S2CMessageFlipGrill message, IPayloadContext context) {
        context.enqueueWork(() -> ClientPlayHandler.handleFlipGrillMessage(message));
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getPosition() {
        return this.position;
    }
}

