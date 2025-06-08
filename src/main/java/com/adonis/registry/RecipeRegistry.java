package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.crafting.CrushingRecipe;
import com.adonis.content.crafting.CrushingRecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateGeography.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CreateGeography.MODID);

    public static final RegistryObject<RecipeSerializer<CrushingRecipe>> CRUSHING_SERIALIZER =
            RECIPE_SERIALIZERS.register("crushing", CrushingRecipeSerializer::new);

    public static final RegistryObject<RecipeType<CrushingRecipe>> CRUSHING_TYPE =
            RECIPE_TYPES.register("crushing", () -> registerRecipeType("crushing"));

    private static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
        return new RecipeType<>() {
            public String toString() {
                return CreateGeography.MODID + ":" + identifier;
            }
        };
    }
}