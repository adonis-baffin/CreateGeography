//package com.adonis.recipe;
//
//import javax.annotation.Nonnull;
//import javax.annotation.ParametersAreNonnullByDefault;
//
//import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
//
//import net.minecraft.world.level.Level;
//import net.minecraftforge.items.ItemStackHandler;
//import net.minecraftforge.items.wrapper.RecipeWrapper;
//
//public class FreezingRecipe extends ProcessingRecipe<FreezingRecipe.Wrapper> {
//    public static class Wrapper extends RecipeWrapper {
//        public Wrapper() {
//            super(new ItemStackHandler(1));
//        }
//    }
//
//
//
//
//    public FreezingRecipe(ProcessingRecipeParams params) {
//        super(RecipeTypes.FREEZING, params);
//    }
//
//    @Override
//    public boolean matches(@Nonnull Wrapper inv, @Nonnull Level p_44003_) {
//        if (inv.isEmpty())
//            return false;
//        return ingredients.get(0).test(inv.getItem(0));
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
//}
