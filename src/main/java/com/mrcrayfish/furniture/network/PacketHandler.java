package com.mrcrayfish.furniture.network;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class PacketHandler {
    public static final String PROTOCOL_VERSION = "1";

    private static SimpleChannel instance;
    private static int nextId = 0;

    public static void init() {
        instance = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Reference.MOD_ID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();
        register(C2SMessageLockCrate.class, new C2SMessageLockCrate(), NetworkDirection.PLAY_TO_SERVER);
        register(C2SMessageSendMail.class, new C2SMessageSendMail(), NetworkDirection.PLAY_TO_SERVER);
        register(C2SMessageSetMailBoxName.class, new C2SMessageSetMailBoxName(), NetworkDirection.PLAY_TO_SERVER);
        register(C2SMessageOpenMailBox.class, new C2SMessageOpenMailBox(), NetworkDirection.PLAY_TO_SERVER);
        register(S2CMessageFlipGrill.class, new S2CMessageFlipGrill(), NetworkDirection.PLAY_TO_CLIENT);
        register(C2SMessageSetDoorMat.class, new C2SMessageSetDoorMat(), NetworkDirection.PLAY_TO_SERVER);
    }

    private static <T> void register(Class<T> clazz, IMessage<T> message, @Nullable NetworkDirection direction) {
        instance.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle, Optional.ofNullable(direction));
    }

    public static SimpleChannel getPlayChannel() {
        return instance;
    }
}

