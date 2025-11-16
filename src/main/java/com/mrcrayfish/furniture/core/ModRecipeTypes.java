package com.mrcrayfish.furniture.core;

import com.mrcrayfish.furniture.Reference;
import com.mrcrayfish.furniture.item.crafting.FreezerSolidifyRecipe;
import com.mrcrayfish.furniture.item.crafting.GrillCookingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Author: MrCrayfish
 */
public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, Reference.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<GrillCookingRecipe>> GRILL_COOKING = create("grill_cooking");
    public static final DeferredHolder<RecipeType<?>, RecipeType<FreezerSolidifyRecipe>> FREEZER_SOLIDIFY = create("freezer_solidify");

    private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> create(String name) {
        return REGISTER.register(name, () -> new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }
}

