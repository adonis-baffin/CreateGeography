package com.adonis.compat.jei.category;

import com.adonis.compat.jei.GeographyJEIPlugin;
import com.adonis.content.crafting.FragmentingRecipe;
import com.adonis.registry.ItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class FragmentingRecipeCategory implements IRecipeCategory<FragmentingRecipe> {

    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable chanceSlot;

    public FragmentingRecipeCategory(IGuiHelper guiHelper) {
        title = Component.translatable("jei.creategeography.recipe.fragmenting");
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistry.GEOFRAGMENTATOR.get()));

        // 使用与动力冲压机相同的背景尺寸
        background = guiHelper.createBlankDrawable(177, 90);

        // 创建槽位背景 - 使用 AllGuiTextures 的位置信息
        slot = guiHelper.createDrawable(AllGuiTextures.JEI_SLOT.location,
                AllGuiTextures.JEI_SLOT.startX, AllGuiTextures.JEI_SLOT.startY,
                AllGuiTextures.JEI_SLOT.width, AllGuiTextures.JEI_SLOT.height);
        chanceSlot = guiHelper.createDrawable(AllGuiTextures.JEI_CHANCE_SLOT.location,
                AllGuiTextures.JEI_CHANCE_SLOT.startX, AllGuiTextures.JEI_CHANCE_SLOT.startY,
                AllGuiTextures.JEI_CHANCE_SLOT.width, AllGuiTextures.JEI_CHANCE_SLOT.height);
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
        // 输入槽 - 与动力冲压机位置完全相同 (27, 51)
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(slot, -1, -1)
                .addIngredients(recipe.getIngredients().get(0));

        // 如果有方块转换，在右上角显示输出方块
        if (recipe.getOutputBlock().getBlock() != Blocks.AIR) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 20)
                    .setBackground(slot, -1, -1)
                    .addItemStack(new ItemStack(recipe.getOutputBlock().getBlock()));
        }

        // 输出物品 - 1-2个产物保持原位置，超过2个则换行排列
        List<FragmentingRecipe.ChanceResult> results = recipe.getRollableResults();
        int outputStartX = 131;
        int outputStartY = 50; // 恢复原来的Y位置
        int slotSpacing = 19;

        for (int i = 0; i < results.size(); i++) {
            FragmentingRecipe.ChanceResult result = results.get(i);
            int index = i; // 用于回调中的索引

            int slotX, slotY;

            if (results.size() <= 2) {
                // 1-2个产物：保持原来的水平排列位置
                slotX = outputStartX + i * slotSpacing;
                slotY = outputStartY;
            } else {
                // 超过2个产物：使用网格布局，每行最多2个
                int row = i / 2;
                int col = i % 2;
                slotX = outputStartX + col * slotSpacing;
                slotY = outputStartY + row * slotSpacing;
            }

            // 根据概率选择槽位背景
            IDrawable slotBackground = result.getChance() < 1.0f ? chanceSlot : slot;

            builder.addSlot(RecipeIngredientRole.OUTPUT, slotX, slotY)
                    .setBackground(slotBackground, -1, -1)
                    .addItemStack(result.getStack())
                    .addTooltipCallback((slotView, tooltip) -> {
                        float chance = results.get(index).getChance();
                        if (chance < 1.0f) {
                            tooltip.add(1, Component.translatable("jei.creategeography.chance", (int) (chance * 100))
                                    .withStyle(ChatFormatting.GOLD));
                        }
                    });
        }
    }

    @Override
    public void draw(FragmentingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // 绘制阴影 - 与动力冲压机相同位置
        AllGuiTextures.JEI_SHADOW.render(guiGraphics, 61, 41);

        // 绘制主箭头 - 从输入到输出区域中心
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 52, 54);

        // 绘制地质锤图标 - 在机器应该出现的位置，放大2倍
        int hammerX = getBackground().getWidth() / 2 - 16; // 居中，考虑2倍尺寸
        int hammerY = 18; // 稍微调高一点

        // 放大2倍绘制地质锤物品图标
        ItemStack hammer = new ItemStack(ItemRegistry.GEOFRAGMENTATOR.get());
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(hammerX, hammerY, 0);
        poseStack.scale(2.0f, 2.0f, 1.0f);
        guiGraphics.renderItem(hammer, 0, 0);
        poseStack.popPose();
    }
}