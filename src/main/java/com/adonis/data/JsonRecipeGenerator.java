//package com.adonis.data;
//
//import com.adonis.CreateGeography;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import net.minecraftforge.event.server.ServerStartedEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Mod.EventBusSubscriber(modid = CreateGeography.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
//public class JsonRecipeGenerator {
//
//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//
//    @SubscribeEvent
//    public static void onServerStarted(ServerStartedEvent event) {
//        try {
//            // 获取正确的项目根目录
//            Path currentDir = Paths.get(System.getProperty("user.dir"));
//            Path projectDir;
//            if (currentDir.endsWith("run")) {
//                projectDir = currentDir.getParent();
//            } else {
//                projectDir = currentDir;
//            }
//
//            Path recipesDir = projectDir.resolve("src/main/resources/data/creategeography/recipes");
//            Path tagsDir = projectDir.resolve("src/main/resources/data/creategeography/tags");
//            Files.createDirectories(recipesDir);
//            Files.createDirectories(tagsDir);
//
//            if (!Files.exists(recipesDir)) {
//                return;
//            }
//
//            // 生成标签文件
//            generateTags(tagsDir);
//
//            // 生成所有配方类型
//            generateCraftingRecipes(recipesDir);
//            generateSmeltingRecipes(recipesDir);
//            generateCreateCrushingRecipes(recipesDir);
//            generateCreateMixingRecipes(recipesDir);
//            generateCreateCompactingRecipes(recipesDir);
//            generateCreateFillingRecipes(recipesDir);
//            generateCreateWashingRecipes(recipesDir);
//            generateCreateMillingRecipes(recipesDir);
//            generateCreateSandpaperPolishingRecipes(recipesDir);
//            generateCreatePressingRecipes(recipesDir);
//            generateFragmentingRecipes(recipesDir);
//            generateSmokingRecipes(recipesDir);
//            generateCampfireRecipes(recipesDir);
//            generateCreateHeatedMixingRecipes(recipesDir);
//
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("Error generating recipes: " + e.getMessage(), e);
//        }
//    }
//
//    private static void generateTags(Path tagsDir) throws IOException {
//        Path itemTagsDir = tagsDir.resolve("items");
//        Files.createDirectories(itemTagsDir);
//
//        // 石质工具材料标签
//        JsonObject stoneToolMaterials = new JsonObject();
//        stoneToolMaterials.addProperty("replace", false);
//        JsonArray stoneToolValues = new JsonArray();
//        stoneToolValues.add("minecraft:gravel");
//        stoneToolValues.add("minecraft:deepslate");
//        stoneToolMaterials.add("values", stoneToolValues);
//        writeJsonFile(itemTagsDir.resolve("stone_tool_materials.json"), stoneToolMaterials);
//
//        // 矿物粉末标签
//        JsonObject mineralPowders = new JsonObject();
//        mineralPowders.addProperty("replace", false);
//        JsonArray powderValues = new JsonArray();
//        powderValues.add("creategeography:coal_powder");
//        powderValues.add("creategeography:charcoal_powder");
//        powderValues.add("creategeography:sulfur_powder");
//        powderValues.add("creategeography:niter_powder");
//        powderValues.add("creategeography:lapis_lazuli_powder");
//        mineralPowders.add("values", powderValues);
//        writeJsonFile(itemTagsDir.resolve("mineral_powders.json"), mineralPowders);
//
//        // 泛泥土类标签
//        JsonObject soilBlocks = new JsonObject();
//        soilBlocks.addProperty("replace", false);
//        JsonArray soilValues = new JsonArray();
//        soilValues.add("minecraft:dirt");
//        soilValues.add("minecraft:coarse_dirt");
//        soilValues.add("minecraft:rooted_dirt");
//        soilValues.add("minecraft:grass_block");
//        soilBlocks.add("values", soilValues);
//        writeJsonFile(itemTagsDir.resolve("soil_blocks.json"), soilBlocks);
//
//        // 泥土类标签
//        JsonObject dirtBlocks = new JsonObject();
//        dirtBlocks.addProperty("replace", false);
//        JsonArray dirtValues = new JsonArray();
//        dirtValues.add("minecraft:dirt");
//        dirtValues.add("minecraft:coarse_dirt");
//        dirtValues.add("minecraft:rooted_dirt");
//        dirtBlocks.add("values", dirtValues);
//        writeJsonFile(itemTagsDir.resolve("dirt_blocks.json"), dirtBlocks);
//    }
//
//    private static void generateCraftingRecipes(Path recipesDir) throws IOException {
//        Path craftingDir = recipesDir.resolve("crafting");
//        Files.createDirectories(craftingDir);
//
//        // 3木纤维=3纸
//        JsonObject woodFiberToPaper = new JsonObject();
//        woodFiberToPaper.addProperty("type", "minecraft:crafting_shapeless");
//        woodFiberToPaper.addProperty("category", "misc");
//        woodFiberToPaper.add("ingredients", createJsonArray(createItemIngredient("creategeography:wood_fiber", 3)));
//        woodFiberToPaper.add("result", createItemResult("minecraft:paper", 3));
//        writeJsonFile(craftingDir.resolve("paper_from_wood_fiber.json"), woodFiberToPaper);
//
//        // 1硫磺粉1木炭粉6硝石粉=8火药
//        JsonObject gunpowderFromPowders = new JsonObject();
//        gunpowderFromPowders.addProperty("type", "minecraft:crafting_shapeless");
//        gunpowderFromPowders.addProperty("category", "misc");
//        JsonArray gunpowderIngredients = new JsonArray();
//        gunpowderIngredients.add(createItemIngredient("creategeography:sulfur_powder"));
//        gunpowderIngredients.add(createItemIngredient("creategeography:charcoal_powder"));
//        for (int i = 0; i < 6; i++) {
//            gunpowderIngredients.add(createItemIngredient("creategeography:niter_powder"));
//        }
//        gunpowderFromPowders.add("ingredients", gunpowderIngredients);
//        gunpowderFromPowders.add("result", createItemResult("minecraft:gunpowder", 8));
//        writeJsonFile(craftingDir.resolve("gunpowder_from_powders.json"), gunpowderFromPowders);
//
//        // 4盐-盐块
//        JsonObject saltBlock = new JsonObject();
//        saltBlock.addProperty("type", "minecraft:crafting_shaped");
//        saltBlock.addProperty("category", "building_blocks");
//        saltBlock.add("pattern", createJsonArray("SS", "SS"));
//        JsonObject saltKey = new JsonObject();
//        saltKey.add("S", createItemIngredient("creategeography:salt"));
//        saltBlock.add("key", saltKey);
//        saltBlock.add("result", createItemResult("creategeography:salt_block"));
//        writeJsonFile(craftingDir.resolve("salt_block_from_salt.json"), saltBlock);
//
//        // 4沙尘-沙子
//        JsonObject sandFromDust = new JsonObject();
//        sandFromDust.addProperty("type", "minecraft:crafting_shaped");
//        sandFromDust.addProperty("category", "building_blocks");
//        sandFromDust.add("pattern", createJsonArray("SS", "SS"));
//        JsonObject sandKey = new JsonObject();
//        sandKey.add("S", createItemIngredient("creategeography:sand_dust"));
//        sandFromDust.add("key", sandKey);
//        sandFromDust.add("result", createItemResult("minecraft:sand"));
//        writeJsonFile(craftingDir.resolve("sand_from_dust.json"), sandFromDust);
//
//        // 4红沙尘-红沙
//        JsonObject redSandFromDust = new JsonObject();
//        redSandFromDust.addProperty("type", "minecraft:crafting_shaped");
//        redSandFromDust.addProperty("category", "building_blocks");
//        redSandFromDust.add("pattern", createJsonArray("SS", "SS"));
//        JsonObject redSandKey = new JsonObject();
//        redSandKey.add("S", createItemIngredient("creategeography:red_sand_dust"));
//        redSandFromDust.add("key", redSandKey);
//        redSandFromDust.add("result", createItemResult("minecraft:red_sand"));
//        writeJsonFile(craftingDir.resolve("red_sand_from_dust.json"), redSandFromDust);
//
//        // 4灰烬-凝灰岩
//        JsonObject tuffFromAsh = new JsonObject();
//        tuffFromAsh.addProperty("type", "minecraft:crafting_shaped");
//        tuffFromAsh.addProperty("category", "building_blocks");
//        tuffFromAsh.add("pattern", createJsonArray("AA", "AA"));
//        JsonObject tuffKey = new JsonObject();
//        tuffKey.add("A", createItemIngredient("creategeography:ash"));
//        tuffFromAsh.add("key", tuffKey);
//        tuffFromAsh.add("result", createItemResult("minecraft:tuff"));
//        writeJsonFile(craftingDir.resolve("tuff_from_ash.json"), tuffFromAsh);
//
//        // 3灰烬1沙尘-沙砾
//        JsonObject gravelFromAshAndSand = new JsonObject();
//        gravelFromAshAndSand.addProperty("type", "minecraft:crafting_shapeless");
//        gravelFromAshAndSand.addProperty("category", "building_blocks");
//        JsonArray gravelIngredients = new JsonArray();
//        for (int i = 0; i < 3; i++) {
//            gravelIngredients.add(createItemIngredient("creategeography:ash"));
//        }
//        gravelIngredients.add(createItemIngredient("creategeography:sand_dust"));
//        gravelFromAshAndSand.add("ingredients", gravelIngredients);
//        gravelFromAshAndSand.add("result", createItemResult("minecraft:gravel"));
//        writeJsonFile(craftingDir.resolve("gravel_from_ash_and_sand.json"), gravelFromAshAndSand);
//
//        // 4土坷-泥土
//        JsonObject dirtFromClods = new JsonObject();
//        dirtFromClods.addProperty("type", "minecraft:crafting_shaped");
//        dirtFromClods.addProperty("category", "building_blocks");
//        dirtFromClods.add("pattern", createJsonArray("CC", "CC"));
//        JsonObject dirtKey = new JsonObject();
//        dirtKey.add("C", createItemIngredient("creategeography:dirt_clod"));
//        dirtFromClods.add("key", dirtKey);
//        dirtFromClods.add("result", createItemResult("minecraft:dirt"));
//        writeJsonFile(craftingDir.resolve("dirt_from_clods.json"), dirtFromClods);
//    }
//
//    private static void generateSmeltingRecipes(Path recipesDir) throws IOException {
//        Path smeltingDir = recipesDir.resolve("smelting");
//        Files.createDirectories(smeltingDir);
//
//        // 木纤维-木炭粉
//        JsonObject charcoalPowderFromFiber = createSmeltingRecipe(
//                "creategeography:wood_fiber", "creategeography:charcoal_powder", 0.1f, 200);
//        writeJsonFile(smeltingDir.resolve("charcoal_powder_from_wood_fiber.json"), charcoalPowderFromFiber);
//
//        // 泥巴=泥土
//        JsonObject dirtFromMud = createSmeltingRecipe(
//                "minecraft:mud", "minecraft:dirt", 0.1f, 200);
//        writeJsonFile(smeltingDir.resolve("dirt_from_mud_smelting.json"), dirtFromMud);
//
//        // 盐碱泥巴=盐碱土
//        JsonObject salineSoilFromMud = createSmeltingRecipe(
//                "creategeography:saline_mud", "creategeography:saline_dirt", 0.1f, 200);
//        writeJsonFile(smeltingDir.resolve("saline_soil_from_mud_smelting.json"), salineSoilFromMud);
//
//        // 冻土=泥巴
//        JsonObject mudFromFrozenSoil = createSmeltingRecipe(
//                "creategeography:frozen_soil", "minecraft:mud", 0.1f, 200);
//        writeJsonFile(smeltingDir.resolve("mud_from_frozen_soil_smelting.json"), mudFromFrozenSoil);
//    }
//
//    private static void generateCampfireRecipes(Path recipesDir) throws IOException {
//        Path campfireDir = recipesDir.resolve("campfire");
//        Files.createDirectories(campfireDir);
//
//        // 泥巴=泥土
//        JsonObject dirtFromMud = createCampfireRecipe(
//                "minecraft:mud", "minecraft:dirt", 0.1f, 600);
//        writeJsonFile(campfireDir.resolve("dirt_from_mud_campfire.json"), dirtFromMud);
//
//        // 盐碱泥巴=盐碱土
//        JsonObject salineSoilFromMud = createCampfireRecipe(
//                "creategeography:saline_mud", "creategeography:saline_dirt", 0.1f, 600);
//        writeJsonFile(campfireDir.resolve("saline_soil_from_mud_campfire.json"), salineSoilFromMud);
//
//        // 冻土=泥巴
//        JsonObject mudFromFrozenSoil = createCampfireRecipe(
//                "creategeography:frozen_soil", "minecraft:mud", 0.1f, 600);
//        writeJsonFile(campfireDir.resolve("mud_from_frozen_soil_campfire.json"), mudFromFrozenSoil);
//    }
//
//    private static void generateSmokingRecipes(Path recipesDir) throws IOException {
//        Path smokingDir = recipesDir.resolve("smoking");
//        Files.createDirectories(smokingDir);
//
//        // 泥巴=泥土
//        JsonObject dirtFromMud = createSmokingRecipe(
//                "minecraft:mud", "minecraft:dirt", 0.1f, 100);
//        writeJsonFile(smokingDir.resolve("dirt_from_mud_smoking.json"), dirtFromMud);
//
//        // 盐碱泥巴=盐碱土
//        JsonObject salineSoilFromMud = createSmokingRecipe(
//                "creategeography:saline_mud", "creategeography:saline_dirt", 0.1f, 100);
//        writeJsonFile(smokingDir.resolve("saline_soil_from_mud_smoking.json"), salineSoilFromMud);
//
//        // 冻土=泥巴
//        JsonObject mudFromFrozenSoil = createSmokingRecipe(
//                "creategeography:frozen_soil", "minecraft:mud", 0.1f, 100);
//        writeJsonFile(smokingDir.resolve("mud_from_frozen_soil_smoking.json"), mudFromFrozenSoil);
//    }
//
//    private static void generateCreateCrushingRecipes(Path recipesDir) throws IOException {
//        Path crushingDir = recipesDir.resolve("crushing");
//        Files.createDirectories(crushingDir);
//
//        // 煤炭：1+0.5*2煤粉
//        JsonObject coalCrushing = new JsonObject();
//        coalCrushing.addProperty("type", "create:crushing");
//        coalCrushing.add("ingredients", createJsonArray(createItemIngredient("minecraft:coal")));
//        JsonArray coalResults = new JsonArray();
//        coalResults.add(createItemResult("creategeography:coal_powder"));
//        JsonObject coalResult2 = createItemResult("creategeography:coal_powder", 2);
//        coalResult2.addProperty("chance", 0.5);
//        coalResults.add(coalResult2);
//        coalCrushing.add("results", coalResults);
//        coalCrushing.addProperty("processingTime", 150);
//        writeJsonFile(crushingDir.resolve("coal_crushing.json"), coalCrushing);
//
//        // 木炭：1+0.5*2木炭粉
//        JsonObject charcoalCrushing = new JsonObject();
//        charcoalCrushing.addProperty("type", "create:crushing");
//        charcoalCrushing.add("ingredients", createJsonArray(createItemIngredient("minecraft:charcoal")));
//        JsonArray charcoalResults = new JsonArray();
//        charcoalResults.add(createItemResult("creategeography:charcoal_powder"));
//        JsonObject charcoalResult2 = createItemResult("creategeography:charcoal_powder", 2);
//        charcoalResult2.addProperty("chance", 0.5);
//        charcoalResults.add(coalResult2);
//        charcoalCrushing.add("results", charcoalResults);
//        charcoalCrushing.addProperty("processingTime", 150);
//        writeJsonFile(crushingDir.resolve("charcoal_crushing.json"), charcoalCrushing);
//    }
//
//    private static void generateCreateMixingRecipes(Path recipesDir) throws IOException {
//        Path mixingDir = recipesDir.resolve("mixing");
//        Files.createDirectories(mixingDir);
//
//        // 250mb水+盐=250mb盐水
//        JsonObject saltToBrine = new JsonObject();
//        saltToBrine.addProperty("type", "create:mixing");
//        saltToBrine.add("ingredients", createJsonArray(createItemIngredient("creategeography:salt")));
//        saltToBrine.add("fluidIngredients", createJsonArray(createFluidIngredient("minecraft:water", 250)));
//        saltToBrine.add("results", createJsonArray());
//        saltToBrine.add("fluidResults", createJsonArray(createFluidResult("creategeography:brine", 250)));
//        saltToBrine.addProperty("processingTime", 100);
//        writeJsonFile(mixingDir.resolve("salt_to_brine.json"), saltToBrine);
//
//        // 1硫磺粉1木炭粉6硝石粉=8火药
//        JsonObject gunpowderMixing = new JsonObject();
//        gunpowderMixing.addProperty("type", "create:mixing");
//        JsonArray gunpowderIngredients = new JsonArray();
//        gunpowderIngredients.add(createItemIngredient("creategeography:sulfur_powder"));
//        gunpowderIngredients.add(createItemIngredient("creategeography:charcoal_powder"));
//        for (int i = 0; i < 6; i++) {
//            gunpowderIngredients.add(createItemIngredient("creategeography:niter_powder"));
//        }
//        gunpowderMixing.add("ingredients", gunpowderIngredients);
//        gunpowderMixing.add("results", createJsonArray(createItemResult("minecraft:gunpowder", 8)));
//        gunpowderMixing.addProperty("processingTime", 200);
//        writeJsonFile(mixingDir.resolve("gunpowder_mixing.json"), gunpowderMixing);
//
//        // 硝石+1000mb水=冰
//        JsonObject niterToIce = new JsonObject();
//        niterToIce.addProperty("type", "create:mixing");
//        niterToIce.add("ingredients", createJsonArray(createItemIngredient("creategeography:niter")));
//        niterToIce.add("fluidIngredients", createJsonArray(createFluidIngredient("minecraft:water", 1000)));
//        niterToIce.add("results", createJsonArray(createItemResult("minecraft:ice")));
//        niterToIce.addProperty("processingTime", 200);
//        writeJsonFile(mixingDir.resolve("niter_to_ice.json"), niterToIce);
//
//        // 硝石粉+1000mb水=冰
//        JsonObject niterPowderToIce = new JsonObject();
//        niterPowderToIce.addProperty("type", "create:mixing");
//        niterPowderToIce.add("ingredients", createJsonArray(createItemIngredient("creategeography:niter_powder")));
//        niterPowderToIce.add("fluidIngredients", createJsonArray(createFluidIngredient("minecraft:water", 1000)));
//        niterPowderToIce.add("results", createJsonArray(createItemResult("minecraft:ice")));
//        niterPowderToIce.addProperty("processingTime", 200);
//        writeJsonFile(mixingDir.resolve("niter_powder_to_ice.json"), niterPowderToIce);
//
//        // 250mb盐水+泥土=盐碱泥巴
//        JsonObject dirtTosalineMud = new JsonObject();
//        dirtTosalineMud.addProperty("type", "create:mixing");
//        dirtTosalineMud.add("ingredients", createJsonArray(createItemIngredient("minecraft:dirt")));
//        dirtTosalineMud.add("fluidIngredients", createJsonArray(createFluidIngredient("creategeography:brine", 250)));
//        dirtTosalineMud.add("results", createJsonArray(createItemResult("creategeography:saline_mud")));
//        dirtTosalineMud.addProperty("processingTime", 100);
//        writeJsonFile(mixingDir.resolve("dirt_to_saline_mud.json"), dirtTosalineMud);
//
//        // 250mb盐水+砂土=盐碱泥巴
//        JsonObject coarseDirtToSalineMud = new JsonObject();
//        coarseDirtToSalineMud.addProperty("type", "create:mixing");
//        coarseDirtToSalineMud.add("ingredients", createJsonArray(createItemIngredient("minecraft:coarse_dirt")));
//        coarseDirtToSalineMud.add("fluidIngredients", createJsonArray(createFluidIngredient("creategeography:brine", 250)));
//        coarseDirtToSalineMud.add("results", createJsonArray(createItemResult("creategeography:saline_mud")));
//        coarseDirtToSalineMud.addProperty("processingTime", 100);
//        writeJsonFile(mixingDir.resolve("coarse_dirt_to_saline_mud.json"), coarseDirtToSalineMud);
//
//        // 250mb盐水+缠根泥土=盐碱泥巴
//        JsonObject rootedDirtToSalineMud = new JsonObject();
//        rootedDirtToSalineMud.addProperty("type", "create:mixing");
//        rootedDirtToSalineMud.add("ingredients", createJsonArray(createItemIngredient("minecraft:rooted_dirt")));
//        rootedDirtToSalineMud.add("fluidIngredients", createJsonArray(createFluidIngredient("creategeography:brine", 250)));
//        rootedDirtToSalineMud.add("results", createJsonArray(createItemResult("creategeography:saline_mud")));
//        rootedDirtToSalineMud.addProperty("processingTime", 100);
//        writeJsonFile(mixingDir.resolve("rooted_dirt_to_saline_mud.json"), rootedDirtToSalineMud);
//
//        // 250mb盐水+泥巴=250mb盐水+盐碱泥巴
//        JsonObject mudToSalineMud = new JsonObject();
//        mudToSalineMud.addProperty("type", "create:mixing");
//        mudToSalineMud.add("ingredients", createJsonArray(createItemIngredient("minecraft:mud")));
//        mudToSalineMud.add("fluidIngredients", createJsonArray(createFluidIngredient("creategeography:brine", 250)));
//        mudToSalineMud.add("results", createJsonArray(createItemResult("creategeography:saline_mud")));
//        mudToSalineMud.add("fluidResults", createJsonArray(createFluidResult("creategeography:brine", 250)));
//        mudToSalineMud.addProperty("processingTime", 100);
//        writeJsonFile(mixingDir.resolve("mud_to_saline_mud.json"), mudToSalineMud);
//    }
//
//    private static void generateCreateHeatedMixingRecipes(Path recipesDir) throws IOException {
//        Path heatedMixingDir = recipesDir.resolve("heated_mixing");
//        Files.createDirectories(heatedMixingDir);
//
//        // 泥巴+250mb水（加热混合）
//        JsonObject mudHeatedMixing = new JsonObject();
//        mudHeatedMixing.addProperty("type", "create:mixing");
//        mudHeatedMixing.addProperty("heatRequirement", "heated");
//        mudHeatedMixing.add("ingredients", createJsonArray(createItemIngredient("minecraft:mud")));
//        mudHeatedMixing.add("fluidIngredients", createJsonArray(createFluidIngredient("minecraft:water", 250)));
//        mudHeatedMixing.add("results", createJsonArray(createItemResult("minecraft:mud")));
//        mudHeatedMixing.addProperty("processingTime", 100);
//        writeJsonFile(heatedMixingDir.resolve("mud_heated_mixing.json"), mudHeatedMixing);
//    }
//
//    private static void generateCreateCompactingRecipes(Path recipesDir) throws IOException {
//        Path compactingDir = recipesDir.resolve("compacting");
//        Files.createDirectories(compactingDir);
//
//        // 海带+250mb水=250mb盐水+木纤维
//        JsonObject kelpToBrine = new JsonObject();
//        kelpToBrine.addProperty("type", "create:compacting");
//        kelpToBrine.add("ingredients", createJsonArray(createItemIngredient("minecraft:kelp")));
//        kelpToBrine.add("fluidIngredients", createJsonArray(createFluidIngredient("minecraft:water", 250)));
//        kelpToBrine.add("results", createJsonArray(createItemResult("creategeography:wood_fiber")));
//        kelpToBrine.add("fluidResults", createJsonArray(createFluidResult("creategeography:brine", 250)));
//        kelpToBrine.addProperty("processingTime", 100);
//        writeJsonFile(compactingDir.resolve("kelp_to_brine.json"), kelpToBrine);
//
//        // 红树根+250mb水=250mb盐水+木纤维
//        JsonObject mangroveRootsToBrine = new JsonObject();
//        mangroveRootsToBrine.addProperty("type", "create:compacting");
//        mangroveRootsToBrine.add("ingredients", createJsonArray(createItemIngredient("minecraft:mangrove_roots")));
//        mangroveRootsToBrine.add("fluidIngredients", createJsonArray(createFluidIngredient("minecraft:water", 250)));
//        mangroveRootsToBrine.add("results", createJsonArray(createItemResult("creategeography:wood_fiber")));
//        mangroveRootsToBrine.add("fluidResults", createJsonArray(createFluidResult("creategeography:brine", 250)));
//        mangroveRootsToBrine.addProperty("processingTime", 100);
//        writeJsonFile(compactingDir.resolve("mangrove_roots_to_brine.json"), mangroveRootsToBrine);
//
//        // 4沙尘-沙子
//        JsonObject sandDustToSand = new JsonObject();
//        sandDustToSand.addProperty("type", "create:compacting");
//        JsonArray sandDustIngredients = new JsonArray();
//        for (int i = 0; i < 4; i++) {
//            sandDustIngredients.add(createItemIngredient("creategeography:sand_dust"));
//        }
//        sandDustToSand.add("ingredients", sandDustIngredients);
//        sandDustToSand.add("results", createJsonArray(createItemResult("minecraft:sand")));
//        sandDustToSand.addProperty("processingTime", 100);
//        writeJsonFile(compactingDir.resolve("sand_dust_to_sand.json"), sandDustToSand);
//
//        // 4红沙尘-红沙
//        JsonObject redSandDustToRedSand = new JsonObject();
//        redSandDustToRedSand.addProperty("type", "create:compacting");
//        JsonArray redSandDustIngredients = new JsonArray();
//        for (int i = 0; i < 4; i++) {
//            redSandDustIngredients.add(createItemIngredient("creategeography:red_sand_dust"));
//        }
//        redSandDustToRedSand.add("ingredients", redSandDustIngredients);
//        redSandDustToRedSand.add("results", createJsonArray(createItemResult("minecraft:red_sand")));
//        redSandDustToRedSand.addProperty("processingTime", 100);
//        writeJsonFile(compactingDir.resolve("red_sand_dust_to_red_sand.json"), redSandDustToRedSand);
//
//        // 原木类-4木纤维
//        String[] logTypes = {"oak_log", "birch_log", "spruce_log", "jungle_log", "acacia_log", "dark_oak_log", "mangrove_log", "cherry_log"};
//        for (String logType : logTypes) {
//            JsonObject logToWoodFiber = new JsonObject();
//            logToWoodFiber.addProperty("type", "create:compacting");
//            logToWoodFiber.add("ingredients", createJsonArray(createItemIngredient("minecraft:" + logType)));
//            logToWoodFiber.add("results", createJsonArray(createItemResult("creategeography:wood_fiber", 4)));
//            logToWoodFiber.addProperty("processingTime", 100);
//            writeJsonFile(compactingDir.resolve(logType + "_to_wood_fiber.json"), logToWoodFiber);
//        }
//
//        // 去皮原木类-4木纤维
//        String[] strippedLogTypes = {"stripped_oak_log", "stripped_birch_log", "stripped_spruce_log", "stripped_jungle_log", "stripped_acacia_log", "stripped_dark_oak_log", "stripped_mangrove_log", "stripped_cherry_log"};
//        for (String strippedLogType : strippedLogTypes) {
//            JsonObject strippedLogToWoodFiber = new JsonObject();
//            strippedLogToWoodFiber.addProperty("type", "create:compacting");
//            strippedLogToWoodFiber.add("ingredients", createJsonArray(createItemIngredient("minecraft:" + strippedLogType)));
//            strippedLogToWoodFiber.add("results", createJsonArray(createItemResult("creategeography:wood_fiber", 4)));
//            strippedLogToWoodFiber.addProperty("processingTime", 100);
//            writeJsonFile(compactingDir.resolve(strippedLogType + "_to_wood_fiber.json"), strippedLogToWoodFiber);
//        }
//    }
//
//    private static void generateCreateFillingRecipes(Path recipesDir) throws IOException {
//        Path fillingDir = recipesDir.resolve("filling");
//        Files.createDirectories(fillingDir);
//
//        // 250mb盐水+玻璃瓶=盐水瓶
//        JsonObject brineBottle = new JsonObject();
//        brineBottle.addProperty("type", "create:filling");
//        brineBottle.add("ingredients", createJsonArray(createItemIngredient("minecraft:glass_bottle")));
//        brineBottle.add("fluidIngredients", createJsonArray(createFluidIngredient("creategeography:brine", 250)));
//        brineBottle.add("results", createJsonArray(createItemResult("creategeography:brine_bottle")));
//        brineBottle.addProperty("processingTime", 100);
//        writeJsonFile(fillingDir.resolve("brine_bottle.json"), brineBottle);
//    }
//
//    private static void generateCreateWashingRecipes(Path recipesDir) throws IOException {
//        Path washingDir = recipesDir.resolve("washing");
//        Files.createDirectories(washingDir);
//
//        // 石英砂-0.5下界石英
//        JsonObject quartzSandWashing = new JsonObject();
//        quartzSandWashing.addProperty("type", "create:splashing");
//        quartzSandWashing.add("ingredients", createJsonArray(createItemIngredient("creategeography:quartz_sand")));
//        JsonObject quartzResult = createItemResult("minecraft:quartz");
//        quartzResult.addProperty("chance", 0.5);
//        quartzSandWashing.add("results", createJsonArray(quartzResult));
//        quartzSandWashing.addProperty("processingTime", 100);
//        writeJsonFile(washingDir.resolve("quartz_sand_washing.json"), quartzSandWashing);
//    }
//
//    private static void generateCreateMillingRecipes(Path recipesDir) throws IOException {
//        Path millingDir = recipesDir.resolve("milling");
//        Files.createDirectories(millingDir);
//
//        // 砂岩：1沙子+0.2硝石（覆盖原配方）
//        JsonObject sandstoneMilling = new JsonObject();
//        sandstoneMilling.addProperty("type", "create:milling");
//        sandstoneMilling.add("ingredients", createJsonArray(createItemIngredient("minecraft:sandstone")));
//        JsonArray sandstoneResults = new JsonArray();
//        sandstoneResults.add(createItemResult("minecraft:sand"));
//        JsonObject niterResult = createItemResult("creategeography:niter");
//        niterResult.addProperty("chance", 0.2);
//        sandstoneResults.add(niterResult);
//        sandstoneMilling.add("results", sandstoneResults);
//        sandstoneMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("sandstone_milling.json"), sandstoneMilling);
//
//        // 红砂岩：1红沙+0.2硝石
//        JsonObject redSandstoneMilling = new JsonObject();
//        redSandstoneMilling.addProperty("type", "create:milling");
//        redSandstoneMilling.add("ingredients", createJsonArray(createItemIngredient("minecraft:red_sandstone")));
//        JsonArray redSandstoneResults = new JsonArray();
//        redSandstoneResults.add(createItemResult("minecraft:red_sand"));
//        JsonObject redNiterResult = createItemResult("creategeography:niter");
//        redNiterResult.addProperty("chance", 0.2);
//        redSandstoneResults.add(redNiterResult);
//        redSandstoneMilling.add("results", redSandstoneResults);
//        redSandstoneMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("red_sandstone_milling.json"), redSandstoneMilling);
//
//// 硝石：0.75硝石粉
//        JsonObject niterMilling = new JsonObject();
//        niterMilling.addProperty("type", "create:milling");
//        niterMilling.add("ingredients", createJsonArray(createItemIngredient("creategeography:niter")));
//        JsonObject niterPowderResult = createItemResult("creategeography:niter_powder");
//        niterPowderResult.addProperty("chance", 0.75);
//        niterMilling.add("results", createJsonArray(niterPowderResult));
//        niterMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("niter_milling.json"), niterMilling);
//
//        // 凝灰岩：3灰烬1硫磺粉
//        JsonObject tuffMilling = new JsonObject();
//        tuffMilling.addProperty("type", "create:milling");
//        tuffMilling.add("ingredients", createJsonArray(createItemIngredient("minecraft:tuff")));
//        JsonArray tuffResults = new JsonArray();
//        tuffResults.add(createItemResult("creategeography:ash", 3));
//        tuffResults.add(createItemResult("creategeography:sulfur_powder"));
//        tuffMilling.add("results", tuffResults);
//        tuffMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("tuff_milling.json"), tuffMilling);
//
//        // 煤炭研磨：1+0.5*2煤粉
//        JsonObject coalMilling = new JsonObject();
//        coalMilling.addProperty("type", "create:milling");
//        coalMilling.add("ingredients", createJsonArray(createItemIngredient("minecraft:coal")));
//        JsonArray coalResults = new JsonArray();
//        coalResults.add(createItemResult("creategeography:coal_powder"));
//        JsonObject coalResult2 = createItemResult("creategeography:coal_powder", 2);
//        coalResult2.addProperty("chance", 0.5);
//        coalResults.add(coalResult2);
//        coalMilling.add("results", coalResults);
//        coalMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("coal_milling.json"), coalMilling);
//
//        // 木炭研磨：1+0.5*2木炭粉
//        JsonObject charcoalMilling = new JsonObject();
//        charcoalMilling.addProperty("type", "create:milling");
//        charcoalMilling.add("ingredients", createJsonArray(createItemIngredient("minecraft:charcoal")));
//        JsonArray charcoalResults = new JsonArray();
//        charcoalResults.add(createItemResult("creategeography:charcoal_powder"));
//        JsonObject charcoalResult2 = createItemResult("creategeography:charcoal_powder", 2);
//        charcoalResult2.addProperty("chance", 0.5);
//        charcoalResults.add(charcoalResult2);
//        charcoalMilling.add("results", charcoalResults);
//        charcoalMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("charcoal_milling.json"), charcoalMilling);
//
//        // 正长石-红沙尘
//        JsonObject orthoclaseMilling = new JsonObject();
//        orthoclaseMilling.addProperty("type", "create:milling");
//        orthoclaseMilling.add("ingredients", createJsonArray(createItemIngredient("creategeography:orthaclase")));
//        orthoclaseMilling.add("results", createJsonArray(createItemResult("creategeography:red_sand_dust")));
//        orthoclaseMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("orthaclase_milling.json"), orthoclaseMilling);
//
//        // 斜长石-灰烬
//        JsonObject plagioclaseMilling = new JsonObject();
//        plagioclaseMilling.addProperty("type", "create:milling");
//        plagioclaseMilling.add("ingredients", createJsonArray(createItemIngredient("creategeography:plagioclase")));
//        plagioclaseMilling.add("results", createJsonArray(createItemResult("creategeography:ash")));
//        plagioclaseMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("plagioclase_milling.json"), plagioclaseMilling);
//
//// 砾石-沙尘
//        JsonObject gravelMilling = new JsonObject();
//        gravelMilling.addProperty("type", "create:milling");
//        gravelMilling.add("ingredients", createJsonArray(createItemIngredient("minecraft:gravel")));
//        gravelMilling.add("results", createJsonArray(createItemResult("creategeography:sand_dust")));
//        gravelMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("gravel_milling.json"), gravelMilling);
//
//        // 深板岩砾石-沙尘
//        JsonObject deepslateGravelMilling = new JsonObject();
//        deepslateGravelMilling.addProperty("type", "create:milling");
//        deepslateGravelMilling.add("ingredients", createJsonArray(createItemIngredient("minecraft:cobbled_deepslate")));
//        deepslateGravelMilling.add("results", createJsonArray(createItemResult("creategeography:sand_dust")));
//        deepslateGravelMilling.addProperty("processingTime", 100);
//        writeJsonFile(millingDir.resolve("deepslate_gravel_milling.json"), deepslateGravelMilling);
//    }
//
//    private static void generateCreateSandpaperPolishingRecipes(Path recipesDir) throws IOException {
//        Path sandpaperDir = recipesDir.resolve("sandpaper_polishing");
//        Files.createDirectories(sandpaperDir);
//
//        // 角闪石-磨制角闪石
//        JsonObject hornblendePolishing = new JsonObject();
//        hornblendePolishing.addProperty("type", "create:sandpaper_polishing");
//        hornblendePolishing.add("ingredients", createJsonArray(createItemIngredient("creategeography:hornblende")));
//        hornblendePolishing.add("results", createJsonArray(createItemResult("creategeography:polished_hornblende")));
//        writeJsonFile(sandpaperDir.resolve("hornblende_polishing.json"), hornblendePolishing);
//    }
//
//    private static void generateCreatePressingRecipes(Path recipesDir) throws IOException {
//        Path pressingDir = recipesDir.resolve("pressing");
//        Files.createDirectories(pressingDir);
//
//        // 木纤维=纸
//        JsonObject woodFiberToPaper = new JsonObject();
//        woodFiberToPaper.addProperty("type", "create:pressing");
//        woodFiberToPaper.add("ingredients", createJsonArray(createItemIngredient("creategeography:wood_fiber")));
//        woodFiberToPaper.add("results", createJsonArray(createItemResult("minecraft:paper")));
//        woodFiberToPaper.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("wood_fiber_to_paper.json"), woodFiberToPaper);
//
//        // 石头：4砾石
//        JsonObject stoneToGravel = new JsonObject();
//        stoneToGravel.addProperty("type", "create:pressing");
//        stoneToGravel.add("ingredients", createJsonArray(createItemIngredient("minecraft:stone")));
//        stoneToGravel.add("results", createJsonArray(createItemResult("minecraft:gravel", 4)));
//        stoneToGravel.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("stone_to_gravel.json"), stoneToGravel);
//
//        // 圆石：4砾石
//        JsonObject cobblestoneToGravel = new JsonObject();
//        cobblestoneToGravel.addProperty("type", "create:pressing");
//        cobblestoneToGravel.add("ingredients", createJsonArray(createItemIngredient("minecraft:cobblestone")));
//        cobblestoneToGravel.add("results", createJsonArray(createItemResult("minecraft:gravel", 4)));
//        cobblestoneToGravel.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("cobblestone_to_gravel.json"), cobblestoneToGravel);
//
//        // 深板岩：4深板岩砾石
//        JsonObject deepslateToGravel = new JsonObject();
//        deepslateToGravel.addProperty("type", "create:pressing");
//        deepslateToGravel.add("ingredients", createJsonArray(createItemIngredient("minecraft:deepslate")));
//        deepslateToGravel.add("results", createJsonArray(createItemResult("minecraft:cobbled_deepslate", 4)));
//        deepslateToGravel.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("deepslate_to_gravel.json"), deepslateToGravel);
//
//        // 深板岩圆石：4深板岩砾石
//        JsonObject cobbledDeepslateToGravel = new JsonObject();
//        cobbledDeepslateToGravel.addProperty("type", "create:pressing");
//        cobbledDeepslateToGravel.add("ingredients", createJsonArray(createItemIngredient("minecraft:cobbled_deepslate")));
//        cobbledDeepslateToGravel.add("results", createJsonArray(createItemResult("minecraft:cobbled_deepslate", 4)));
//        cobbledDeepslateToGravel.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("cobbled_deepslate_to_gravel.json"), cobbledDeepslateToGravel);
//
//        // 泛泥土类：4土坷（使用标签）
//        JsonObject soilToDirtClods = new JsonObject();
//        soilToDirtClods.addProperty("type", "create:pressing");
//        JsonObject soilTag = new JsonObject();
//        soilTag.addProperty("tag", "creategeography:soil_blocks");
//        soilToDirtClods.add("ingredients", createJsonArray(soilTag));
//        soilToDirtClods.add("results", createJsonArray(createItemResult("creategeography:dirt_clod", 4)));
//        soilToDirtClods.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("soil_to_dirt_clods.json"), soilToDirtClods);
//
//        // 安山岩：1砾石3斜长石
//        JsonObject andesitePressing = new JsonObject();
//        andesitePressing.addProperty("type", "create:pressing");
//        andesitePressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:andesite")));
//        JsonArray andesiteResults = new JsonArray();
//        andesiteResults.add(createItemResult("minecraft:gravel"));
//        andesiteResults.add(createItemResult("creategeography:plagioclase", 3));
//        andesitePressing.add("results", andesiteResults);
//        andesitePressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("andesite_pressing.json"), andesitePressing);
//
//        // 花岗岩：1砾石2正长石1斜长石
//        JsonObject granitePressing = new JsonObject();
//        granitePressing.addProperty("type", "create:pressing");
//        granitePressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:granite")));
//        JsonArray graniteResults = new JsonArray();
//        graniteResults.add(createItemResult("minecraft:gravel"));
//        graniteResults.add(createItemResult("creategeography:orthaclase", 2));
//        graniteResults.add(createItemResult("creategeography:plagioclase"));
//        granitePressing.add("results", graniteResults);
//        granitePressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("granite_pressing.json"), granitePressing);
//
//        // 闪长岩：1砾石1斜长石2石英砂
//        JsonObject dioritePressing = new JsonObject();
//        dioritePressing.addProperty("type", "create:pressing");
//        dioritePressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:diorite")));
//        JsonArray dioriteResults = new JsonArray();
//        dioriteResults.add(createItemResult("minecraft:gravel"));
//        dioriteResults.add(createItemResult("creategeography:plagioclase"));
//        dioriteResults.add(createItemResult("creategeography:quartz_sand", 2));
//        dioritePressing.add("results", dioriteResults);
//        dioritePressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("diorite_pressing.json"), dioritePressing);
//
//        // 沙砾：1沙尘3灰烬
//        JsonObject gravelPressing = new JsonObject();
//        gravelPressing.addProperty("type", "create:pressing");
//        gravelPressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:gravel")));
//        JsonArray gravelResults = new JsonArray();
//        gravelResults.add(createItemResult("creategeography:sand_dust"));
//        gravelResults.add(createItemResult("creategeography:ash", 3));
//        gravelPressing.add("results", gravelResults);
//        gravelPressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("gravel_pressing.json"), gravelPressing);
//
//        // 黏土：4黏土球
//        JsonObject clayPressing = new JsonObject();
//        clayPressing.addProperty("type", "create:pressing");
//        clayPressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:clay")));
//        clayPressing.add("results", createJsonArray(createItemResult("minecraft:clay_ball", 4)));
//        clayPressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("clay_pressing.json"), clayPressing);
//
//        // 沙子：4沙尘
//        JsonObject sandPressing = new JsonObject();
//        sandPressing.addProperty("type", "create:pressing");
//        sandPressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:sand")));
//        sandPressing.add("results", createJsonArray(createItemResult("creategeography:sand_dust", 4)));
//        sandPressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("sand_pressing.json"), sandPressing);
//
//        // 砂岩：1沙子
//        JsonObject sandstonePressing = new JsonObject();
//        sandstonePressing.addProperty("type", "create:pressing");
//        sandstonePressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:sandstone")));
//        sandstonePressing.add("results", createJsonArray(createItemResult("minecraft:sand")));
//        sandstonePressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("sandstone_pressing.json"), sandstonePressing);
//
//        // 红沙：4红沙尘
//        JsonObject redSandPressing = new JsonObject();
//        redSandPressing.addProperty("type", "create:pressing");
//        redSandPressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:red_sand")));
//        redSandPressing.add("results", createJsonArray(createItemResult("creategeography:red_sand_dust", 4)));
//        redSandPressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("red_sand_pressing.json"), redSandPressing);
//
//        // 红砂岩：1红沙
//        JsonObject redSandstonePressing = new JsonObject();
//        redSandstonePressing.addProperty("type", "create:pressing");
//        redSandstonePressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:red_sandstone")));
//        redSandstonePressing.add("results", createJsonArray(createItemResult("minecraft:red_sand")));
//        redSandstonePressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("red_sandstone_pressing.json"), redSandstonePressing);
//
//        // 凝灰岩：4灰烬
//        JsonObject tuffPressing = new JsonObject();
//        tuffPressing.addProperty("type", "create:pressing");
//        tuffPressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:tuff")));
//        tuffPressing.add("results", createJsonArray(createItemResult("creategeography:ash", 4)));
//        tuffPressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("tuff_pressing.json"), tuffPressing);
//
//        // 岩浆块：4岩浆膏
//        JsonObject magmaBlockPressing = new JsonObject();
//        magmaBlockPressing.addProperty("type", "create:pressing");
//        magmaBlockPressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:magma_block")));
//        magmaBlockPressing.add("results", createJsonArray(createItemResult("minecraft:magma_cream", 4)));
//        magmaBlockPressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("magma_block_pressing.json"), magmaBlockPressing);
//
//        // 紫水晶块：4紫水晶碎片
//        JsonObject amethystBlockPressing = new JsonObject();
//        amethystBlockPressing.addProperty("type", "create:pressing");
//        amethystBlockPressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:amethyst_block")));
//        amethystBlockPressing.add("results", createJsonArray(createItemResult("minecraft:amethyst_shard", 4)));
//        amethystBlockPressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("amethyst_block_pressing.json"), amethystBlockPressing);
//
//        // 萤石：4萤石粉
//        JsonObject glowestonePressing = new JsonObject();
//        glowestonePressing.addProperty("type", "create:pressing");
//        glowestonePressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:glowstone")));
//        glowestonePressing.add("results", createJsonArray(createItemResult("minecraft:glowstone_dust", 4)));
//        glowestonePressing.addProperty("processingTime", 100);
//        writeJsonFile(pressingDir.resolve("glowstone_pressing.json"), glowestonePressing);
//
//        // 矿石冲压配方
//        generateOrePressingRecipes(pressingDir);
//    }
//
//    private static void generateOrePressingRecipes(Path pressingDir) throws IOException {
//        // 普通矿石
//        String[][] oreData = {
//                {"coal_ore", "gravel", "3", "creategeography:coal_powder", "2"},
//                {"iron_ore", "gravel", "3", "create:crushed_raw_iron", "1"},
//                {"gold_ore", "gravel", "3", "create:crushed_raw_gold", "1"},
//                {"copper_ore", "gravel", "3", "create:crushed_raw_copper", "1"},
//                {"lapis_ore", "gravel", "3", "creategeography:lapis_lazuli_powder", "9"},
//                {"redstone_ore", "gravel", "3", "minecraft:redstone", "5"},
//                {"diamond_ore", "gravel", "3", "minecraft:diamond", "1"},
//                {"emerald_ore", "gravel", "3", "minecraft:emerald", "1"}
//        };
//
//        for (String[] ore : oreData) {
//            JsonObject orePressing = new JsonObject();
//            orePressing.addProperty("type", "create:pressing");
//            orePressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:" + ore[0])));
//            JsonArray oreResults = new JsonArray();
//            oreResults.add(createItemResult("minecraft:" + ore[1], Integer.parseInt(ore[2])));
//            oreResults.add(createItemResult(ore[3], Integer.parseInt(ore[4])));
//            orePressing.add("results", oreResults);
//            orePressing.addProperty("processingTime", 100);
//            writeJsonFile(pressingDir.resolve(ore[0] + "_pressing.json"), orePressing);
//        }
//
//        // 深层矿石
//        String[][] deepOreData = {
//                {"deepslate_coal_ore", "cobbled_deepslate", "3", "creategeography:coal_powder", "2"},
//                {"deepslate_iron_ore", "cobbled_deepslate", "3", "create:crushed_raw_iron", "1"},
//                {"deepslate_gold_ore", "cobbled_deepslate", "3", "create:crushed_raw_gold", "1"},
//                {"deepslate_copper_ore", "cobbled_deepslate", "3", "create:crushed_raw_copper", "1"},
//                {"deepslate_lapis_ore", "cobbled_deepslate", "3", "creategeography:lapis_lazuli_powder", "9"},
//                {"deepslate_redstone_ore", "cobbled_deepslate", "3", "minecraft:redstone", "5"},
//                {"deepslate_diamond_ore", "cobbled_deepslate", "3", "minecraft:diamond", "1"},
//                {"deepslate_emerald_ore", "cobbled_deepslate", "3", "minecraft:emerald", "1"}
//        };
//
//        for (String[] deepOre : deepOreData) {
//            JsonObject deepOrePressing = new JsonObject();
//            deepOrePressing.addProperty("type", "create:pressing");
//            deepOrePressing.add("ingredients", createJsonArray(createItemIngredient("minecraft:" + deepOre[0])));
//            JsonArray deepOreResults = new JsonArray();
//            deepOreResults.add(createItemResult("minecraft:" + deepOre[1], Integer.parseInt(deepOre[2])));
//            deepOreResults.add(createItemResult(deepOre[3], Integer.parseInt(deepOre[4])));
//            deepOrePressing.add("results", deepOreResults);
//            deepOrePressing.addProperty("processingTime", 100);
//            writeJsonFile(pressingDir.resolve(deepOre[0] + "_pressing.json"), deepOrePressing);
//        }
//    }
//
//    private static void generateFragmentingRecipes(Path recipesDir) throws IOException {
//        Path fragmentingDir = recipesDir.resolve("fragmenting");
//        Files.createDirectories(fragmentingDir);
//
//        // 创建子分类文件夹
//        Path salineDir = fragmentingDir.resolve("saline");
//        Path soilDir = fragmentingDir.resolve("soil");
//        Path rockDir = fragmentingDir.resolve("rock");
//        Path oreDir = fragmentingDir.resolve("ore");
//        Path deepOreDir = fragmentingDir.resolve("deep_ore");
//        Files.createDirectories(salineDir);
//        Files.createDirectories(soilDir);
//        Files.createDirectories(rockDir);
//        Files.createDirectories(oreDir);
//        Files.createDirectories(deepOreDir);
//
//        // 盐碱相关破碎
//        JsonObject salineMudFragmenting = createFragmentingRecipe("creategeography:saline_mud",
//                new ChanceResult("minecraft:mud", 1.0f),
//                new ChanceResult("creategeography:salt", 1.0f));
//        writeJsonFile(salineDir.resolve("saline_mud_fragmenting.json"), salineMudFragmenting);
//
//        JsonObject salineSoilFragmenting = createFragmentingRecipe("creategeography:saline_dirt",
//                new ChanceResult("minecraft:dirt", 1.0f),
//                new ChanceResult("creategeography:salt", 1.0f));
//        writeJsonFile(salineDir.resolve("saline_soil_fragmenting.json"), salineSoilFragmenting);
//
//        JsonObject salineFarmlandFragmenting = createFragmentingRecipe("creategeography:saline_farmland",
//                new ChanceResult("minecraft:farmland", 1.0f),
//                new ChanceResult("creategeography:salt", 1.0f));
//        writeJsonFile(salineDir.resolve("saline_farmland_fragmenting.json"), salineFarmlandFragmenting);
//
//        // 土壤破碎 - 泥土类（使用标签）：0.9土坷0.1硝石
//        JsonObject dirtTagFragmenting = new JsonObject();
//        dirtTagFragmenting.addProperty("type", "creategeography:fragmenting");
//        JsonObject dirtTag = new JsonObject();
//        dirtTag.addProperty("tag", "creategeography:dirt_blocks");
//        dirtTagFragmenting.add("ingredient", dirtTag);
//        JsonArray dirtResults = new JsonArray();
//        JsonObject dirtClodResult = new JsonObject();
//        dirtClodResult.addProperty("item", "creategeography:dirt_clod");
//        dirtClodResult.addProperty("chance", 0.9f);
//        dirtResults.add(dirtClodResult);
//        JsonObject niterResult = new JsonObject();
//        niterResult.addProperty("item", "creategeography:niter");
//        niterResult.addProperty("chance", 0.1f);
//        dirtResults.add(niterResult);
//        dirtTagFragmenting.add("results", dirtResults);
//        writeJsonFile(soilDir.resolve("dirt_blocks_fragmenting.json"), dirtTagFragmenting);
//
//        // 草方块：0.8土坷0.1小麦种子0.1硝石
//        JsonObject grassBlockFragmenting = createFragmentingRecipe("minecraft:grass_block",
//                new ChanceResult("creategeography:dirt_clod", 0.8f),
//                new ChanceResult("minecraft:wheat_seeds", 0.1f),
//                new ChanceResult("creategeography:niter", 0.1f));
//        writeJsonFile(soilDir.resolve("grass_block_fragmenting.json"), grassBlockFragmenting);
//
//        // 砂土：0.8土坷0.2硝石
//        JsonObject coarseDirtFragmenting = createFragmentingRecipe("minecraft:coarse_dirt",
//                new ChanceResult("creategeography:dirt_clod", 0.8f),
//                new ChanceResult("creategeography:niter", 0.2f));
//        writeJsonFile(soilDir.resolve("coarse_dirt_special_fragmenting.json"), coarseDirtFragmenting);
//
//        // 岩石破碎
//        // 安山岩：0.5砾石0.45斜长石0.05角闪石
//        JsonObject andesiteFragmenting = createFragmentingRecipe("minecraft:andesite",
//                new ChanceResult("minecraft:gravel", 0.5f),
//                new ChanceResult("creategeography:plagioclase", 0.45f),
//                new ChanceResult("creategeography:hornblende", 0.05f));
//        writeJsonFile(rockDir.resolve("andesite_fragmenting.json"), andesiteFragmenting);
//
//        // 花岗岩：0.5砾石0.45正长石0.05石英砂
//        JsonObject graniteFragmenting = createFragmentingRecipe("minecraft:granite",
//                new ChanceResult("minecraft:gravel", 0.5f),
//                new ChanceResult("creategeography:orthaclase", 0.45f),
//                new ChanceResult("creategeography:quartz_sand", 0.05f));
//        writeJsonFile(rockDir.resolve("granite_fragmenting.json"), graniteFragmenting);
//
//        // 闪长岩：0.5砾石0.45石英砂0.05角闪石
//        JsonObject dioriteFragmenting = createFragmentingRecipe("minecraft:diorite",
//                new ChanceResult("minecraft:gravel", 0.5f),
//                new ChanceResult("creategeography:quartz_sand", 0.45f),
//                new ChanceResult("creategeography:hornblende", 0.05f));
//        writeJsonFile(rockDir.resolve("diorite_fragmenting.json"), dioriteFragmenting);
//
//        // 沙砾：0.6灰烬0.3沙尘0.1燧石
//        JsonObject gravelFragmenting = createFragmentingRecipe("minecraft:gravel",
//                new ChanceResult("creategeography:ash", 0.6f),
//                new ChanceResult("creategeography:sand_dust", 0.3f),
//                new ChanceResult("minecraft:flint", 0.1f));
//        writeJsonFile(rockDir.resolve("gravel_fragmenting.json"), gravelFragmenting);
//
//        // 黏土：0.8黏土球0.2硝石
//        JsonObject clayFragmenting = createFragmentingRecipe("minecraft:clay",
//                new ChanceResult("minecraft:clay_ball", 0.8f),
//                new ChanceResult("creategeography:niter", 0.2f));
//        writeJsonFile(rockDir.resolve("clay_fragmenting.json"), clayFragmenting);
//
//        // 沙子：1沙尘
//        JsonObject sandFragmenting = createFragmentingRecipe("minecraft:sand",
//                new ChanceResult("creategeography:sand_dust", 1.0f));
//        writeJsonFile(rockDir.resolve("sand_fragmenting.json"), sandFragmenting);
//
//        // 砂岩：0.8沙尘0.2硝石
//        JsonObject sandstoneFragmenting = createFragmentingRecipe("minecraft:sandstone",
//                new ChanceResult("creategeography:sand_dust", 0.8f),
//                new ChanceResult("creategeography:niter", 0.2f));
//        writeJsonFile(rockDir.resolve("sandstone_fragmenting.json"), sandstoneFragmenting);
//
//        // 红沙：1红沙尘
//        JsonObject redSandFragmenting = createFragmentingRecipe("minecraft:red_sand",
//                new ChanceResult("creategeography:red_sand_dust", 1.0f));
//        writeJsonFile(rockDir.resolve("red_sand_fragmenting.json"), redSandFragmenting);
//
//        // 红砂岩：0.8红沙尘0.2硝石
//        JsonObject redSandstoneFragmenting = createFragmentingRecipe("minecraft:red_sandstone",
//                new ChanceResult("creategeography:red_sand_dust", 0.8f),
//                new ChanceResult("creategeography:niter", 0.2f));
//        writeJsonFile(rockDir.resolve("red_sandstone_fragmenting.json"), redSandstoneFragmenting);
//
//        // 凝灰岩：0.8灰烬0.2硫磺粉
//        JsonObject tuffFragmenting = createFragmentingRecipe("minecraft:tuff",
//                new ChanceResult("creategeography:ash", 0.8f),
//                new ChanceResult("creategeography:sulfur_powder", 0.2f));
//        writeJsonFile(rockDir.resolve("tuff_fragmenting.json"), tuffFragmenting);
//
//        // 熔渣：0.25煤炭粉0.5灰烬0.25硫磺粉
//        JsonObject slagFragmenting = createFragmentingRecipe("create:scoria",
//                new ChanceResult("creategeography:coal_powder", 0.25f),
//                new ChanceResult("creategeography:ash", 0.5f),
//                new ChanceResult("creategeography:sulfur_powder", 0.25f));
//        writeJsonFile(rockDir.resolve("slag_fragmenting.json"), slagFragmenting);
//
//        // 焦黑熔渣：0.5煤炭粉0.25灰烬0.25硫磺粉
//        JsonObject scoriaFragmenting = createFragmentingRecipe("create:scorchia",
//                new ChanceResult("creategeography:coal_powder", 0.5f),
//                new ChanceResult("creategeography:ash", 0.25f),
//                new ChanceResult("creategeography:sulfur_powder", 0.25f));
//        writeJsonFile(rockDir.resolve("scoria_fragmenting.json"), scoriaFragmenting);
//
//        // 岩浆块：0.8岩浆膏0.2硫磺粉
//        JsonObject magmaBlockFragmenting = createFragmentingRecipe("minecraft:magma_block",
//                new ChanceResult("minecraft:magma_cream", 0.8f),
//                new ChanceResult("creategeography:sulfur_powder", 0.2f));
//        writeJsonFile(rockDir.resolve("magma_block_fragmenting.json"), magmaBlockFragmenting);
//
//        // 矿石破碎
//        String[][] oreData = {
//                {"coal_ore", "creategeography:coal_powder", "2"},
//                {"iron_ore", "create:crushed_raw_iron", "1"},
//                {"gold_ore", "create:crushed_raw_gold", "1"},
//                {"copper_ore", "create:crushed_raw_copper", "1"},
//                {"lapis_ore", "creategeography:lapis_lazuli_powder", "9"},
//                {"redstone_ore", "minecraft:redstone", "5"},
//                {"diamond_ore", "minecraft:diamond", "1"},
//                {"emerald_ore", "minecraft:emerald", "1"}
//        };
//
//        for (String[] ore : oreData) {
//            JsonObject oreFragmenting = createFragmentingRecipe("minecraft:" + ore[0],
//                    new ChanceResult(ore[1], 1.0f, Integer.parseInt(ore[2])));
//            writeJsonFile(oreDir.resolve(ore[0] + "_fragmenting.json"), oreFragmenting);
//        }
//
//        // 深层矿石破碎
//        String[][] deepOreData = {
//                {"deepslate_coal_ore", "creategeography:coal_powder", "2"},
//                {"deepslate_iron_ore", "create:crushed_raw_iron", "1"},
//                {"deepslate_gold_ore", "create:crushed_raw_gold", "1"},
//                {"deepslate_copper_ore", "create:crushed_raw_copper", "1"},
//                {"deepslate_lapis_ore", "creategeography:lapis_lazuli_powder", "9"},
//                {"deepslate_redstone_ore", "minecraft:redstone", "5"},
//                {"deepslate_diamond_ore", "minecraft:diamond", "1"},
//                {"deepslate_emerald_ore", "minecraft:emerald", "1"}
//        };
//
//        for (String[] deepOre : deepOreData) {
//            JsonObject deepOreFragmenting = createFragmentingRecipe("minecraft:" + deepOre[0],
//                    new ChanceResult(deepOre[1], 1.0f, Integer.parseInt(deepOre[2])));
//            writeJsonFile(deepOreDir.resolve(deepOre[0] + "_fragmenting.json"), deepOreFragmenting);
//        }
//    }
//
//    // ========================= 辅助方法 =========================
//
//    // 熔炼配方创建辅助方法
//    private static JsonObject createSmeltingRecipe(String input, String output, float experience, int cookingTime) {
//        JsonObject recipe = new JsonObject();
//        recipe.addProperty("type", "minecraft:smelting");
//        recipe.add("ingredient", createItemIngredient(input));
//        recipe.addProperty("result", output);
//        recipe.addProperty("experience", experience);
//        recipe.addProperty("cookingtime", cookingTime);
//        return recipe;
//    }
//
//    // 营火配方创建辅助方法
//    private static JsonObject createCampfireRecipe(String input, String output, float experience, int cookingTime) {
//        JsonObject recipe = new JsonObject();
//        recipe.addProperty("type", "minecraft:campfire_cooking");
//        recipe.add("ingredient", createItemIngredient(input));
//        recipe.addProperty("result", output);
//        recipe.addProperty("experience", experience);
//        recipe.addProperty("cookingtime", cookingTime);
//        return recipe;
//    }
//
//    // 烟熏配方创建辅助方法
//    private static JsonObject createSmokingRecipe(String input, String output, float experience, int cookingTime) {
//        JsonObject recipe = new JsonObject();
//        recipe.addProperty("type", "minecraft:smoking");
//        recipe.add("ingredient", createItemIngredient(input));
//        recipe.addProperty("result", output);
//        recipe.addProperty("experience", experience);
//        recipe.addProperty("cookingtime", cookingTime);
//        return recipe;
//    }
//
//    // 破碎作用配方创建辅助方法
//    private static JsonObject createFragmentingRecipe(String input, ChanceResult... results) {
//        JsonObject recipe = new JsonObject();
//        recipe.addProperty("type", "creategeography:fragmenting");
//        recipe.add("ingredient", createItemIngredient(input));
//
//        JsonArray resultsArray = new JsonArray();
//        for (ChanceResult result : results) {
//            JsonObject resultObj = new JsonObject();
//            resultObj.addProperty("item", result.item);
//            if (result.count > 1) {
//                resultObj.addProperty("count", result.count);
//            }
//            if (result.chance < 1.0f) {
//                resultObj.addProperty("chance", result.chance);
//            }
//            resultsArray.add(resultObj);
//        }
//        recipe.add("results", resultsArray);
//
//        return recipe;
//    }
//
//    // 物品成分创建辅助方法
//    private static JsonObject createItemIngredient(String item) {
//        JsonObject ingredient = new JsonObject();
//        ingredient.addProperty("item", item);
//        return ingredient;
//    }
//
//    private static JsonObject createItemIngredient(String item, int count) {
//        JsonObject ingredient = createItemIngredient(item);
//        if (count > 1) {
//            ingredient.addProperty("count", count);
//        }
//        return ingredient;
//    }
//
//    // 物品结果创建辅助方法
//    private static JsonObject createItemResult(String item) {
//        JsonObject result = new JsonObject();
//        result.addProperty("item", item);
//        return result;
//    }
//
//    private static JsonObject createItemResult(String item, int count) {
//        JsonObject result = createItemResult(item);
//        if (count > 1) {
//            result.addProperty("count", count);
//        }
//        return result;
//    }
//
//    // 流体成分创建辅助方法
//    private static JsonObject createFluidIngredient(String fluid, int amount) {
//        JsonObject ingredient = new JsonObject();
//        ingredient.addProperty("fluid", fluid);
//        ingredient.addProperty("amount", amount);
//        return ingredient;
//    }
//
//    // 流体结果创建辅助方法
//    private static JsonObject createFluidResult(String fluid, int amount) {
//        JsonObject result = new JsonObject();
//        result.addProperty("fluid", fluid);
//        result.addProperty("amount", amount);
//        return result;
//    }
//
//    // JSON数组创建辅助方法
//    private static JsonArray createJsonArray(Object... elements) {
//        JsonArray array = new JsonArray();
//        for (Object element : elements) {
//            if (element instanceof String) {
//                array.add((String) element);
//            } else if (element instanceof JsonObject) {
//                array.add((JsonObject) element);
//            }
//        }
//        return array;
//    }
//
//    // 文件写入辅助方法
//    private static void writeJsonFile(Path filePath, JsonObject jsonObject) throws IOException {
//        Files.createDirectories(filePath.getParent());
//        try (FileWriter writer = new FileWriter(filePath.toFile())) {
//            GSON.toJson(jsonObject, writer);
//        }
//    }
//
//    // 破碎作用结果类
//    private static class ChanceResult {
//        private final String item;
//        private final float chance;
//        private final int count;
//
//        public ChanceResult(String item, float chance) {
//            this(item, chance, 1);
//        }
//
//        public ChanceResult(String item, float chance, int count) {
//            this.item = item;
//            this.chance = chance;
//            this.count = count;
//        }
//    }
//}