package com.adonis.content.crafting;

import com.adonis.registry.RecipeRegistry;
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

import java.util.ArrayList;
import java.util.List;

public class FragmentingRecipe implements Recipe<SmartInventory> {
    public static final int MAX_RESULTS = 9;

    private final ResourceLocation id;
    private final String group;
    private final Ingredient inputBlock;
    private final BlockState outputBlock;
    private final NonNullList<ChanceResult> results;
    private final String soundEvent;

    public FragmentingRecipe(ResourceLocation id, String group, Ingredient inputBlock, BlockState outputBlock, NonNullList<ChanceResult> results, String soundEvent) {
        this.id = id;
        this.group = group;
        this.inputBlock = inputBlock;
        this.outputBlock = outputBlock;
        this.results = results;
        this.soundEvent = soundEvent;
    }

    public BlockState getOutputBlock() {
        return this.outputBlock;
    }

    public NonNullList<ChanceResult> getRollableResults() {
        return this.results;
    }

    public String getSoundEventID() {
        return this.soundEvent;
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

    public boolean matches(BlockState blockState) {
        return this.inputBlock.test(new ItemStack(blockState.getBlock().asItem()));
    }

    @Override
    public ResourceLocation getId() { return this.id; }

    @Override
    public String getGroup() { return this.group; }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(this.inputBlock);
        return nonNullList;
    }

    @Override
    public boolean matches(SmartInventory inv, Level level) {
        if (inv.isEmpty()) return false;
        return inputBlock.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(SmartInventory inv, RegistryAccess access) {
        return this.results.isEmpty() ? ItemStack.EMPTY : this.results.get(0).getStack().copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return this.results.isEmpty() ? ItemStack.EMPTY : this.results.get(0).getStack();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public RecipeSerializer<?> getSerializer() { return RecipeRegistry.FRAGMENTING_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return RecipeRegistry.FRAGMENTING_TYPE.get(); }

    // --- 内部类 ChanceResult (完整版) ---
    public static class ChanceResult {
        public static final ChanceResult EMPTY = new ChanceResult(ItemStack.EMPTY, 0.0f);
        private final ItemStack stack;
        private final float chance;

        public ChanceResult(ItemStack stack, float chance) {
            this.stack = stack;
            this.chance = this.chanceAsFloat(chance);
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