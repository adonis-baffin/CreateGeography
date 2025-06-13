//package com.adonis.data;
//
//import com.adonis.CreateGeography;
//import com.adonis.fluid.GeographyFluids;
//import com.adonis.registry.ItemRegistry;
//import com.simibubi.create.AllRecipeTypes;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
//import net.minecraft.data.recipes.FinishedRecipe;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.level.material.Fluids;
//import net.minecraftforge.fluids.FluidStack;
//
//import java.util.function.Consumer;
//
//public class CreateRecipes {
//
//    public static void register(Consumer<FinishedRecipe> consumer) {
//        CreateGeography.LOGGER.info("=== CreateRecipes.register called! ===");
//
//        // 检查关键物品是否存在
//        try {
//            ItemRegistry.SALT.get();
//            ItemRegistry.WOOD_FIBER.get();
//            GeographyFluids.BRINE.get();
//            CreateGeography.LOGGER.info("=== All required items/fluids are registered ===");
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("=== Missing required items/fluids: " + e.getMessage(), e);
//            return;
//        }
//
//        // 压块塑形配方
//        CreateGeography.LOGGER.info("=== Registering compacting recipes ===");
//        registerCompactingRecipes(consumer);
//        CreateGeography.LOGGER.info("=== Compacting recipes registered ===");
//
//        // 注液配方
//        CreateGeography.LOGGER.info("=== Registering filling recipes ===");
//        registerFillingRecipes(consumer);
//        CreateGeography.LOGGER.info("=== Filling recipes registered ===");
//
//        // 水洗配方
//        CreateGeography.LOGGER.info("=== Registering washing recipes ===");
//        registerWashingRecipes(consumer);
//        CreateGeography.LOGGER.info("=== Washing recipes registered ===");
//
//        // 混合搅拌配方
//        CreateGeography.LOGGER.info("=== Registering mixing recipes ===");
//        registerMixingRecipes(consumer);
//        CreateGeography.LOGGER.info("=== Mixing recipes registered ===");
//
//        // 粉碎轮配方
//        CreateGeography.LOGGER.info("=== Registering crushing recipes ===");
//        registerCrushingRecipes(consumer);
//        CreateGeography.LOGGER.info("=== Crushing recipes registered ===");
//
//        // 研磨配方
//        CreateGeography.LOGGER.info("=== Registering milling recipes ===");
//        registerMillingRecipes(consumer);
//        CreateGeography.LOGGER.info("=== Milling recipes registered ===");
//
//        // 砂纸打磨配方
//        CreateGeography.LOGGER.info("=== Registering sandpaper polishing recipes ===");
//        registerSandpaperPolishingRecipes(consumer);
//        CreateGeography.LOGGER.info("=== Sandpaper polishing recipes registered ===");
//
//        // 冲压配方
//        CreateGeography.LOGGER.info("=== Registering pressing recipes ===");
//        registerPressingRecipes(consumer);
//        CreateGeography.LOGGER.info("=== Pressing recipes registered ===");
//
//        CreateGeography.LOGGER.info("=== All Create recipes registered successfully! ===");
//    }
//
//    private static void registerCompactingRecipes(Consumer<FinishedRecipe> consumer) {
//        // 海带+250mb水=250mb盐水+木纤维
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.COMPACTING.getSerializer(),
//                new ResourceLocation("creategeography", "kelp_to_brine"))
//                .require(Items.KELP)
//                .require(Fluids.WATER, 250)
//                .output(GeographyFluids.BRINE.get(), 250)
//                .output(ItemRegistry.WOOD_FIBER.get())
//                .build(consumer);
//
//        // 红树根+250mb水=250mb盐水+木纤维
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.COMPACTING.getSerializer(),
//                new ResourceLocation("creategeography", "mangrove_roots_to_brine"))
//                .require(Items.MANGROVE_ROOTS)
//                .require(Fluids.WATER, 250)
//                .output(GeographyFluids.BRINE.get(), 250)
//                .output(ItemRegistry.WOOD_FIBER.get())
//                .build(consumer);
//
//        // 4沙尘-沙子 (修复：使用多个require调用或者Ingredient.of)
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.COMPACTING.getSerializer(),
//                new ResourceLocation("creategeography", "sand_dust_to_sand"))
//                .require(Ingredient.of(ItemRegistry.SAND_DUST.get()).getItems()[0].getItem())
//                .require(Ingredient.of(ItemRegistry.SAND_DUST.get()).getItems()[0].getItem())
//                .require(Ingredient.of(ItemRegistry.SAND_DUST.get()).getItems()[0].getItem())
//                .require(Ingredient.of(ItemRegistry.SAND_DUST.get()).getItems()[0].getItem())
//                .output(Items.SAND)
//                .build(consumer);
//
//        // 或者更简洁的写法：4红沙尘-红沙
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.COMPACTING.getSerializer(),
//                new ResourceLocation("creategeography", "red_sand_dust_to_red_sand"))
//                .require(ItemRegistry.RED_SAND_DUST.get())
//                .require(ItemRegistry.RED_SAND_DUST.get())
//                .require(ItemRegistry.RED_SAND_DUST.get())
//                .require(ItemRegistry.RED_SAND_DUST.get())
//                .output(Items.RED_SAND)
//                .build(consumer);
//
//        // 原木-4木纤维
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.COMPACTING.getSerializer(),
//                new ResourceLocation("creategeography", "oak_log_to_wood_fiber"))
//                .require(Items.OAK_LOG)
//                .output(ItemRegistry.WOOD_FIBER.get(), 4)
//                .build(consumer);
//    }
//
//    private static void registerFillingRecipes(Consumer<FinishedRecipe> consumer) {
//        // 250mb盐水+玻璃瓶=盐水瓶
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.FILLING.getSerializer(),
//                new ResourceLocation("creategeography", "brine_bottle"))
//                .require(Items.GLASS_BOTTLE)
//                .require(GeographyFluids.BRINE.get(), 250)
//                .output(ItemRegistry.BRINE_BOTTLE.get())
//                .build(consumer);
//    }
//
//    private static void registerWashingRecipes(Consumer<FinishedRecipe> consumer) {
//        // 石英砂-0.5下界石英
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.SPLASHING.getSerializer(),
//                new ResourceLocation("creategeography", "quartz_sand_washing"))
//                .require(ItemRegistry.QUARTZ_SAND.get())
//                .output(0.5f, Items.QUARTZ)
//                .build(consumer);
//    }
//
//    private static void registerMixingRecipes(Consumer<FinishedRecipe> consumer) {
//        // 250mb水+盐=250mb盐水
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.MIXING.getSerializer(),
//                new ResourceLocation("creategeography", "salt_to_brine"))
//                .require(ItemRegistry.SALT.get())
//                .require(Fluids.WATER, 250)
//                .output(GeographyFluids.BRINE.get(), 250)
//                .build(consumer);
//
//        // 1硫磺粉1木炭粉6硝石粉=8火药 (修复：需要6个硝石粉的require调用)
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.MIXING.getSerializer(),
//                new ResourceLocation("creategeography", "gunpowder_mixing"))
//                .require(ItemRegistry.SULFUR_POWDER.get())
//                .require(ItemRegistry.CHARCOAL_POWDER.get())
//                .require(ItemRegistry.NITER_POWDER.get())
//                .require(ItemRegistry.NITER_POWDER.get())
//                .require(ItemRegistry.NITER_POWDER.get())
//                .require(ItemRegistry.NITER_POWDER.get())
//                .require(ItemRegistry.NITER_POWDER.get())
//                .require(ItemRegistry.NITER_POWDER.get())
//                .output(Items.GUNPOWDER, 8)
//                .build(consumer);
//
//        // 硝石+1000mb水=冰
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.MIXING.getSerializer(),
//                new ResourceLocation("creategeography", "niter_to_ice"))
//                .require(ItemRegistry.NITER.get())
//                .require(Fluids.WATER, 1000)
//                .output(Items.ICE)
//                .build(consumer);
//    }
//
//    private static void registerCrushingRecipes(Consumer<FinishedRecipe> consumer) {
//        CreateGeography.LOGGER.info("=== Starting to register individual crushing recipes ===");
//
//        // 煤炭：1+0.5*2煤粉
//        CreateGeography.LOGGER.info("=== Registering coal crushing recipe ===");
//        try {
//            new ProcessingRecipeBuilder<>(AllRecipeTypes.CRUSHING.getSerializer(),
//                    new ResourceLocation("creategeography", "coal_crushing"))
//                    .require(Items.COAL)
//                    .output(ItemRegistry.COAL_POWDER.get())
//                    .output(0.5f, ItemRegistry.COAL_POWDER.get(), 2)
//                    .build(consumer);
//            CreateGeography.LOGGER.info("=== Coal crushing recipe registered successfully ===");
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("=== Error registering coal crushing recipe: " + e.getMessage(), e);
//        }
//
//        // 木炭：1+0.5*2木炭粉
//        CreateGeography.LOGGER.info("=== Registering charcoal crushing recipe ===");
//        try {
//            new ProcessingRecipeBuilder<>(AllRecipeTypes.CRUSHING.getSerializer(),
//                    new ResourceLocation("creategeography", "charcoal_crushing"))
//                    .require(Items.CHARCOAL)
//                    .output(ItemRegistry.CHARCOAL_POWDER.get())
//                    .output(0.5f, ItemRegistry.CHARCOAL_POWDER.get(), 2)
//                    .build(consumer);
//            CreateGeography.LOGGER.info("=== Charcoal crushing recipe registered successfully ===");
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("=== Error registering charcoal crushing recipe: " + e.getMessage(), e);
//        }
//
//        CreateGeography.LOGGER.info("=== Finished registering crushing recipes ===");
//    }
//
//    private static void registerMillingRecipes(Consumer<FinishedRecipe> consumer) {
//        // 砂岩：1沙子+0.2硝石
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.MILLING.getSerializer(),
//                new ResourceLocation("creategeography", "sandstone_milling"))
//                .require(Items.SANDSTONE)
//                .output(Items.SAND)
//                .output(0.2f, ItemRegistry.NITER.get())
//                .build(consumer);
//
//        // 红砂岩：1红沙+0.2硝石
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.MILLING.getSerializer(),
//                new ResourceLocation("creategeography", "red_sandstone_milling"))
//                .require(Items.RED_SANDSTONE)
//                .output(Items.RED_SAND)
//                .output(0.2f, ItemRegistry.NITER.get())
//                .build(consumer);
//    }
//
//    private static void registerSandpaperPolishingRecipes(Consumer<FinishedRecipe> consumer) {
//        // 角闪石-磨制角闪石
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.SANDPAPER_POLISHING.getSerializer(),
//                new ResourceLocation("creategeography", "hornblende_polishing"))
//                .require(ItemRegistry.HORNBLENDE.get())
//                .output(ItemRegistry.POLISHED_HORNBLENDE.get())
//                .build(consumer);
//    }
//
//    private static void registerPressingRecipes(Consumer<FinishedRecipe> consumer) {
//        // 木纤维=纸
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.PRESSING.getSerializer(),
//                new ResourceLocation("creategeography", "wood_fiber_to_paper"))
//                .require(ItemRegistry.WOOD_FIBER.get())
//                .output(Items.PAPER)
//                .build(consumer);
//
//        // 石头：4砾石
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.PRESSING.getSerializer(),
//                new ResourceLocation("creategeography", "stone_to_gravel"))
//                .require(Items.STONE)
//                .output(Items.GRAVEL, 4)
//                .build(consumer);
//
//        // 圆石：4砾石
//        new ProcessingRecipeBuilder<>(AllRecipeTypes.PRESSING.getSerializer(),
//                new ResourceLocation("creategeography", "cobblestone_to_gravel"))
//                .require(Items.COBBLESTONE)
//                .output(Items.GRAVEL, 4)
//                .build(consumer);
//    }
//}