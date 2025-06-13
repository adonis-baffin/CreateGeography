//package com.adonis.data;
//
//import com.adonis.CreateGeography;
//import com.adonis.registry.ItemRegistry;
//import com.adonis.fluid.GeographyFluids;
//import com.simibubi.create.AllRecipeTypes;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
//import com.simibubi.create.content.processing.recipe.ProcessingOutput;
//import com.simibubi.create.foundation.fluid.FluidIngredient;
//import net.minecraft.core.NonNullList;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.Recipe;
//import net.minecraft.world.item.crafting.RecipeManager;
//import net.minecraft.world.item.crafting.RecipeType;
//import net.minecraft.world.level.material.Fluids;
//import net.minecraftforge.event.server.ServerStartedEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fluids.FluidStack;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.util.HashMap;
//import java.util.Map;
//
//@Mod.EventBusSubscriber(modid = CreateGeography.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
//public class RuntimeRecipeManager {
//
//    @SubscribeEvent
//    public static void onServerStarted(ServerStartedEvent event) {
//        CreateGeography.LOGGER.info("=== Server started, registering Create recipes at runtime ===");
//
//        try {
//            RecipeManager recipeManager = event.getServer().getRecipeManager();
//            addRecipesToManager(recipeManager);
//            CreateGeography.LOGGER.info("=== Runtime recipe registration completed ===");
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("Error during runtime recipe registration", e);
//        }
//    }
//
//    private static void addRecipesToManager(RecipeManager recipeManager) {
//        try {
//            // 使用反射获取配方管理器的内部映射
//            Field recipesField = RecipeManager.class.getDeclaredField("recipes");
//            recipesField.setAccessible(true);
//
//            @SuppressWarnings("unchecked")
//            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allRecipes =
//                    (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>) recipesField.get(recipeManager);
//
//            // 添加粉碎配方
//            addCrushingRecipes(allRecipes);
//
//            // 添加混合配方
//            addMixingRecipes(allRecipes);
//
//            CreateGeography.LOGGER.info("=== Successfully added recipes to RecipeManager ===");
//
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("Failed to add recipes to RecipeManager", e);
//        }
//    }
//
//    private static void addCrushingRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allRecipes) {
//        CreateGeography.LOGGER.info("=== Adding crushing recipes ===");
//
//        try {
//            // 创建煤炭粉碎配方
//            ProcessingRecipe<?> coalRecipe = createCrushingRecipe(
//                    new ResourceLocation(CreateGeography.MODID, "coal_crushing_runtime"),
//                    Items.COAL,
//                    ItemRegistry.COAL_POWDER.get(),
//                    2 // 额外产出数量
//            );
//
//            addRecipe(allRecipes, coalRecipe);
//            CreateGeography.LOGGER.info("Added coal crushing recipe");
//
//            // 创建木炭粉碎配方
//            ProcessingRecipe<?> charcoalRecipe = createCrushingRecipe(
//                    new ResourceLocation(CreateGeography.MODID, "charcoal_crushing_runtime"),
//                    Items.CHARCOAL,
//                    ItemRegistry.CHARCOAL_POWDER.get(),
//                    2 // 额外产出数量
//            );
//
//            addRecipe(allRecipes, charcoalRecipe);
//            CreateGeography.LOGGER.info("Added charcoal crushing recipe");
//
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("Error adding crushing recipes", e);
//        }
//    }
//
//    private static void addMixingRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allRecipes) {
//        CreateGeography.LOGGER.info("=== Adding mixing recipes ===");
//
//        try {
//            // 创建盐水混合配方
//            ProcessingRecipe<?> brineRecipe = createMixingRecipe(
//                    new ResourceLocation(CreateGeography.MODID, "salt_to_brine_runtime"),
//                    ItemRegistry.SALT.get(),
//                    new FluidStack(Fluids.WATER, 250),
//                    new FluidStack(GeographyFluids.BRINE.get(), 250)
//            );
//
//            addRecipe(allRecipes, brineRecipe);
//            CreateGeography.LOGGER.info("Added salt to brine mixing recipe");
//
//        } catch (Exception e) {
//            CreateGeography.LOGGER.error("Error adding mixing recipes", e);
//        }
//    }
//
//    // 使用 ProcessingRecipeBuilder 创建粉碎配方
//    private static ProcessingRecipe<?> createCrushingRecipe(ResourceLocation id,
//                                                            net.minecraft.world.level.ItemLike input,
//                                                            net.minecraft.world.level.ItemLike output,
//                                                            int bonusCount) throws Exception {
//
//        // 获取 ProcessingRecipeFactory
//        ProcessingRecipeBuilder.ProcessingRecipeFactory<?> factory =
//                (ProcessingRecipeBuilder.ProcessingRecipeFactory<?>) getProcessingRecipeFactory(AllRecipeTypes.CRUSHING);
//
//        // 创建 ProcessingRecipeParams
//        Object params = createProcessingRecipeParams(id);
//
//        // 设置输入
//        NonNullList<Ingredient> ingredients = NonNullList.create();
//        ingredients.add(Ingredient.of(input));
//        setField(params, "ingredients", ingredients);
//
//        // 设置输出
//        NonNullList<ProcessingOutput> results = NonNullList.create();
//        // 主要产出
//        results.add(new ProcessingOutput(new ItemStack(output), 1.0f));
//        // 额外产出 (50%概率)
//        if (bonusCount > 0) {
//            results.add(new ProcessingOutput(new ItemStack(output, bonusCount), 0.5f));
//        }
//        setField(params, "results", results);
//
//        // 设置处理时间
//        setField(params, "processingDuration", 150);
//
//        // 创建配方
//        return (ProcessingRecipe<?>) factory.create((ProcessingRecipeBuilder.ProcessingRecipeParams) params);
//    }
//
//    // 使用 ProcessingRecipeBuilder 创建混合配方
//    private static ProcessingRecipe<?> createMixingRecipe(ResourceLocation id,
//                                                          net.minecraft.world.level.ItemLike input,
//                                                          FluidStack fluidInput,
//                                                          FluidStack fluidOutput) throws Exception {
//
//        // 获取 ProcessingRecipeFactory
//        ProcessingRecipeBuilder.ProcessingRecipeFactory<?> factory =
//                (ProcessingRecipeBuilder.ProcessingRecipeFactory<?>) getProcessingRecipeFactory(AllRecipeTypes.MIXING);
//
//        // 创建 ProcessingRecipeParams
//        Object params = createProcessingRecipeParams(id);
//
//        // 设置物品输入
//        NonNullList<Ingredient> ingredients = NonNullList.create();
//        ingredients.add(Ingredient.of(input));
//        setField(params, "ingredients", ingredients);
//
//        // 设置流体输入
//        NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
//        fluidIngredients.add(FluidIngredient.fromFluid(fluidInput.getFluid(), fluidInput.getAmount()));
//        setField(params, "fluidIngredients", fluidIngredients);
//
//        // 设置流体输出
//        NonNullList<FluidStack> fluidResults = NonNullList.create();
//        fluidResults.add(fluidOutput);
//        setField(params, "fluidResults", fluidResults);
//
//        // 设置处理时间
//        setField(params, "processingDuration", 100);
//
//        // 创建配方
//        return (ProcessingRecipe<?>) factory.create((ProcessingRecipeBuilder.ProcessingRecipeParams) params);
//    }
//
//    private static void addRecipe(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> allRecipes, Recipe<?> recipe) {
//        RecipeType<?> type = recipe.getType();
//        allRecipes.computeIfAbsent(type, k -> new HashMap<>()).put(recipe.getId(), recipe);
//    }
//
//    // 反射辅助方法
//    private static Object getProcessingRecipeFactory(AllRecipeTypes recipeType) throws Exception {
//        // 从 AllRecipeTypes 枚举中获取 ProcessingRecipeFactory
//        // 根据源码，每个枚举值都有一个构造函数接受 ProcessingRecipeFactory
//
//        // 尝试获取枚举的私有字段
//        Field[] fields = AllRecipeTypes.class.getDeclaredFields();
//        for (Field field : fields) {
//            if (field.getName().contains("serializerObject") || field.getName().contains("factory")) {
//                field.setAccessible(true);
//                Object value = field.get(recipeType);
//                if (value != null) {
//                    CreateGeography.LOGGER.info("Found field: " + field.getName() + " = " + value.getClass());
//                }
//            }
//        }
//
//        // 直接使用 getSerializer() 获取序列化器，然后从中提取工厂
//        Object serializer = recipeType.getSerializer();
//        CreateGeography.LOGGER.info("Serializer class: " + serializer.getClass());
//
//        // 查找 ProcessingRecipeSerializer 中的工厂字段
//        Field[] serializerFields = serializer.getClass().getDeclaredFields();
//        for (Field field : serializerFields) {
//            field.setAccessible(true);
//            Object value = field.get(serializer);
//            if (value != null && value.getClass().getName().contains("ProcessingRecipeFactory")) {
//                CreateGeography.LOGGER.info("Found factory in serializer: " + field.getName());
//                return value;
//            }
//        }
//
//        throw new RuntimeException("Could not find ProcessingRecipeFactory for " + recipeType.name());
//    }
//
//    private static Object createProcessingRecipeParams(ResourceLocation id) throws Exception {
//        // 查找 ProcessingRecipeBuilder 的内部类 ProcessingRecipeParams
//        Class<?> processingRecipeBuilderClass = ProcessingRecipeBuilder.class;
//        Class<?>[] innerClasses = processingRecipeBuilderClass.getDeclaredClasses();
//
//        Class<?> paramsClass = null;
//        for (Class<?> innerClass : innerClasses) {
//            if (innerClass.getSimpleName().equals("ProcessingRecipeParams")) {
//                paramsClass = innerClass;
//                break;
//            }
//        }
//
//        if (paramsClass == null) {
//            throw new RuntimeException("Could not find ProcessingRecipeParams class");
//        }
//
//        // 查找构造函数
//        Constructor<?> constructor = paramsClass.getDeclaredConstructor(ResourceLocation.class);
//        constructor.setAccessible(true);
//
//        return constructor.newInstance(id);
//    }
//
//    private static void setField(Object object, String fieldName, Object value) throws Exception {
//        Field field = object.getClass().getDeclaredField(fieldName);
//        field.setAccessible(true);
//        field.set(object, value);
//    }
//}