package com.adonis.content;

import com.adonis.content.block.IBeamReceiver;
import com.adonis.content.block.IBeamSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

public class BeamHelper {

    public static class BeamProperties {
        public final float intensity;
        public final Direction direction;
        public final Vec3i color;
        public final BeamType beamType;

        public BeamProperties(float intensity, Direction direction, Vec3i color, BeamType beamType) {
            this.intensity = intensity;
            this.direction = direction;
            this.color = color != null ? color : new Vec3i(255, 255, 255);
            this.beamType = beamType != null ? beamType : BeamType.DEFAULT;
        }

        public static BeamProperties withDirection(BeamProperties original, Direction newDirection) {
            return new BeamProperties(original.intensity, newDirection, original.color, original.beamType);
        }

        public void write(CompoundTag tag) {
            tag.putFloat("Intensity", this.intensity);
            tag.putInt("Direction", this.direction.get3DDataValue());
            tag.putInt("ColorR", this.color.getX());
            tag.putInt("ColorG", this.color.getY());
            tag.putInt("ColorB", this.color.getZ());
            tag.putString("BeamType", this.beamType.name());
        }

        public static BeamProperties read(CompoundTag tag) {
            float intensity = tag.getFloat("Intensity");
            Direction direction = Direction.from3DDataValue(tag.getInt("Direction"));
            int r = tag.getInt("ColorR");
            int g = tag.getInt("ColorG");
            int b = tag.getInt("ColorB");
            BeamType beamType = BeamType.valueOf(tag.getString("BeamType"));
            return new BeamProperties(intensity, direction, new Vec3i(r, g, b), beamType);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BeamProperties other)) return false;
            return this.intensity == other.intensity &&
                    this.direction == other.direction &&
                    this.color.equals(other.color) &&
                    this.beamType == other.beamType;
        }

        public BeamType getType() {
            return this.beamType;
        }
    }

    public enum BeamType {
        DEFAULT(64, true, (entity, props) -> {}, (state, props) -> {}),
        THERMAL(32, false, (entity, props) -> entity.setSecondsOnFire(5), (state, props) -> {});

        private final int range;
        private final boolean canPassThroughEntities;
        private final BiConsumer<LivingEntity, BeamProperties> livingEntityBiConsumer;
        private final BiConsumer<BlockState, BeamProperties> blockStateBiConsumer;

        BeamType(int range, boolean canPassThroughEntities,
                 BiConsumer<LivingEntity, BeamProperties> livingEntityBiConsumer,
                 BiConsumer<BlockState, BeamProperties> blockStateBiConsumer) {
            this.range = range;
            this.canPassThroughEntities = canPassThroughEntities;
            this.livingEntityBiConsumer = livingEntityBiConsumer;
            this.blockStateBiConsumer = blockStateBiConsumer;
        }

        public int getRange() {
            return range;
        }

        public boolean canPassThroughEntities() {
            return canPassThroughEntities;
        }
    }

    // 修复：正确混合颜色
    public static Vec3i colorSum(Vec3i color1, Vec3i color2) {
        int r = Math.min(255, (color1.getX() + color2.getX()) / 2);
        int g = Math.min(255, (color1.getY() + color2.getY()) / 2);
        int b = Math.min(255, (color1.getZ() + color2.getZ()) / 2);
        return new Vec3i(r, g, b);
    }

    // 将 DyeColor 转换为 Vec3i
    public static Vec3i dyeColorToVec3i(DyeColor dyeColor) {
        float[] rgb = dyeColor.getTextureDiffuseColors();
        int r = (int) (rgb[0] * 255);
        int g = (int) (rgb[1] * 255);
        int b = (int) (rgb[2] * 255);
        return new Vec3i(r, g, b);
    }

    public static void propagateLinearBeamVar(IBeamSource iBeamSource, BlockPos initialPos, BeamProperties beamProperties, int lastIndex) {
        if (iBeamSource.getInitialBeamProperties() == null) return;

        BlockPos lastPos = initialPos;
        Direction direction = beamProperties.direction;
        int range = 16; // 固定16格范围

        for (int i = 0; i + lastIndex <= range; i++) {
            lastPos = lastPos.relative(direction);
            Vec3i vec3 = lastPos;
            BlockState state = iBeamSource.getLevel().getBlockState(lastPos);
            boolean penetrable = state.isAir() || state.getLightBlock(iBeamSource.getLevel(), lastPos) == 0;

            if (state.getBlock() instanceof IBeamReceiver iBeamReceiver) {
                iBeamSource.addToBeamBlocks(initialPos, vec3, beamProperties);
                iBeamReceiver.receive(iBeamSource, state, lastPos, beamProperties, 0); // 重置lastIndex为0以支持16格新传播
                break;
            } else if (i + lastIndex >= range || !penetrable) {
                iBeamSource.addToBeamBlocks(initialPos, vec3, beamProperties);
                break;
            }
        }
    }
}