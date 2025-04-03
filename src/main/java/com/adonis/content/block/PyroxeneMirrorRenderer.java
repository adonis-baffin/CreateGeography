package com.adonis.content.block;

import com.adonis.CreateGeography;
import com.adonis.GeographyPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PyroxeneMirrorRenderer extends ShaftRenderer<PyroxeneMirrorBlockEntity> {

    public PyroxeneMirrorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(PyroxeneMirrorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        // 渲染传动杆
        super.renderSafe(be, partialTicks, ms, bufferSource, light, overlay);

        // 渲染镜片
        BlockState state = be.getBlockState();
        SuperByteBuffer mirror = CachedBufferer.partial(GeographyPartialModels.PYROXENE_MIRROR, state);

        Direction direction = state.getValue(DirectionalKineticBlock.FACING);
        if (direction.getAxis().isHorizontal()) {
            mirror.rotateCentered(direction.getClockWise(), (float) (Math.PI / 2F));
        }
        kineticRotationTransform(mirror, be, Direction.Axis.Y, AngleHelper.rad(be.getIndependentAngle(partialTicks)), light)
                .light(light)
                .renderInto(ms, bufferSource.getBuffer(RenderType.cutoutMipped()));
    }
}