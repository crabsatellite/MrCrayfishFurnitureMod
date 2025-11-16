package com.mrcrayfish.furniture.core;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.entity.SeatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, Reference.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT = register("seat", EntityType.Builder.<SeatEntity>of((type, world) -> new SeatEntity(world), MobCategory.MISC).sized(0.0F, 0.0F));

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return REGISTER.register(name, () -> builder.build(name));
    }
}
