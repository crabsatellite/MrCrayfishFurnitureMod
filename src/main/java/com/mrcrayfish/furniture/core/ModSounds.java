package com.mrcrayfish.furniture.core;

import com.mrcrayfish.furniture.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Author: MrCrayfish
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(Registries.SOUND_EVENT, Reference.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CABINET_OPEN = register("block.cabinet.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CABINET_CLOSE = register("block.cabinet.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BEDSIDE_CABINET_OPEN = register("block.bedside_cabinet.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BEDSIDE_CABINET_CLOSE = register("block.bedside_cabinet.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BLINDS_OPEN = register("block.blinds.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BLINDS_CLOSE = register("block.blinds.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_TRAMPOLINE_BOUNCE = register("block.trampoline.bounce");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GRILL_PLACE = register("block.grill.place");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GRILL_FLIP = register("block.grill.flip");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_DIVING_BOARD_BOUNCE = register("block.diving_board.bounce");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_FRIDGE_OPEN = register("block.fridge.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_FRIDGE_CLOSE = register("block.fridge.close");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, name)));
    }
}

