package com.mrcrayfish.furniture.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Author: MrCrayfish
 */
public class SimpleCookingSerializer<T extends AbstractCookingRecipe> implements net.minecraft.world.item.crafting.RecipeSerializer<T> {
    private final Factory<T> factory;
    private final int defaultCookingTime;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public SimpleCookingSerializer(Factory<T> factory, int defaultCookingTime) {
        this.factory = factory;
        this.defaultCookingTime = defaultCookingTime;
        
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(AbstractCookingRecipe::getGroup),
            CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(AbstractCookingRecipe::category),
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.getIngredients().get(0)),
            ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null)),
            Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AbstractCookingRecipe::getExperience),
            Codec.INT.fieldOf("cookingtime").orElse(this.defaultCookingTime).forGetter(AbstractCookingRecipe::getCookingTime)
        ).apply(instance, (group, category, ingredient, result, experience, cookingTime) -> 
            this.factory.create(group, category, ingredient, result, experience, cookingTime)
        ));
        
        this.streamCodec = StreamCodec.of(
            (buf, recipe) -> {
                buf.writeUtf(recipe.getGroup());
                buf.writeEnum(recipe.category());
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredients().get(0));
                ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItem(null));
                buf.writeFloat(recipe.getExperience());
                buf.writeVarInt(recipe.getCookingTime());
            },
            buf -> {
                String group = buf.readUtf();
                CookingBookCategory category = buf.readEnum(CookingBookCategory.class);
                Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                float experience = buf.readFloat();
                int cookingTime = buf.readVarInt();
                return this.factory.create(group, category, ingredient, result, experience, cookingTime);
            }
        );
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    public interface Factory<T extends AbstractCookingRecipe> {
        T create(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
    }
}
