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

public class CraftingRecipes {

    public static void register(Consumer<FinishedRecipe> consumer) {
        // 3木纤维=3纸
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PAPER, 3)
                .requires(ItemRegistry.WOOD_FIBER.get(), 3)
                .unlockedBy("has_wood_fiber", has(ItemRegistry.WOOD_FIBER.get()))
                .save(consumer, "paper_from_wood_fiber");

        // 1硫磺粉1木炭粉6硝石粉=8火药
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER, 8)
                .requires(ItemRegistry.SULFUR_POWDER.get())
                .requires(ItemRegistry.CHARCOAL_POWDER.get())
                .requires(ItemRegistry.NITER_POWDER.get(), 6)
                .unlockedBy("has_sulfur_powder", has(ItemRegistry.SULFUR_POWDER.get()))
                .save(consumer, "gunpowder_from_powders");

        // 4盐-盐块
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SALT_BLOCK.get())
                .pattern("SS")
                .pattern("SS")
                .define('S', ItemRegistry.SALT.get())
                .unlockedBy("has_salt", has(ItemRegistry.SALT.get()))
                .save(consumer, "salt_block_from_salt");

        // 4沙尘-沙子
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.SAND)
                .pattern("SS")
                .pattern("SS")
                .define('S', ItemRegistry.SAND_DUST.get())
                .unlockedBy("has_sand_dust", has(ItemRegistry.SAND_DUST.get()))
                .save(consumer, "sand_from_dust");

        // 4红沙尘-红沙
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.RED_SAND)
                .pattern("SS")
                .pattern("SS")
                .define('S', ItemRegistry.RED_SAND_DUST.get())
                .unlockedBy("has_red_sand_dust", has(ItemRegistry.RED_SAND_DUST.get()))
                .save(consumer, "red_sand_from_dust");

        // 4灰烬-凝灰岩
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.TUFF)
                .pattern("AA")
                .pattern("AA")
                .define('A', ItemRegistry.ASH.get())
                .unlockedBy("has_ash", has(ItemRegistry.ASH.get()))
                .save(consumer, "tuff_from_ash");

        // 3灰烬1沙尘-沙砾
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.GRAVEL)
                .requires(ItemRegistry.ASH.get(), 3)
                .requires(ItemRegistry.SAND_DUST.get())
                .unlockedBy("has_ash", has(ItemRegistry.ASH.get()))
                .save(consumer, "gravel_from_ash_and_sand");

        // 4土坷-泥土
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.DIRT)
                .pattern("CC")
                .pattern("CC")
                .define('C', ItemRegistry.DIRT_CLOD.get())
                .unlockedBy("has_dirt_clod", has(ItemRegistry.DIRT_CLOD.get()))
                .save(consumer, "dirt_from_clods");
    }
}