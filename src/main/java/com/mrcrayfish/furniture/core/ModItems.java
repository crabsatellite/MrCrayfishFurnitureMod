package com.mrcrayfish.furniture.core;

import com.mrcrayfish.furniture.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, Reference.MOD_ID);

    public static final DeferredHolder<Item, Item> SPATULA = register("spatula", () -> {
        // In 1.21.1, SwordItem constructor takes only Tier and Properties
        Item.Properties properties = new Item.Properties();
        return new SwordItem(Tiers.IRON, properties);
    });


    private static DeferredHolder<Item, Item> register(String name, Supplier<Item> item) {
        return REGISTER.register(name, item);
    }
}
