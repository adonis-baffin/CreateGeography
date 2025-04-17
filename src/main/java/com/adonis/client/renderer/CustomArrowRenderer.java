package com.adonis.client.renderer;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class CustomArrowRenderer<T extends AbstractArrow> extends ArrowRenderer<T> {
    
    private final ResourceLocation texture;
    
    public CustomArrowRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context);
        this.texture = texture;
    }
    
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}