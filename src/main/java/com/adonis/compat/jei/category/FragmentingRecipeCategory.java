package com.adonis.compat.jei.category;

import com.adonis.CreateGeography;
import com.adonis.compat.jei.GeographyJEIPlugin;
import com.adonis.content.crafting.FragmentingRecipe;
import com.adonis.registry.ItemRegistry;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.ponder.ui.LayoutHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class FragmentingRecipeCategory implements IRecipeCategory<FragmentingRecipe> {

    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;

    public FragmentingRecipeCategory(IGuiHelper guiHelper) {
        title = Component.translatable("jei.creategeography.recipe.fragmenting");
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistry.GEOFRAGMENTATOR.get()));

        // 我们需要一个更宽的背景来容纳可能的多行输出
        background = guiHelper.createBlankDrawable(177, 100);

        // 使用Create的箭头贴图，或者你自己的
        // 假设箭头在sprites.png的这个位置
        ResourceLocation jeiSprites = new ResourceLocation(CreateGeography.MODID, "textures/gui/jei/sprites.png");
        arrow = guiHelper.createDrawable(jeiSprites, 0, 18, 24, 17);
    }

    @Override
    public RecipeType<FragmentingRecipe> getRecipeType() {
        return GeographyJEIPlugin.FRAGMENTING_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FragmentingRecipe recipe, IFocusGroup focuses) {
        // --- 输入和工具 ---
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 41)
                .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.CATALYST, 51, 10)
                .addItemStack(new ItemStack(ItemRegistry.GEOFRAGMENTATOR.get()));

        if (recipe.getOutputBlock().getBlock() != Blocks.AIR) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 27, 10)
                    .addItemStack(new ItemStack(recipe.getOutputBlock().getBlock()));
        }

        // --- 输出物品布局 ---
        List<FragmentingRecipe.ChanceResult> recipeOutputs = recipe.getRollableResults();
        int outboxSize = 85; // 输出区域的总宽度
        int outputCount = recipeOutputs.size();

        // 使用LayoutHelper来优雅地排列输出
        LayoutHelper layout = LayoutHelper.centeredHorizontal(outputCount, 1, 18, 18, 1);
        int xOffset = 95; // 输出区域的起始X坐标
        int yOffset = 40; // 输出区域的起始Y坐标

        for (FragmentingRecipe.ChanceResult result : recipeOutputs) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, xOffset + layout.getX(), yOffset + layout.getY())
                    .addItemStack(result.getStack())
                    // --- 核心改动：添加Tooltip回调 ---
                    .addTooltipCallback((recipeSlotView, tooltip) -> {
                        float chance = result.getChance();
                        // 只有在几率不为100%时才显示
                        if (chance < 1.0f) {
                            tooltip.add(1, Component.translatable("jei.creategeography.chance", (int) (chance * 100))
                                    .withStyle(ChatFormatting.GOLD));
                        }
                    });
            layout.next();
        }
    }

    @Override
    public void draw(FragmentingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // 绘制输入->输出的箭头
        arrow.draw(guiGraphics, 60, 41);

        // 如果既有方块转变，又有物品掉落，可以画一个额外的箭头
        if (recipe.getOutputBlock().getBlock() != Blocks.AIR && !recipe.getRollableResults().isEmpty()) {
            AllGuiTextures.JEI_ARROW.render(guiGraphics, 60, 10);
        }
    }
}