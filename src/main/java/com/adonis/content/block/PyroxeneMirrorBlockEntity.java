package com.adonis.content.block;

import com.adonis.content.BeamHelper;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PyroxeneMirrorBlockEntity extends KineticBlockEntity implements IBeamReceiver {

    public float rotVelocity;
    public float oAngle;
    public float angle;
    private static final float ANGLE_RANGE = 90F;
    private @Nullable State state = null;
    public int tickCount = 0;
    public Map<com.mojang.datafixers.util.Pair<Vec3i, Vec3i>, BeamHelper.BeamProperties> beamBlocks = new HashMap<>();
    public Set<BlockPos> dependents = new HashSet<>();

    public PyroxeneMirrorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;
        float actualSpeed = getSpeed();
        rotVelocity = actualSpeed / (90F / 21F);

        this.oAngle = angle;
        this.angle = Mth.clamp(this.angle + rotVelocity, 0F, ANGLE_RANGE);

        if (isReceivingSunlight()) {
            BeamHelper.BeamProperties sunBeam = new BeamHelper.BeamProperties(
                    1.0F, this.getBlockState().getValue(DirectionalKineticBlock.FACING), new Vec3i(255, 255, 255), BeamHelper.BeamType.DEFAULT
            );
            Direction reflected = getReflectedDirection(Direction.DOWN, this.getBlockState());
            if (reflected != null) {
                BeamHelper.BeamProperties reflectedBeam = BeamHelper.BeamProperties.withDirection(sunBeam, reflected);
                IBeamSource.propagateLinearBeamVar((IBeamSource) this.getBlockState().getBlock(), this.getBlockPos(), reflectedBeam, 0);
            }
        }
    }

    public boolean isReceivingSunlight() {
        if (level == null || !level.isDay()) return false;
        Direction facing = this.getBlockState().getValue(DirectionalKineticBlock.FACING);
        float angleToUp = Math.abs(this.angle - 45.0F);
        if (angleToUp > 10.0F) return false;

        BlockPos posAbove = this.getBlockPos().above();
        for (int i = 0; i < level.getHeight() - posAbove.getY(); i++) {
            BlockState stateAbove = level.getBlockState(posAbove.offset(0, i, 0));
            if (!stateAbove.isAir() && stateAbove.getLightBlock(level, posAbove.offset(0, i, 0)) > 0) {
                return false;
            }
        }
        return true;
    }

    public @Nullable State getState() {
        return this.state;
    }

    public boolean isRotating() {
        return this.oAngle != this.angle;
    }

    public float getIndependentAngle(float partialTicks) {
        return Mth.clamp(this.angle + partialTicks * this.rotVelocity, 0F, ANGLE_RANGE);
    }

    public @Nullable Direction getReflectedDirection(Direction dir, BlockState state) {
        if (this.getState() == null) return null;
        Direction facing = this.getBlockState().getValue(DirectionalKineticBlock.FACING);
        Direction direction = null;

        if (facing.getAxis().isVertical()) {
            if (dir.getAxis().isHorizontal()) {
                direction = dir.getCounterClockWise();
                if (dir.getAxis().equals(Direction.Axis.X)) {
                    direction = direction.getOpposite();
                }
            }
        } else {
            if (dir.getAxis().isVertical()) {
                if (facing.getAxis().equals(Direction.Axis.X)) {
                    direction = Direction.NORTH;
                } else {
                    direction = Direction.WEST;
                }
                if (facing.getAxisDirection().equals(Direction.AxisDirection.POSITIVE)) {
                    direction = direction.getOpposite();
                }
                if (dir.getAxisDirection().equals(Direction.AxisDirection.NEGATIVE)) {
                    direction = direction.getOpposite();
                }
            } else {
                if (!dir.getAxis().equals(facing.getAxis())) {
                    direction = Direction.UP;
                    if (facing.getAxisDirection().equals(Direction.AxisDirection.NEGATIVE)) {
                        direction = direction.getOpposite();
                    }
                    if (dir.getAxisDirection().equals(Direction.AxisDirection.NEGATIVE)) {
                        direction = direction.getOpposite();
                    }
                }
            }
        }

        if (direction != null && !this.getState().isParallel()) {
            direction = direction.getOpposite();
        }

        return direction;
    }

    @Override
    public boolean useCenteredIncidence() {
        return true;
    }

    @Override
    public void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        Direction direction = beamProperties.direction;
        Direction reflectedDirection = getReflectedDirection(direction, state);
        if (reflectedDirection != null) {
            BeamHelper.BeamProperties reflectedProperties = BeamHelper.BeamProperties.withDirection(beamProperties, reflectedDirection);
            IBeamSource.propagateLinearBeamVar(iBeamSource, lastPos, reflectedProperties, lastIndex);
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (compound.contains("AngularPosition")) {
            this.angle = compound.getFloat("AngularPosition");
        }
        if (compound.contains("AngularVelocity")) {
            this.rotVelocity = compound.getFloat("AngularVelocity");
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("AngularPosition", this.angle);
        compound.putFloat("AngularVelocity", this.rotVelocity);
    }

    public enum State {
        PARALLEL(0),
        PERPENDICULAR(1);

        private final int id;

        State(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public State getPerpendicular() {
            return this == PARALLEL ? PERPENDICULAR : PARALLEL;
        }

        public boolean isParallel() {
            return this == PARALLEL;
        }

        public double getAngle() {
            return this == PARALLEL ? 0D : Math.PI / 2D;
        }
    }
}