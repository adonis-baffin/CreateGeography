package com.adonis.data;

import com.adonis.content.crafting.FragmentingRecipe;
import com.adonis.registry.ItemRegistry;
import com.adonis.registry.RecipeRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class FragmentingRecipes {
    
    public static void register(Consumer<FinishedRecipe> consumer) {
        // 盐碱相关破碎
        registerSalineFragmenting(consumer);
        
        // 土壤破碎
        registerSoilFragmenting(consumer);
        
        // 岩石破碎
        registerRockFragmenting(consumer);
        
        // 矿石破碎
        registerOreFragmenting(consumer);
        
        // 深层矿石破碎
        registerDeepOreFragmenting(consumer);
    }
    
    private static void registerSalineFragmenting(Consumer<FinishedRecipe> consumer) {
        // 盐碱泥巴=泥巴+盐
        // 注意：需要等你提供方块注册代码
        /*
        createFragmentingRecipe(consumer, "saline_mud_fragmenting",
                Ingredient.of(BlockRegistry.SALINE_MUD.get()),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(new ItemStack(BlockRegistry.MUD.get()), 1.0f),
                new FragmentingRecipe.ChanceResult(new ItemStack(ItemRegistry.SALT.get()), 1.0f)
        );
        
        // 盐碱土=泥土+盐
        createFragmentingRecipe(consumer, "saline_soil_fragmenting",
                Ingredient.of(BlockRegistry.SALINE_SOIL.get()),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(new ItemStack(Items.DIRT), 1.0f),
                new FragmentingRecipe.ChanceResult(new ItemStack(ItemRegistry.SALT.get()), 1.0f)
        );
        
        // 盐碱耕地=耕地+盐
        createFragmentingRecipe(consumer, "saline_farmland_fragmenting",
                Ingredient.of(BlockRegistry.SALINE_FARMLAND.get()),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(new ItemStack(Items.FARMLAND), 1.0f),
                new FragmentingRecipe.ChanceResult(new ItemStack(ItemRegistry.SALT.get()), 1.0f)
        );
        */
    }
    
    private static void registerSoilFragmenting(Consumer<FinishedRecipe> consumer) {
        // 泥土类：0.9土坷0.1硝石
        createFragmentingRecipe(consumer, "dirt_fragmenting",
                Ingredient.of(Items.DIRT),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.DIRT_CLOD.get(), 0.9f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.NITER.get(), 0.1f)
        );
        
        createFragmentingRecipe(consumer, "coarse_dirt_fragmenting",
                Ingredient.of(Items.COARSE_DIRT),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.DIRT_CLOD.get(), 0.9f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.NITER.get(), 0.1f)
        );
        
        createFragmentingRecipe(consumer, "rooted_dirt_fragmenting",
                Ingredient.of(Items.ROOTED_DIRT),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.DIRT_CLOD.get(), 0.9f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.NITER.get(), 0.1f)
        );
        
        // 草方块：0.8土坷0.1小麦种子0.1硝石
        createFragmentingRecipe(consumer, "grass_block_fragmenting",
                Ingredient.of(Items.GRASS_BLOCK),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.DIRT_CLOD.get(), 0.8f),
                new FragmentingRecipe.ChanceResult(Items.WHEAT_SEEDS, 0.1f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.NITER.get(), 0.1f)
        );
        
        // 砂土：0.8土坷0.2硝石
        createFragmentingRecipe(consumer, "coarse_dirt_special_fragmenting",
                Ingredient.of(Items.COARSE_DIRT),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.DIRT_CLOD.get(), 0.8f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.NITER.get(), 0.2f)
        );
    }
    
    private static void registerRockFragmenting(Consumer<FinishedRecipe> consumer) {
        // 安山岩：0.5砾石0.45斜长石0.05角闪石
        createFragmentingRecipe(consumer, "andesite_fragmenting",
                Ingredient.of(Items.ANDESITE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.GRAVEL, 0.5f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.PLAGIOCLASE.get(), 0.45f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.HORNBLENDE.get(), 0.05f)
        );
        
        // 花岗岩：0.5砾石0.45正长石0.05石英砂
        createFragmentingRecipe(consumer, "granite_fragmenting",
                Ingredient.of(Items.GRANITE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.GRAVEL, 0.5f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.ORTHACLASE.get(), 0.45f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.QUARTZ_SAND.get(), 0.05f)
        );
        
        // 闪长岩：0.5砾石0.45石英砂0.05角闪石
        createFragmentingRecipe(consumer, "diorite_fragmenting",
                Ingredient.of(Items.DIORITE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.GRAVEL, 0.5f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.QUARTZ_SAND.get(), 0.45f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.HORNBLENDE.get(), 0.05f)
        );
        
        // 沙砾：0.6灰烬0.3沙尘0.1燧石
        createFragmentingRecipe(consumer, "gravel_fragmenting",
                Ingredient.of(Items.GRAVEL),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.ASH.get(), 0.6f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.SAND_DUST.get(), 0.3f),
                new FragmentingRecipe.ChanceResult(Items.FLINT, 0.1f)
        );
        
        // 黏土：0.8黏土球0.2硝石
        createFragmentingRecipe(consumer, "clay_fragmenting",
                Ingredient.of(Items.CLAY),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.CLAY_BALL, 0.8f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.NITER.get(), 0.2f)
        );
        
        // 沙子：1沙尘
        createFragmentingRecipe(consumer, "sand_fragmenting",
                Ingredient.of(Items.SAND),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.SAND_DUST.get(), 1.0f)
        );
        
        // 砂岩：0.8沙尘0.2硝石
        createFragmentingRecipe(consumer, "sandstone_fragmenting",
                Ingredient.of(Items.SANDSTONE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.SAND_DUST.get(), 0.8f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.NITER.get(), 0.2f)
        );
        
        // 红沙：1红沙尘
        createFragmentingRecipe(consumer, "red_sand_fragmenting",
                Ingredient.of(Items.RED_SAND),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.RED_SAND_DUST.get(), 1.0f)
        );
        
        // 红砂岩：0.8红沙尘0.2硝石
        createFragmentingRecipe(consumer, "red_sandstone_fragmenting",
                Ingredient.of(Items.RED_SANDSTONE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.RED_SAND_DUST.get(), 0.8f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.NITER.get(), 0.2f)
        );
        
        // 凝灰岩：0.8灰烬0.2硫磺粉
        createFragmentingRecipe(consumer, "tuff_fragmenting",
                Ingredient.of(Items.TUFF),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.ASH.get(), 0.8f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.SULFUR_POWDER.get(), 0.2f)
        );
        
        // 岩浆块：0.8岩浆膏0.2硫磺粉
        createFragmentingRecipe(consumer, "magma_block_fragmenting",
                Ingredient.of(Items.MAGMA_BLOCK),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.MAGMA_CREAM, 0.8f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.SULFUR_POWDER.get(), 0.2f)
        );
    }
    
    private static void registerOreFragmenting(Consumer<FinishedRecipe> consumer) {
        // 煤矿石：2煤粉
        createFragmentingRecipe(consumer, "coal_ore_fragmenting",
                Ingredient.of(Items.COAL_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.COAL_POWDER.get(), 1.0f),
                new FragmentingRecipe.ChanceResult(ItemRegistry.COAL_POWDER.get(), 1.0f)
        );
        
        // 铁矿石：1粉碎铁矿石
        createFragmentingRecipe(consumer, "iron_ore_fragmenting",
                Ingredient.of(Items.IRON_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(com.simibubi.create.AllItems.CRUSHED_IRON.get(), 1.0f)
        );
        
        // 金矿石：1粉碎金矿石
        createFragmentingRecipe(consumer, "gold_ore_fragmenting",
                Ingredient.of(Items.GOLD_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(com.simibubi.create.AllItems.CRUSHED_GOLD.get(), 1.0f)
        );
        
        // 铜矿石：1粉碎铜矿石
        createFragmentingRecipe(consumer, "copper_ore_fragmenting",
                Ingredient.of(Items.COPPER_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(com.simibubi.create.AllItems.CRUSHED_COPPER.get(), 1.0f)
        );
        
        // 青金石矿石：9青金石粉
        createFragmentingRecipe(consumer, "lapis_ore_fragmenting",
                Ingredient.of(Items.LAPIS_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.LAPIS_LAZULI_POWDER.get(), 1.0f, 9)
        );
        
        // 红石矿石：5红石粉
        createFragmentingRecipe(consumer, "redstone_ore_fragmenting",
                Ingredient.of(Items.REDSTONE_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.REDSTONE, 1.0f, 5)
        );
        
        // 钻石矿石：1钻石
        createFragmentingRecipe(consumer, "diamond_ore_fragmenting",
                Ingredient.of(Items.DIAMOND_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.DIAMOND, 1.0f)
        );
        
        // 绿宝石矿石：1绿宝石
        createFragmentingRecipe(consumer, "emerald_ore_fragmenting",
                Ingredient.of(Items.EMERALD_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.EMERALD, 1.0f)
        );
    }
    
    private static void registerDeepOreFragmenting(Consumer<FinishedRecipe> consumer) {
        // 深层煤矿石：2煤粉
        createFragmentingRecipe(consumer, "deepslate_coal_ore_fragmenting",
                Ingredient.of(Items.DEEPSLATE_COAL_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.COAL_POWDER.get(), 1.0f, 2)
        );
        
        // 深层铁矿石：1粉碎铁矿石
        createFragmentingRecipe(consumer, "deepslate_iron_ore_fragmenting",
                Ingredient.of(Items.DEEPSLATE_IRON_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(com.simibubi.create.AllItems.CRUSHED_IRON.get(), 1.0f)
        );
        
        // 深层金矿石：1粉碎金矿石
        createFragmentingRecipe(consumer, "deepslate_gold_ore_fragmenting",
                Ingredient.of(Items.DEEPSLATE_GOLD_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(com.simibubi.create.AllItems.CRUSHED_GOLD.get(), 1.0f)
        );
        
        // 深层铜矿石：1粉碎铜矿石
        createFragmentingRecipe(consumer, "deepslate_copper_ore_fragmenting",
                Ingredient.of(Items.DEEPSLATE_COPPER_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(com.simibubi.create.AllItems.CRUSHED_COPPER.get(), 1.0f)
        );
        
        // 深层青金石矿石：9青金石粉
        createFragmentingRecipe(consumer, "deepslate_lapis_ore_fragmenting",
                Ingredient.of(Items.DEEPSLATE_LAPIS_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(ItemRegistry.LAPIS_LAZULI_POWDER.get(), 1.0f, 9)
        );
        
        // 深层红石矿石：5红石粉
        createFragmentingRecipe(consumer, "deepslate_redstone_ore_fragmenting",
                Ingredient.of(Items.DEEPSLATE_REDSTONE_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.REDSTONE, 1.0f, 5)
        );
        
        // 深层钻石矿石：1钻石
        createFragmentingRecipe(consumer, "deepslate_diamond_ore_fragmenting",
                Ingredient.of(Items.DEEPSLATE_DIAMOND_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.DIAMOND, 1.0f)
        );
        
        // 深层绿宝石矿石：1绿宝石
        createFragmentingRecipe(consumer, "deepslate_emerald_ore_fragmenting",
                Ingredient.of(Items.DEEPSLATE_EMERALD_ORE),
                Blocks.AIR.defaultBlockState(),
                new FragmentingRecipe.ChanceResult(Items.EMERALD, 1.0f)
        );
    }
    
    /**
     * 创建破碎作用配方的辅助方法
     */
    private static void createFragmentingRecipe(Consumer<FinishedRecipe> consumer, String name,
                                               Ingredient input, 
                                               net.minecraft.world.level.block.state.BlockState outputBlock,
                                               FragmentingRecipe.ChanceResult... results) {
        
        NonNullList<FragmentingRecipe.ChanceResult> resultsList = NonNullList.create();
        for (FragmentingRecipe.ChanceResult result : results) {
            resultsList.add(result);
        }
        
        FragmentingRecipe recipe = new FragmentingRecipe(
                new net.minecraft.resources.ResourceLocation(com.adonis.CreateGeography.MODID, name),
                "",
                input,
                outputBlock,
                resultsList,
                ""
        );
        
        consumer.accept(new FinishedFragmentingRecipe(recipe));
    }
    
    /**
     * 用于包装FragmentingRecipe的FinishedRecipe实现
     */
    private static class FinishedFragmentingRecipe implements FinishedRecipe {
        private final FragmentingRecipe recipe;
        
        public FinishedFragmentingRecipe(FragmentingRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void serializeRecipeData(com.google.gson.JsonObject json) {
            // 序列化输入材料
            json.add("ingredient", recipe.getIngredients().get(0).toJson());
            
            // 序列化输出方块（如果不是空气）
            if (recipe.getOutputBlock() != Blocks.AIR.defaultBlockState()) {
                json.addProperty("output_block", 
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK
                        .getKey(recipe.getOutputBlock().getBlock()).toString());
            }
            
            // 序列化结果
            com.google.gson.JsonArray resultsArray = new com.google.gson.JsonArray();
            for (FragmentingRecipe.ChanceResult result : recipe.getRollableResults()) {
                com.google.gson.JsonObject resultObj = new com.google.gson.JsonObject();
                resultObj.addProperty("item", 
                    net.minecraft.core.registries.BuiltInRegistries.ITEM
                        .getKey(result.getStack().getItem()).toString());
                if (result.getStack().getCount() > 1) {
                    resultObj.addProperty("count", result.getStack().getCount());
                }
                if (result.getChance() < 1.0f) {
                    resultObj.addProperty("chance", result.getChance());
                }
                resultsArray.add(resultObj);
            }
            json.add("results", resultsArray);
            
            // 添加声音事件（如果有）
            if (!recipe.getSoundEventID().isEmpty()) {
                json.addProperty("sound", recipe.getSoundEventID());
            }
        }

        @Override
        public net.minecraft.resources.ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() {
            return RecipeRegistry.FRAGMENTING_SERIALIZER.get();
        }

        @Override
        public com.google.gson.JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public net.minecraft.resources.ResourceLocation getAdvancementId() {
            return null;
        }
    }
    // 在你的 FragmentingRecipe.ChanceResult 类中添加以下构造函数和方法：

    public static class ChanceResult {
        public static final ChanceResult EMPTY = new ChanceResult(ItemStack.EMPTY, 0.0f);
        private final ItemStack stack;
        private final float chance;

        public ChanceResult(ItemStack stack, float chance) {
            this.stack = stack;
            this.chance = this.chanceAsFloat(chance);
        }

        // 新增：支持物品和数量的构造函数
        public ChanceResult(net.minecraft.world.level.ItemLike item, float chance) {
            this(new ItemStack(item), chance);
        }

        // 新增：支持物品、概率和数量的构造函数
        public ChanceResult(net.minecraft.world.level.ItemLike item, float chance, int count) {
            this(new ItemStack(item, count), chance);
        }

        private float chanceAsFloat(float c) {
            if (c > 1.0f) return 1.0f;
            if (c < 0.0f) return 0.0f;
            return c;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public float getChance() {
            return this.chance;
        }

        public ItemStack rollOutput(RandomSource rand, int fortuneLevel) {
            // 可以未来在这里添加时运逻辑
            if (this.chance < 1.0f && rand.nextFloat() > this.chance) {
                return ItemStack.EMPTY;
            }
            return this.getStack().copy();
        }

        public static ChanceResult deserialize(JsonElement json) {
            if (!json.isJsonObject()) {
                throw new JsonParseException("Must be a JSON object");
            }
            JsonObject obj = json.getAsJsonObject();
            // 使用 CraftingHelper 来安全地解析物品，它支持NBT
            ItemStack stack = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(obj, true, true);
            float chance = GsonHelper.getAsFloat(obj, "chance", 1.0F);
            return new ChanceResult(stack, chance);
        }

        public static ChanceResult read(FriendlyByteBuf buf) {
            ItemStack stack = buf.readItem();
            float chance = buf.readFloat();
            return new ChanceResult(stack, chance);
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeItem(this.stack);
            buf.writeFloat(this.chance);
        }
    }
}