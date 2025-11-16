package com.mrcrayfish.furniture.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Since Forge allows NBT tag on the output of the crafting item, this builder allows you to set
 * that tag. This is used only for data generators.
 * <p>
 * Author: MrCrayfish
 */
public class ForgeShapedRecipeBuilder extends ShapedRecipeBuilder {
    private final RecipeCategory category;
    private final String key;
    private final ItemStack result;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> ingredientMap = Maps.newLinkedHashMap();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private final boolean showNotification = true;
    private String group;

    private ForgeShapedRecipeBuilder(RecipeCategory category, String key, ItemStack resultIn) {
        super(category, resultIn.getItem(), resultIn.getCount());
        this.category = category;
        this.key = key;
        this.result = resultIn.copy();
    }

    public static ForgeShapedRecipeBuilder shapedRecipe(RecipeCategory category, String key, ItemStack resultIn) {
        return new ForgeShapedRecipeBuilder(category, key, resultIn);
    }

    public ForgeShapedRecipeBuilder key(Character symbol, TagKey<Item> tagIn) {
        return this.key(symbol, Ingredient.of(tagIn));
    }

    public ForgeShapedRecipeBuilder key(Character symbol, ItemLike itemIn) {
        return this.key(symbol, Ingredient.of(itemIn));
    }

    public ForgeShapedRecipeBuilder key(Character symbol, Ingredient ingredientIn) {
        if (this.ingredientMap.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.ingredientMap.put(symbol, ingredientIn);
            return this;
        }
    }

    public ForgeShapedRecipeBuilder patternLine(String patternIn) {
        if (!this.pattern.isEmpty() && patternIn.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.pattern.add(patternIn);
            return this;
        }
    }

    public ForgeShapedRecipeBuilder addCriterion(String name, Criterion<?> criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public ForgeShapedRecipeBuilder setGroup(String groupIn) {
        this.group = groupIn;
        return this;
    }

    public void build(RecipeOutput output) {
        this.build(output, BuiltInRegistries.ITEM.getKey(this.result.getItem()));
    }

    public void build(RecipeOutput output, String save) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(this.result.getItem());
        ResourceLocation saveLocation = ResourceLocation.parse(save);
        if (saveLocation.equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(output, saveLocation);
        }
    }

    /**
     * Builds this recipe into an {@link RecipeOutput}.
     */
    public void build(RecipeOutput output, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        // Build the shaped recipe
        CraftingBookCategory craftingCategory = this.determineBookCategory(this.category);
        ShapedRecipePattern recipePattern = ShapedRecipePattern.of(this.ingredientMap, this.pattern);
        ShapedRecipe recipe = new ShapedRecipe(
                this.group == null ? "" : this.group,
                craftingCategory,
                recipePattern,
                this.result
        );

        // Build the advancement
        AdvancementHolder advancement = this.advancementBuilder.build(
                id.withPrefix("recipes/" + this.category.getFolderName() + "/")
        );

        // Pass to output
        output.accept(id, recipe, advancement);
    }

    private CraftingBookCategory determineBookCategory(RecipeCategory category) {
        return switch (category) {
            case BUILDING_BLOCKS -> CraftingBookCategory.BUILDING;
            case REDSTONE -> CraftingBookCategory.REDSTONE;
            case TOOLS, COMBAT -> CraftingBookCategory.EQUIPMENT;
            default -> CraftingBookCategory.MISC;
        };
    }

    private boolean hasAnyCriteria() {
        try {
            java.lang.reflect.Field field = Advancement.Builder.class.getDeclaredField("criteria");
            field.setAccessible(true);
            Map<?, ?> criteria = (Map<?, ?>) field.get(this.advancementBuilder);
            return !criteria.isEmpty();
        } catch (Exception e) {
            return true; // Assume criteria exist if reflection fails
        }
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void validate(ResourceLocation id) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.ingredientMap.keySet());
            set.remove(' ');

            for (String s : this.pattern) {
                for (int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.ingredientMap.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            } else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (!this.hasAnyCriteria()) {
                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }
}

