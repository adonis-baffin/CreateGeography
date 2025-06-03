//package com.adonis.recipe;
//
//import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.items.ItemStackHandler;
//import net.minecraftforge.items.wrapper.RecipeWrapper;
//import com.adonis.recipe.RecipeTypes;
//
//import javax.annotation.ParametersAreNonnullByDefault;
//
//@ParametersAreNonnullByDefault
//public class WeatheringRecipe extends ProcessingRecipe<WeatheringRecipe.WeatheringWrapper> {
//
//    public WeatheringRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
//        super(RecipeTypes.WEATHERING, params);
//    }
//
//    @Override
//    public boolean matches(WeatheringWrapper inv, Level worldIn) {
//        if (inv.isEmpty())
//            return false;
//        return ingredients.get(0)
//                .test(inv.getItem(0));
//    }
//
//    @Override
//    protected int getMaxInputCount() {
//        return 1;
//    }
//
//    @Override
//    protected int getMaxOutputCount() {
//        return 12;
//    }
//
//    public static class WeatheringWrapper extends RecipeWrapper {
//        public WeatheringWrapper() {
//            super(new ItemStackHandler(1));
//        }
//    }
//
//}