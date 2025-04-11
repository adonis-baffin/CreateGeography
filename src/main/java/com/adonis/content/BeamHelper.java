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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;

public class BeamHelper {
    private static final Logger LOGGER = LogManager.getLogger("BeamHelper");

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
    }

    public enum BeamType {
        DEFAULT(64, true, (entity, props) -> {}, (state, props) -> {}),
        THERMAL(32, false, (entity, props) -> entity.setSecondsOnFire(5), (state, props) -> {}),
        SOLAR(16, true, (entity, props) -> {}, (state, props) -> {});

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
    }

    public static Vec3i colorSum(Vec3i color1, Vec3i color2) {
        int r = Math.min(255, (color1.getX() + color2.getX()) / 2);
        int g = Math.min(255, (color1.getY() + color2.getY()) / 2);
        int b = Math.min(255, (color1.getZ() + color2.getZ()) / 2);
        return new Vec3i(r, g, b);
    }

    public static Vec3i dyeColorToVec3i(DyeColor dyeColor) {
        float[] rgb = dyeColor.getTextureDiffuseColors();
        int r = (int) (rgb[0] * 255);
        int g = (int) (rgb[1] * 255);
        int b = (int) (rgb[2] * 255);
        return new Vec3i(r, g, b);
    }

    public static void propagateLinearBeamVar(IBeamSource source, BlockPos initialPos, BeamProperties beamProperties, int lastIndex) {
        Level level = source.getLevel();
        if (level == null) return;

        BlockPos startPos = initialPos;
        Direction direction = beamProperties.direction;
        int range = 16;

        for (int i = 1; i <= range - lastIndex; i++) {
            BlockPos currentPos = startPos.relative(direction, i);
            if (!level.isLoaded(currentPos)) break;

            BlockState state = level.getBlockState(currentPos);
            boolean penetrable = state.isAir() || state.is(Blocks.LIGHT);

            Vec3i startVec = new Vec3i(startPos.getX(), startPos.getY(), startPos.getZ());
            Vec3i currentVec = new Vec3i(currentPos.getX(), currentPos.getY(), currentPos.getZ());
            source.addToBeamBlocks(startVec, currentVec, beamProperties);

            if (state.getBlock() instanceof IBeamReceiver receiver) {
                LOGGER.info("Beam from " + startPos + " hit receiver at " + currentPos);
                receiver.receive(source, state, startPos, beamProperties, lastIndex + i);
                break;
            } else if (!penetrable) {
                LOGGER.info("Beam from " + startPos + " obstructed at " + currentPos + " by " + state.getBlock());
                break;
            }
        }
    }
}