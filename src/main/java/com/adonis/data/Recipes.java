//package com.adonis.data;
//
//import com.adonis.CreateGeography;
//import net.minecraft.MethodsReturnNonnullByDefault;
//import net.minecraft.data.PackOutput;
//import net.minecraft.data.recipes.FinishedRecipe;
//import net.minecraft.data.recipes.RecipeProvider;
//import com.adonis.data.CreateRecipes;
//
//import javax.annotation.ParametersAreNonnullByDefault;
//import java.util.function.Consumer;
//
//@ParametersAreNonnullByDefault
//@MethodsReturnNonnullByDefault
//public class Recipes extends RecipeProvider {
//
//    public Recipes(PackOutput output) {
//        super(output);
//    }
//
//    @Override
//    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
//        CreateGeography.LOGGER.info("=== Recipes.buildRecipes called! ===");
//
//        try {
//            CreateRecipes.register(consumer);
//            CreateGeography.LOGGER.info("=== CreateRecipes.register completed ===");
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("Error in CreateRecipes: " + e.getMessage(), e);
//        }
//
//        try {
//            FragmentingRecipes.register(consumer);
//            CreateGeography.LOGGER.info("=== FragmentingRecipes.register completed ===");
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("Error in FragmentingRecipes: " + e.getMessage(), e);
//        }
//    }
//}