package com.adonis.data;

import com.adonis.registry.BlockRegistry;
import com.adonis.registry.ItemRegistry;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

public class SmeltingRecipes {

    public static void register(Consumer<FinishedRecipe> consumer) {
        // 木纤维-木炭粉
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ItemRegistry.WOOD_FIBER.get()),
                        RecipeCategory.MISC,
                        ItemRegistry.CHARCOAL_POWDER.get(),
                        0.1f,
                        200
                ).unlockedBy("has_wood_fiber", has(ItemRegistry.WOOD_FIBER.get()))
                .save(consumer, "charcoal_powder_from_wood_fiber");

        // 泥巴=泥土 (使用原版的泥巴方块)
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(Items.MUD),
                        RecipeCategory.BUILDING_BLOCKS,
                        Items.DIRT,
                        0.1f,
                        200
                ).unlockedBy("has_mud", has(Items.MUD))
                .save(consumer, "dirt_from_mud_smelting");

        // 盐碱泥巴=盐碱土
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(BlockRegistry.SALINE_MUD.get()),
                        RecipeCategory.BUILDING_BLOCKS,
                        BlockRegistry.SALINE_DIRT.get(),
                        0.1f,
                        200
                ).unlockedBy("has_saline_mud", has(BlockRegistry.SALINE_MUD.get()))
                .save(consumer, "saline_soil_from_mud_smelting");

        // 冻土=泥巴 (输出原版泥巴方块)
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(BlockRegistry.FROZEN_SOIL.get()),
                        RecipeCategory.BUILDING_BLOCKS,
                        Items.MUD,
                        0.1f,
                        200
                ).unlockedBy("has_frozen_soil", has(BlockRegistry.FROZEN_SOIL.get()))
                .save(consumer, "mud_from_frozen_soil_smelting");
    }
}