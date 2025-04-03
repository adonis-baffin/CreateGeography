package com.adonis.content.block;

import com.adonis.content.BeamHelper;
import com.adonis.content.GeographyShapes;
import com.adonis.registry.BlockEntityRegistry;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class PyroxeneMirrorBlock extends DirectionalKineticBlock implements IBE<PyroxeneMirrorBlockEntity>, IBeamSource {

    public PyroxeneMirrorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return GeographyShapes.PYROXENE_MIRROR.get(state.getValue(FACING));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis().equals(state.getValue(FACING).getAxis());
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public Class<PyroxeneMirrorBlockEntity> getBlockEntityClass() {
        return PyroxeneMirrorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PyroxeneMirrorBlockEntity> getBlockEntityType() {
        return BlockEntityRegistry.PYROXENE_MIRROR.get();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return super.rotate(state, rot);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player != null && player.isShiftKeyDown()) {
            ItemStack stack = new ItemStack(this.asItem());
            if (!stack.isEmpty()) {
                if (player.getInventory().add(stack)) {
                    level.removeBlock(pos, false);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    popResource(level, pos, stack);
                    level.removeBlock(pos, false);
                }
                player.getInventory().setChanged();
                return InteractionResult.SUCCESS;
            }
        }
        return super.onWrenched(state, context);
    }

    @Override
    public BeamHelper.BeamProperties getInitialBeamProperties() {
        PyroxeneMirrorBlockEntity be = this.getBlockEntity(getLevel(), getBlockPos());
        if (be != null && be.isReceivingSunlight()) {
            return new BeamHelper.BeamProperties(1.0F, be.getBlockState().getValue(FACING), new Vec3i(255, 255, 255), BeamHelper.BeamType.DEFAULT);
        }
        return null;
    }

    @Override
    public void addToBeamBlocks(Vec3i vec, Vec3i vec1, BeamHelper.BeamProperties beamProperties) {
        PyroxeneMirrorBlockEntity be = this.getBlockEntity(getLevel(), getBlockPos());
        if (be != null) {
            be.beamBlocks.put(new com.mojang.datafixers.util.Pair<>(vec, vec1), beamProperties);
        }
    }

    @Override
    public BlockPos getBlockPos() {
        PyroxeneMirrorBlockEntity be = this.getBlockEntity(getLevel(), getBlockPos());
        return be != null ? be.getBlockPos() : BlockPos.ZERO;
    }

    @Override
    public Level getLevel() {
        PyroxeneMirrorBlockEntity be = this.getBlockEntity(getLevel(), getBlockPos());
        return be != null ? be.getLevel() : null;
    }

    @Override
    public Map<com.mojang.datafixers.util.Pair<Vec3i, Vec3i>, BeamHelper.BeamProperties> getBeamPropertiesMap() {
        PyroxeneMirrorBlockEntity be = this.getBlockEntity(getLevel(), getBlockPos());
        return be != null ? be.beamBlocks : new HashMap<>();
    }

    @Override
    public boolean isDependent(BlockPos pos) {
        PyroxeneMirrorBlockEntity be = this.getBlockEntity(getLevel(), getBlockPos());
        return be != null && be.dependents.contains(pos);
    }

    @Override
    public void addDependent(BlockPos pos) {
        PyroxeneMirrorBlockEntity be = this.getBlockEntity(getLevel(), getBlockPos());
        if (be != null) {
            be.dependents.add(pos);
        }
    }

    @Override
    public int getTickCount() {
        PyroxeneMirrorBlockEntity be = this.getBlockEntity(getLevel(), getBlockPos());
        return be != null ? be.tickCount : 0;
    }

    @Override
    public boolean shouldRendererLaserBeam() {
        return true;
    }
}