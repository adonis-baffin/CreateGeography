// 在你的 ClientSetup.java 中添加以下代码

package com.adonis.client;

import com.adonis.CreateGeography;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CreateGeography.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 客户端初始化代码
        event.enqueueWork(() -> {
            // 在这里注册方块实体渲染器

        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册投掷卵石的渲染器
//        event.registerEntityRenderer(EntityRegistry.THROWN_PEBBLE.get(), ThrownItemRenderer::new);
    }

    // 注册物品模型
    @SubscribeEvent
    public static void registerItemModels(ModelEvent.RegisterAdditional event) {
        // 注册 geofragmentator 模型
        event.register(new ResourceLocation(CreateGeography.MODID, "item/geofragmentator"));

        // 注册 trekking_poles 模型
        event.register(new ResourceLocation(CreateGeography.MODID, "item/trekking_poles"));
    }
}