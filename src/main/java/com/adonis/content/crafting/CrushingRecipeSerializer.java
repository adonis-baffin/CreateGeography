package com.adonis.content.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;

public class CrushingRecipeSerializer implements RecipeSerializer<CrushingRecipe> {

    @Override
    public CrushingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        // 我们只有一个输入，即方块
        Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));

        if (input.isEmpty()) {
            throw new JsonParseException("No ingredient for crushing recipe");
        }
        
        NonNullList<CrushingRecipe.ChanceResult> results = readResults(GsonHelper.getAsJsonArray(json, "results"));
        if (results.isEmpty()){
            throw new JsonParseException("No result for crushing recipe");
        }
        if (results.size() > CrushingRecipe.MAX_RESULTS) {
            throw new JsonParseException("Too many results for crushing recipe! The maximum is " + CrushingRecipe.MAX_RESULTS);
        }
        
        String soundID = GsonHelper.getAsString(json, "sound", "");
        return new CrushingRecipe(recipeId, group, input, results, soundID);
    }
    
    private static NonNullList<CrushingRecipe.ChanceResult> readResults(JsonArray resultArray) {
        NonNullList<CrushingRecipe.ChanceResult> results = NonNullList.create();
        for (JsonElement result : resultArray) {
            results.add(CrushingRecipe.ChanceResult.deserialize(result));
        }
        return results;
    }

    @Nullable
    @Override
    public CrushingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        Ingredient input = Ingredient.fromNetwork(buffer);
        
        int i = buffer.readVarInt();
        NonNullList<CrushingRecipe.ChanceResult> results = NonNullList.withSize(i, CrushingRecipe.ChanceResult.EMPTY);
        for (int j = 0; j < results.size(); ++j) {
            results.set(j, CrushingRecipe.ChanceResult.read(buffer));
        }

        String soundId = buffer.readUtf();
        return new CrushingRecipe(recipeId, group, input, results, soundId);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CrushingRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        recipe.getIngredients().get(0).toNetwork(buffer);
        
        buffer.writeVarInt(recipe.getRollableResults().size());
        for (CrushingRecipe.ChanceResult result : recipe.getRollableResults()) {
            result.write(buffer);
        }
        
        buffer.writeUtf(recipe.getSoundEventID());
    }
}