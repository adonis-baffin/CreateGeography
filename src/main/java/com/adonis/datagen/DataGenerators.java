package com.adonis.datagen;

import com.adonis.CreateGeography;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = CreateGeography.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        CreateGeography.LOGGER.info("========== CreateGeography 数据生成开始 ==========");

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // 确保生成服务器端数据
        boolean includeServer = event.includeServer();
        CreateGeography.LOGGER.info("包含服务器端数据: {}", includeServer);

        // 生成方块标签
        GeographyBlockTagProvider blockTags = new GeographyBlockTagProvider(
                packOutput,
                lookupProvider,
                existingFileHelper
        );
        generator.addProvider(includeServer, blockTags);
        CreateGeography.LOGGER.info("已注册方块标签生成器");

        // 生成物品标签
        generator.addProvider(
                includeServer,
                new GeographyItemTagProvider(
                        packOutput,
                        lookupProvider,
                        blockTags.contentsGetter(),
                        existingFileHelper
                )
        );
        CreateGeography.LOGGER.info("已注册物品标签生成器");

        CreateGeography.LOGGER.info("========== CreateGeography 数据生成结束 ==========");
    }
}