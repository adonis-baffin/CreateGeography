package com.adonis;

import com.adonis.content.block.PyroxeneMirrorRenderer;
import com.adonis.registry.BlockEntityRegistry;
import com.adonis.registry.BlockRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateGeography.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeographyRenderers {

    public static void setupRenderers() {
        // 设置渲染层为 cutoutMipped
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.PYROXENE_MIRROR.get(), RenderType.cutoutMipped());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.PYROXENE_MIRROR.get(), PyroxeneMirrorRenderer::new);
    }
}