package com.adonis.datagen;

import com.adonis.CreateGeography;
import com.adonis.registry.BlockRegistry;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public final class DataGen {
  public static void gatherData(GatherDataEvent event) {
    DataGenerator dataGenerator = event.getGenerator();
    PackOutput output = dataGenerator.getPackOutput();

    // 已移除物品标签和模型生成相关代码
    // 物品模型生成
    CreateGeography.REGISTRATE.addDataGenerator(ProviderType.ITEM_MODEL, provider -> {
      RegistrateItemModelProvider itemProvider = (RegistrateItemModelProvider) provider;
      // PyroxeneMirror 已有代码...
      itemProvider.withExistingParent(
              BlockRegistry.PYROXENE_MIRROR.getId().getPath(),
              itemProvider.modLoc("block/encased_mirror/mirror")
      );
      // PyroxeneHeater
      itemProvider.withExistingParent(
              BlockRegistry.PYROXENE_HEATER.getId().getPath(),
              itemProvider.modLoc("block/pyroxene_heater")
      );
    });

    if (event.includeServer()) {
      dataGenerator.addProvider(true, new DataProvider() {
        @Override
        public String getName() {
          return "CreateGeography' Processing Recipes";
        }

        @Override
        public CompletableFuture<?> run(@Nonnull CachedOutput dc) {
          return CompletableFuture.supplyAsync(() -> new FreezingRecipeGen(output).run(dc));
        }
      });
    }
  }
}