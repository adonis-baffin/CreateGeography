package com.adonis.compat.jei;

import com.adonis.CreateGeography;
import com.adonis.content.crafting.FragmentingRecipe;
import com.adonis.compat.jei.category.FragmentingRecipeCategory;
import com.adonis.registry.ItemRegistry;
import com.adonis.registry.RecipeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class GeographyJEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(CreateGeography.MODID, "jei_plugin");

    public static final RecipeType<FragmentingRecipe> FRAGMENTING_TYPE =
            RecipeType.create(CreateGeography.MODID, "fragmenting", FragmentingRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FragmentingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<FragmentingRecipe> fragmentingRecipes = recipeManager.getAllRecipesFor(RecipeRegistry.FRAGMENTING_TYPE.get());
        registration.addRecipes(FRAGMENTING_TYPE, fragmentingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ItemRegistry.GEOFRAGMENTATOR.get()), FRAGMENTING_TYPE);
    }
}