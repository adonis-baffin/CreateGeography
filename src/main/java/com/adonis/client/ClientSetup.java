package com.adonis.client;

import com.adonis.CreateGeography;
import com.adonis.client.renderer.CustomArrowRenderer;
import com.adonis.registry.BlockEntityRegistry;
import com.adonis.registry.EntityRegistry;
import com.adonis.content.block.PyroxeneMirrorRenderer;
import com.adonis.entity.ExplosiveArrowEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
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
        event.enqueueWork(() -> {
            // 在这里注册方块实体渲染器
            BlockEntityRenderers.register(BlockEntityRegistry.PYROXENE_MIRROR.get(), PyroxeneMirrorRenderer::new);
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册投掷卵石的渲染器
        event.registerEntityRenderer(EntityRegistry.THROWN_PEBBLE.get(), ThrownItemRenderer::new);

        // 注册爆炸箭的渲染器
        event.registerEntityRenderer(EntityRegistry.EXPLOSIVE_ARROW.get(),
                (ctx) -> new CustomArrowRenderer<>(ctx,
                        new ResourceLocation(CreateGeography.MODID, "textures/entity/explosive_arrow.png")));

        // 注册末影水晶爆炸箭的渲染器
        event.registerEntityRenderer(EntityRegistry.ENDER_CRYSTAL_ARROW.get(),
                (ctx) -> new CustomArrowRenderer<>(ctx,
                        new ResourceLocation(CreateGeography.MODID, "textures/entity/ender_crystal_arrow.png")));

        // 注册闪电箭的渲染器
        event.registerEntityRenderer(EntityRegistry.LIGHTNING_ARROW.get(),
                (ctx) -> new CustomArrowRenderer<>(ctx,
                        new ResourceLocation(CreateGeography.MODID, "textures/entity/lightning_arrow.png")));
    }

    // 删除重复的registerRenderers方法，避免冲突
    // public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    //     event.registerBlockEntityRenderer(BlockEntityRegistry.PYROXENE_MIRROR.get(), PyroxeneMirrorRenderer::new);
    // }

    // 内部类：爆炸箭渲染器
//    private static class ExplosiveArrowRenderer extends net.minecraft.client.renderer.entity.ArrowRenderer<ExplosiveArrowEntity> {
//        private static final ResourceLocation TEXTURE = new ResourceLocation(CreateGeography.MODID, "textures/entity/explosive_arrow.png");
//
//        public ExplosiveArrowRenderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) {
//            super(context);
//        }
//
//        @Override
//        public ResourceLocation getTextureLocation(ExplosiveArrowEntity entity) {
//            return TEXTURE;
//        }
//    }
}