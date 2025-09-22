package com.adonis.creategeography.registry;

import com.adonis.creategeography.CreateGeography;
import com.adonis.creategeography.content.crafting.FragmentingRecipe;
import com.adonis.creategeography.content.crafting.FragmentingRecipeSerializer;
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

    public static final RegistryObject<RecipeSerializer<FragmentingRecipe>> FRAGMENTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("fragmenting", FragmentingRecipeSerializer::new);

    public static final RegistryObject<RecipeType<FragmentingRecipe>> FRAGMENTING_TYPE =
            RECIPE_TYPES.register("fragmenting", () -> registerRecipeType("fragmenting"));

    private static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return CreateGeography.MODID + ":" + identifier;
            }
        };
    }
}