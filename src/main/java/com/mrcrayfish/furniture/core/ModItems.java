package com.mrcrayfish.furniture.core;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.item.SpatulaItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, Reference.MOD_ID);

    public static final DeferredHolder<Item, Item> SPATULA = register("spatula", () -> 
        new SpatulaItem(new Item.Properties().durability(250))
    );

    private static DeferredHolder<Item, Item> register(String name, Supplier<Item> item) {
        return REGISTER.register(name, item);
    }
}
