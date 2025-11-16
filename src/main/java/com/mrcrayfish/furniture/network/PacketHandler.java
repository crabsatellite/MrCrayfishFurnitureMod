package com.mrcrayfish.furniture.network;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.network.message.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PacketHandler {
    
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Reference.MOD_ID).versioned("1.0.0");
        
        // Client to Server messages
        registrar.playToServer(
                C2SMessageLockCrate.TYPE,
                C2SMessageLockCrate.CODEC,
                (message, context) -> message.handle(message, context)
        );
        registrar.playToServer(
                C2SMessageSendMail.TYPE,
                C2SMessageSendMail.CODEC,
                (message, context) -> message.handle(message, context)
        );
        registrar.playToServer(
                C2SMessageSetMailBoxName.TYPE,
                C2SMessageSetMailBoxName.CODEC,
                (message, context) -> message.handle(message, context)
        );
        registrar.playToServer(
                C2SMessageOpenMailBox.TYPE,
                C2SMessageOpenMailBox.CODEC,
                (message, context) -> message.handle(message, context)
        );
        registrar.playToServer(
                C2SMessageSetDoorMat.TYPE,
                C2SMessageSetDoorMat.CODEC,
                (message, context) -> message.handle(message, context)
        );
        
        // Server to Client messages
        registrar.playToClient(
                S2CMessageFlipGrill.TYPE,
                S2CMessageFlipGrill.CODEC,
                (message, context) -> message.handle(message, context)
        );
    }
}

