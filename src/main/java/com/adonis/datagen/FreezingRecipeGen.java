package com.adonis.datagen;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import com.adonis.recipe.RecipeTypes;
import com.adonis.CreateGeography;
import com.adonis.recipe.FreezingRecipe;


import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.adonis.registry.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import static mezz.jei.api.ingredients.subtypes.UidContext.Recipe;

public class FreezingRecipeGen extends CreateRecipeProvider {
    GeneratedRecipe PERMAFROST = convert(Items.DIRT, Items.DIRT);
    GeneratedRecipe END_STONE_BRICKS = convert(Items.STONE_BRICKS, Items.END_STONE_BRICKS);
    GeneratedRecipe END_STONE_BRICK_STAIRS = convert(Items.STONE_BRICK_STAIRS, Items.END_STONE_BRICK_STAIRS);
    GeneratedRecipe END_STONE_BRICK_SLAB = convert(Items.STONE_BRICK_SLAB, Items.END_STONE_BRICK_SLAB);
    GeneratedRecipe END_STONE_BRICK_WALL = convert(Items.STONE_BRICK_WALL, Items.END_STONE_BRICK_WALL);
    GeneratedRecipe DRAGON_BREATH = convert(Items.GLASS_BOTTLE, Items.DRAGON_BREATH);
    GeneratedRecipe CHORUS_FRUIT = convert(Items.APPLE, Items.CHORUS_FRUIT);
    GeneratedRecipe ENDER_PEARL = convert(Items.PRISMARINE_CRYSTALS, Items.ENDER_PEARL);
    GeneratedRecipe END_ROD = convert(Items.BLAZE_ROD, Items.END_ROD);

    public FreezingRecipeGen(PackOutput output) {
        super(output);
    }

    public GeneratedRecipe convert(Item item, Item result) {
        return create(() -> item, b -> b.output(result));
    }

    public GeneratedRecipe convert(Supplier<ItemLike> item, Supplier<ItemLike> result) {
        return create(item, b -> b.output((ItemLike) result));
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return create(CreateGeography.MODID, singleIngredient, transform);
    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(String namespace, Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        ProcessingRecipeSerializer<T> serializer = RecipeTypes.FREEZING.getSerializer();
        GeneratedRecipe generatedRecipe = c -> {
            ItemLike itemLike = singleIngredient.get();
            transform.apply(new ProcessingRecipeBuilder<>(serializer.getFactory(), new ResourceLocation(namespace, RegisteredObjects.getKeyOrThrow(itemLike.asItem()).getPath())).withItemIngredients(Ingredient.of(itemLike))).build(c);
        };
        all.add(generatedRecipe);
        return generatedRecipe;
    }
}
