package com.adonis.content.block;

import com.adonis.content.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IBeamReceiver {

    float BEAM_RADIUS = 0.05F;
    float LIVING_ENTITY_EXTENDED_RADIUS = 3F;

    boolean useCenteredIncidence();

    record BeamSourceInstance(Optional<BeamHelper.BeamProperties> optionalBeamProperties, @Nullable BlockPos pos) {
        public static BeamSourceInstance empty(BlockPos pos) {
            return new BeamSourceInstance(Optional.empty(), pos);
        }

        BeamSourceInstance empty() {
            return BeamSourceInstance.empty(this.pos);
        }

        // 修复：将 BeamProperties 包装为 Optional
        public static BeamSourceInstance read(CompoundTag tag) {
            int[] arr = tag.contains("SourcePos") ? tag.getIntArray("SourcePos") : null;
            BlockPos pos = arr == null ? null : new BlockPos(arr[0], arr[1], arr[2]);
            BeamHelper.BeamProperties properties = BeamHelper.BeamProperties.read(tag);
            return new BeamSourceInstance(Optional.of(properties), pos);
        }

        public void write(CompoundTag tag) {
            if (this.pos != null) tag.putIntArray("SourcePos", new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});
            this.optionalBeamProperties.ifPresent(beamProperties -> beamProperties.write(tag));
        }

        public boolean isPropertiesValid(BeamHelper.BeamProperties beamProperties) {
            return this.optionalBeamProperties.isEmpty() || this.optionalBeamProperties.get().equals(beamProperties);
        }

        public BeamSourceInstance checkSourceExistenceAndCompatibility(BlockEntity be) {
            if (be.hasLevel() && this.pos != null && this.optionalBeamProperties.isPresent()) {
                BlockEntity blockEntity = be.getLevel().getBlockEntity(pos);
                if (blockEntity instanceof IBeamSource iBeamSource) {
                    if (!iBeamSource.isDependent(be.getBlockPos())) {
                        return this.empty();
                    }
                } else {
                    return this.empty();
                }
            }
            return this;
        }
    }

    void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex);

    static Vec3 getLaserIrradiatedFaceOffsetVar(Direction faceIrradiated, BlockPos pos, Level level) {
        final AABB[] aabb = {null, null};

        getNearLivingEntity(level, pos, LIVING_ENTITY_EXTENDED_RADIUS, faceIrradiated).ifPresent(
                livingEntity -> {
                    AABB aabb1 = livingEntity.getBoundingBox();
                    aabb1 = aabb1.move(-pos.getX(), -pos.getY(), -pos.getZ());
                    aabb[0] = aabb1;
                });

        BlockState state = level.getBlockState(pos);
        boolean f = state.isAir();
        if (f && aabb[0] != null) {
            aabb[1] = null;
        } else if (f || (state.getBlock() instanceof IBeamReceiver be && be.useCenteredIncidence())) {
            aabb[1] = new AABB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
        } else {
            BlockGetter bg = level.getChunk(pos);
            VoxelShape shape = state.getShape(bg, pos);
            aabb[1] = !shape.isEmpty() ? shape.bounds() : new AABB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
        }

        AABB box = checkBetterBox(aabb[0], aabb[1], faceIrradiated);
        boolean f1 = faceIrradiated.getAxisDirection().getStep() > 0;
        Vec3 vec3 = (!f1 ? new Vec3(box.maxX, box.maxY, box.maxZ) : new Vec3(box.minX, box.minY, box.minZ)).multiply(Vec3.atLowerCornerOf(faceIrradiated.getNormal()));
        return vec3.subtract(new Vec3(0.5, 0.5, 0.5).multiply(Vec3.atLowerCornerOf(faceIrradiated.getNormal()))).scale(f1 ? 1 : -1);
    }

    static AABB checkBetterBox(@Nullable AABB box1, @Nullable AABB box2, Direction direction) {
        if (box1 == null) return box2;
        if (box2 == null) return box1;
        boolean f = direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
        double d = getMaxMinAABB(box1, f).subtract(getMaxMinAABB(box2, f)).get(direction.getAxis()) * (f ? 1 : -1);
        return d < 0 ? box1 : box2;
    }

    static Vec3 getMaxMinAABB(AABB box, boolean b) {
        return b ? new Vec3(box.minX, box.minY, box.minZ) : new Vec3(box.maxX, box.maxY, box.maxZ);
    }

    static Optional<LivingEntity> getNearLivingEntity(Level level, BlockPos pos, double extendedRadius, Direction direction) {
        AABB aabb = new AABB(pos.getCenter().add(new Vec3(extendedRadius, extendedRadius, extendedRadius)), pos.getCenter().add(new Vec3(-extendedRadius, -extendedRadius, -extendedRadius)));
        LivingEntity livingEntity = level.getNearestEntity(LivingEntity.class, TargetingConditions.forNonCombat(), null, pos.getX(), pos.getY(), pos.getZ(), aabb);
        if (livingEntity == null) return Optional.empty();
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getBlockState().getBlock() instanceof IBeamReceiver) {
            return Optional.empty();
        }
        return insideOfBounds(pos, direction, livingEntity.getBoundingBox()) ? Optional.of(livingEntity) : Optional.empty();
    }

    static boolean insideOfBounds(BlockPos pos, Direction direction, AABB aabb) {
        Vec3 u = Vec3.atLowerCornerOf(direction.getNormal()).scale(direction.getAxisDirection().getStep());
        double l = 0.4;
        Vec3 posV = pos.getCenter();
        AABB aabb1 = new AABB(posV.subtract(u.multiply(l, l, l)), posV.add(u.multiply(l, l, l)));
        aabb1 = aabb1.inflate(BEAM_RADIUS);
        return aabb.intersects(aabb1) || aabb1.intersects(aabb);
    }
}