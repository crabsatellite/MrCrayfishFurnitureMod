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
public record C2SMessageSetMailBoxName(String name, BlockPos pos) implements CustomPacketPayload, IMessage<C2SMessageSetMailBoxName> {
    public static final CustomPacketPayload.Type<C2SMessageSetMailBoxName> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "set_mailbox_name"));
    public static final StreamCodec<FriendlyByteBuf, C2SMessageSetMailBoxName> CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(32), C2SMessageSetMailBoxName::name,
            BlockPos.STREAM_CODEC, C2SMessageSetMailBoxName::pos,
            C2SMessageSetMailBoxName::new
    );

    @Override
    public CustomPacketPayload.Type<C2SMessageSetMailBoxName> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, C2SMessageSetMailBoxName> codec() {
        return CODEC;
    }

    @Override
    public void handle(C2SMessageSetMailBoxName message, IPayloadContext context) {
        context.enqueueWork(() -> IMessage.callServerConsumer(message, context, ServerPlayHandler::handleSetMailBoxNameMessage));
    }

    public String getName() {
        return this.name;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

