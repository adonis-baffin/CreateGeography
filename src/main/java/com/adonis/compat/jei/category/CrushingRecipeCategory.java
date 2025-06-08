package com.adonis.compat.jei.category;

import com.adonis.CreateGeography;
import com.adonis.compat.jei.GeographyJEIPlugin;
import com.adonis.content.crafting.CrushingRecipe;
import com.adonis.registry.ItemRegistry;
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

public class CrushingRecipeCategory implements IRecipeCategory<CrushingRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(CreateGeography.MODID, "crushing");
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable slotChance;
    private final IGuiHelper guiHelper;

    public CrushingRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        title = Component.translatable("jei.creategeography.recipe.crushing");
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemRegistry.GEOFRAGMENTATOR.get()));
        
        // 使用一个简单的背景
        background = guiHelper.createBlankDrawable(120, 60);

        // 自定义槽位背景，用于显示几率
        ResourceLocation jeiSprites = new ResourceLocation(CreateGeography.MODID, "textures/gui/sprites.png");
        slot = guiHelper.drawableBuilder(jeiSprites, 0, 0, 18, 18).build();
        slotChance = guiHelper.drawableBuilder(jeiSprites, 18, 0, 18, 18).build();
    }

    @Override
    public RecipeType<CrushingRecipe> getRecipeType() {
        return GeographyJEIPlugin.CRUSHING_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingRecipe recipe, IFocusGroup focuses) {
        // 输入方块
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 22)
                .addIngredients(recipe.getIngredients().get(0));

        // 工具 (地质破碎锤)
        builder.addSlot(RecipeIngredientRole.CATALYST, 5, 2)
                .addItemStack(new ItemStack(ItemRegistry.GEOFRAGMENTATOR.get()));

        // 输出物品
        int x = 40;
        int y = 5;
        for (int i = 0; i < recipe.getRollableResults().size(); i++) {
            CrushingRecipe.ChanceResult result = recipe.getRollableResults().get(i);
            int currentX = x + (i % 4) * 19;
            int currentY = y + (i / 4) * 19;

            builder.addSlot(RecipeIngredientRole.OUTPUT, currentX, currentY)
                    .addItemStack(result.getStack())
                    .addTooltipCallback((slotView, tooltip) -> {
                        float chance = result.getChance();
                        if (chance != 1.0f) {
                            tooltip.add(Component.translatable("jei.creategeography.chance", (int) (chance * 100))
                                    .withStyle(ChatFormatting.GOLD));
                        }
                    });
        }
    }

    // 绘制几率槽位的背景
    @Override
    public void draw(CrushingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int x = 39;
        int y = 4;
        for (int i = 0; i < recipe.getRollableResults().size(); i++) {
            CrushingRecipe.ChanceResult result = recipe.getRollableResults().get(i);
            int currentX = x + (i % 4) * 19;
            int currentY = y + (i / 4) * 19;
            
            IDrawable toDraw = result.getChance() == 1.0f ? slot : slotChance;
            toDraw.draw(guiGraphics, currentX, currentY);
        }
    }
}