package com.adonis.client;

import com.adonis.CreateGeography;
import com.adonis.registry.BlockEntityRegistry;
import com.adonis.registry.EntityRegistry;
import com.adonis.content.block.PyroxeneMirrorRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CreateGeography.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 客户端初始化代码
    }
    
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册投掷卵石的渲染器
        event.registerEntityRenderer(EntityRegistry.THROWN_PEBBLE.get(), ThrownItemRenderer::new);
    }
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.PYROXENE_MIRROR.get(), PyroxeneMirrorRenderer::new);
    }
}