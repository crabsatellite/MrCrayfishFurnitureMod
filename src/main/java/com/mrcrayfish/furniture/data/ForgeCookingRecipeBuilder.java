package com.mrcrayfish.furniture.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class ForgeCookingRecipeBuilder {
    private final RecipeCategory category;
    private final ItemLike result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;
    private final AbstractCookingRecipe.Factory<? extends AbstractCookingRecipe> factory;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group = "";

    private ForgeCookingRecipeBuilder(RecipeCategory category, Ingredient ingredient, ItemLike result, float experience, int cookingTime,
                                      RecipeSerializer<? extends AbstractCookingRecipe> serializer,
                                      AbstractCookingRecipe.Factory<? extends AbstractCookingRecipe> factory) {
        this.category = category;
        this.ingredient = ingredient;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.serializer = serializer;
        this.factory = factory;
    }

    public static ForgeCookingRecipeBuilder generic(Ingredient ingredient, RecipeCategory category, ItemLike result, float experience, int cookingTime,
                                                    RecipeSerializer<? extends AbstractCookingRecipe> serializer,
                                                    AbstractCookingRecipe.Factory<? extends AbstractCookingRecipe> factory) {
        return new ForgeCookingRecipeBuilder(category, ingredient, result, experience, cookingTime, serializer, factory);
    }

    public ForgeCookingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public ForgeCookingRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public void save(RecipeOutput output, String id) {
        save(output, ResourceLocation.parse(id));
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        this.advancementBuilder
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        AbstractCookingRecipe recipe = this.factory.create(
                this.group,
                CookingBookCategory.MISC,
                this.ingredient,
                new ItemStack(this.result),
                this.experience,
                this.cookingTime
        );

        AdvancementHolder advancement = this.advancementBuilder.build(
                id.withPrefix("recipes/" + this.category.getFolderName() + "/")
        );

        output.accept(id, recipe, advancement);
    }
}
