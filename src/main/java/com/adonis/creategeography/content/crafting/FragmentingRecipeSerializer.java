package com.adonis.creategeography.content.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class FragmentingRecipeSerializer implements RecipeSerializer<FragmentingRecipe> {

    @Override
    public FragmentingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
        if (input.isEmpty()) {
            throw new JsonParseException("No ingredient for fragmenting recipe");
        }

        BlockState outputState = Blocks.AIR.defaultBlockState();
        if (json.has("output_block")) {
            ResourceLocation blockId = new ResourceLocation(GsonHelper.getAsString(json, "output_block"));
            Block outputBlock = BuiltInRegistries.BLOCK.getOptional(blockId)
                    .orElseThrow(() -> new JsonSyntaxException("Unknown block '" + blockId + "'"));
            outputState = outputBlock.defaultBlockState();
        }

        NonNullList<FragmentingRecipe.ChanceResult> results = NonNullList.create();
        if (json.has("results")) {
            results.addAll(readResults(GsonHelper.getAsJsonArray(json, "results")));
        }

        if (results.size() > FragmentingRecipe.MAX_RESULTS) {
            throw new JsonParseException("Too many results for fragmenting recipe! The maximum is " + FragmentingRecipe.MAX_RESULTS);
        }

        String soundID = GsonHelper.getAsString(json, "sound", "");
        return new FragmentingRecipe(recipeId, group, input, outputState, results, soundID);
    }

    private static NonNullList<FragmentingRecipe.ChanceResult> readResults(JsonArray resultArray) {
        NonNullList<FragmentingRecipe.ChanceResult> results = NonNullList.create();
        for (JsonElement result : resultArray) {
            results.add(FragmentingRecipe.ChanceResult.deserialize(result));
        }
        return results;
    }

    @Nullable
    @Override
    public FragmentingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        Ingredient input = Ingredient.fromNetwork(buffer);
        BlockState outputState = Block.stateById(buffer.readVarInt());

        int i = buffer.readVarInt();
        NonNullList<FragmentingRecipe.ChanceResult> results = NonNullList.withSize(i, FragmentingRecipe.ChanceResult.EMPTY);
        for (int j = 0; j < results.size(); ++j) {
            results.set(j, FragmentingRecipe.ChanceResult.read(buffer));
        }

        String soundId = buffer.readUtf();
        return new FragmentingRecipe(recipeId, group, input, outputState, results, soundId);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, FragmentingRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        recipe.getIngredients().get(0).toNetwork(buffer);
        buffer.writeVarInt(Block.getId(recipe.getOutputBlock()));

        buffer.writeVarInt(recipe.getRollableResults().size());
        for (FragmentingRecipe.ChanceResult result : recipe.getRollableResults()) {
            result.write(buffer);
        }

        buffer.writeUtf(recipe.getSoundEventID());
    }
}