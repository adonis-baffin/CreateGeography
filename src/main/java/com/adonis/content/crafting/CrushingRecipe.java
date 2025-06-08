package com.adonis.content.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.crafting.CraftingHelper;
import com.adonis.registry.RecipeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// 借鉴了 Farmer's Delight 的 CuttingBoardRecipe
public class CrushingRecipe implements Recipe<SmartInventory> {
    public static final int MAX_RESULTS = 9;

    private final ResourceLocation id;
    private final String group;
    private final Ingredient inputBlock;
    private final NonNullList<ChanceResult> results;
    private final String soundEvent;

    public CrushingRecipe(ResourceLocation id, String group, Ingredient inputBlock, NonNullList<ChanceResult> results, String soundEvent) {
        this.id = id;
        this.group = group;
        this.inputBlock = inputBlock;
        this.results = results;
        this.soundEvent = soundEvent;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(this.inputBlock);
        return nonNullList;
    }

    public NonNullList<ChanceResult> getRollableResults() {
        return this.results;
    }

    public List<ItemStack> rollResults(RandomSource rand, int fortuneLevel) {
        List<ItemStack> rolledList = new ArrayList<>();
        for (ChanceResult result : this.results) {
            ItemStack stack = result.rollOutput(rand, fortuneLevel);
            if (!stack.isEmpty()) {
                rolledList.add(stack);
            }
        }
        return rolledList;
    }

    public String getSoundEventID() {
        return this.soundEvent;
    }
    
    // 自定义匹配逻辑，用于匹配方块
    public boolean matches(BlockState blockState) {
        return this.inputBlock.test(new ItemStack(blockState.getBlock().asItem()));
    }

    @Override
    public boolean matches(SmartInventory inv, Level level) {
        // 这个方法在这里不太适用，因为我们是对方块进行操作，而不是物品栏
        // 但为了实现Recipe接口，还是需要它
        if (inv.isEmpty()) {
            return false;
        }
        return inputBlock.test(inv.getItem(0));
    }


    @Override
    public ItemStack assemble(SmartInventory inv, RegistryAccess access) {
        return this.results.get(0).getStack().copy();
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return this.results.get(0).getStack();
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CRUSHING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CRUSHING_TYPE.get();
    }
    
    // 从 Farmer's Delight 借鉴的 ChanceResult 类，用于处理带几率的掉落
    public static class ChanceResult {
        public static final ChanceResult EMPTY = new ChanceResult(ItemStack.EMPTY, 0.0f);
        private final ItemStack stack;
        private final float chance;

        public ChanceResult(ItemStack stack, float chance) {
            this.stack = stack;
            this.chance = chance;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public float getChance() {
            return this.chance;
        }

        public ItemStack rollOutput(RandomSource rand, int fortuneLevel) {
            // 时运暂时不影响，可以后续添加逻辑
            if (this.chance > 0.0f && this.chance < 1.0f && rand.nextFloat() > this.chance) {
                return ItemStack.EMPTY;
            }
            return this.getStack().copy();
        }

        public static ChanceResult deserialize(JsonElement json) {
            if (!json.isJsonObject()) {
                throw new JsonParseException("Must be a JSON object");
            }
            JsonObject obj = json.getAsJsonObject();
            ItemStack stack = new ItemStack(GsonHelper.getAsItem(obj, "item"));
            if (obj.has("count")) {
                stack.setCount(GsonHelper.getAsInt(obj, "count"));
            }
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