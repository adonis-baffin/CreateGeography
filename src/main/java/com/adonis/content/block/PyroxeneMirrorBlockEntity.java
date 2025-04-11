package com.adonis.content.block;

import com.adonis.content.BeamHelper;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PyroxeneMirrorBlockEntity extends KineticBlockEntity implements IBeamReceiver, IBeamSource {
    private static final Logger LOGGER = LogManager.getLogger("PyroxeneMirrorBlockEntity");

    public float rotVelocity;
    public float oAngle;
    public float angle;
    public float previousAngle;
    private static final float ANGLE_RANGE = 90F;
    private static final int MAX_BEAM_DISTANCE = 16;
    private static final int SUNLIGHT_LEVEL = 15;
    private static final float ROTATION_THRESHOLD = 5.0f;

    private @Nullable State state = State.PARALLEL;
    public int tickCount = 0;
    private boolean wasSunlightReflecting = false;
    private final List<BlockPos> lightPositions = new ArrayList<>();

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

        float targetAngle = getTargetAngle(actualSpeed);
        this.angle = Mth.lerp(0.15f, this.angle, targetAngle);
        this.angle = Mth.clamp(this.angle, 0F, ANGLE_RANGE);

        updateState();

        if (level.isClientSide) return;

        boolean canReflectNow = canReflectSunlight();
        boolean angleChanged = Math.abs(previousAngle - angle) > ROTATION_THRESHOLD;

        if (canReflectNow != wasSunlightReflecting || (canReflectNow && angleChanged)) {
            if (wasSunlightReflecting) clearLightSources();
            clearBeams();
            wasSunlightReflecting = canReflectNow;
            previousAngle = angle;

            if (canReflectNow) {
                createSunlightBeam();
                LOGGER.info("Sunlight beam created at " + worldPosition + " with angle " + angle);
            }
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
        }

        checkForObstructions();
    }

    private float getTargetAngle(float speed) {
        return speed > 0 ? ANGLE_RANGE : speed < 0 ? 0F : angle;
    }

    private void updateState() {
        float i = angle / 9F;
        if (i >= 9.5) this.state = State.PERPENDICULAR;
        else if (i <= 0.5) this.state = State.PARALLEL;
        else this.state = (angle >= 0 && angle < 30) || (angle >= 60 && angle <= 90) ? null : (angle > 45 ? State.PERPENDICULAR : State.PARALLEL);
    }

    public void clearLightSources() {
        for (BlockPos lightPos : lightPositions) {
            BlockState state = level.getBlockState(lightPos);
            if (state.is(Blocks.LIGHT)) level.setBlock(lightPos, Blocks.AIR.defaultBlockState(), 3);
        }
        lightPositions.clear();
        LOGGER.info("Cleared light sources at " + worldPosition);
    }

    private void clearBeams() {
        beamBlocks.clear();
        dependents.clear();
    }

    private void createSunlightBeam() {
        Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING);
        Direction reflectedDirection = calculateReflectedDirection(facing);

        if (reflectedDirection == null) return;

        BeamHelper.BeamProperties sunBeam = new BeamHelper.BeamProperties(
                1.0F, reflectedDirection, new Vec3i(255, 255, 200), BeamHelper.BeamType.DEFAULT
        );

        Vec3i startVec = new Vec3i(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
        Vec3i endVec = startVec.offset(reflectedDirection.getNormal());
        addToBeamBlocks(startVec, endVec, sunBeam);

        BeamHelper.propagateLinearBeamVar(this, getBlockPos(), sunBeam, 0);
        propagateBeamWithLight(getBlockPos(), reflectedDirection, MAX_BEAM_DISTANCE);
    }

    private Direction calculateReflectedDirection(Direction facing) {
        if (facing.getAxis().isVertical()) return facing.getOpposite();

        if (angle >= 60 && angle <= 90) {
            if (facing == Direction.NORTH) return Direction.WEST;
            if (facing == Direction.SOUTH) return Direction.EAST;
            if (facing == Direction.EAST) return Direction.SOUTH;
            if (facing == Direction.WEST) return Direction.NORTH;
        } else if (angle >= 0 && angle < 30) {
            if (facing == Direction.NORTH) return Direction.EAST;
            if (facing == Direction.SOUTH) return Direction.WEST;
            if (facing == Direction.EAST) return Direction.SOUTH;
            if (facing == Direction.WEST) return Direction.NORTH;
        }
        return null;
    }

    private void propagateBeamWithLight(BlockPos startPos, Direction direction, int remainingDistance) {
        if (remainingDistance <= 0 || level == null || direction == null) return;

        BlockPos currentPos = startPos;
        for (int i = 0; i < remainingDistance; i++) {
            currentPos = currentPos.relative(direction);
            BlockState state = level.getBlockState(currentPos);

            if (!state.isAir() && !state.is(Blocks.LIGHT)) {
                if (i > 0) placeLightSource(currentPos.relative(direction.getOpposite()));
                break;
            }
            placeLightSource(currentPos);
        }
    }

    private void placeLightSource(BlockPos pos) {
        BlockState existingState = level.getBlockState(pos);
        if (existingState.isAir() || existingState.is(Blocks.LIGHT)) {
            BlockState lightState = Blocks.LIGHT.defaultBlockState().setValue(BlockStateProperties.LEVEL, SUNLIGHT_LEVEL);
            level.setBlock(pos, lightState, 3);
            if (!lightPositions.contains(pos)) lightPositions.add(pos);
            LOGGER.info("Light source placed at " + pos + " from " + worldPosition);
        }
    }

    private void checkForObstructions() {
        if (!wasSunlightReflecting) return;

        Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING);
        Direction reflectedDirection = calculateReflectedDirection(facing);
        if (reflectedDirection == null) return;

        List<BlockPos> toRemove = new ArrayList<>();
        BlockPos currentPos = worldPosition;
        boolean obstructed = false;

        for (int i = 0; i < MAX_BEAM_DISTANCE; i++) {
            currentPos = currentPos.relative(reflectedDirection);
            BlockState state = level.getBlockState(currentPos);

            if (!state.isAir() && !state.is(Blocks.LIGHT)) {
                final BlockPos obstructingPos = currentPos;
                toRemove.addAll(lightPositions.stream()
                        .filter(pos -> pos.distManhattan(worldPosition) >= obstructingPos.distManhattan(worldPosition))
                        .toList());
                LOGGER.info("Obstruction detected at " + currentPos + ", removing " + toRemove.size() + " light sources from " + worldPosition);
                obstructed = true;
                break;
            }
        }

        for (BlockPos pos : toRemove) {
            if (level.getBlockState(pos).is(Blocks.LIGHT)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
            lightPositions.remove(pos);
        }

        if (!obstructed && lightPositions.size() < MAX_BEAM_DISTANCE) {
            int remainingDistance = MAX_BEAM_DISTANCE - lightPositions.size();
            propagateBeamWithLight(worldPosition, reflectedDirection, remainingDistance);
            LOGGER.info("Restored " + remainingDistance + " light sources at " + worldPosition);
        }
    }

    private boolean canReflectSunlight() {
        if (level == null || level.isClientSide) return false;

        long dayTime = level.getDayTime() % 24000;
        if (!(dayTime >= 0 && dayTime < 12000)) return false;

        Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING);
        if (facing.getAxis().isVertical()) return false;

        boolean validAngle = (angle >= 0 && angle < 30) || (angle >= 60 && angle <= 90);
        return validAngle;
    }

    public @Nullable State getState() {
        return this.state;
    }

    public boolean isRotating() {
        return Math.abs(this.oAngle - this.angle) > 0.01F;
    }

    public float getIndependentAngle(float partialTicks) {
        return Mth.lerp(partialTicks, this.oAngle, this.angle);
    }

    public @Nullable Direction getReflectedDirection(Direction dir, BlockState state) {
        Direction facing = state.getValue(DirectionalKineticBlock.FACING);
        if (angle >= 60 && angle <= 90) {
            if (facing == Direction.NORTH) return dir == Direction.EAST ? Direction.WEST : (dir == Direction.WEST ? Direction.EAST : null);
            if (facing == Direction.SOUTH) return dir == Direction.WEST ? Direction.EAST : (dir == Direction.EAST ? Direction.WEST : null);
            if (facing == Direction.EAST) return dir == Direction.NORTH ? Direction.SOUTH : (dir == Direction.SOUTH ? Direction.NORTH : null);
            if (facing == Direction.WEST) return dir == Direction.SOUTH ? Direction.NORTH : (dir == Direction.NORTH ? Direction.SOUTH : null);
        } else if (angle >= 0 && angle < 30) {
            if (facing == Direction.NORTH) return dir == Direction.WEST ? Direction.EAST : (dir == Direction.EAST ? Direction.WEST : null);
            if (facing == Direction.SOUTH) return dir == Direction.EAST ? Direction.WEST : (dir == Direction.WEST ? Direction.EAST : null);
            if (facing == Direction.EAST) return dir == Direction.SOUTH ? Direction.NORTH : (dir == Direction.NORTH ? Direction.SOUTH : null);
            if (facing == Direction.WEST) return dir == Direction.NORTH ? Direction.SOUTH : (dir == Direction.SOUTH ? Direction.NORTH : null);
        }
        return null;
    }

    @Override
    public void receive(IBeamSource source, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        Direction direction = beamProperties.direction;
        Direction reflectedDirection = getReflectedDirection(direction, state);

        if (reflectedDirection == null) return;

        LOGGER.info("Mirror at " + worldPosition + " received beam from " + lastPos + ", reflecting to " + reflectedDirection);
        clearLightSources();
        clearBeams();

        int remainingDistance = MAX_BEAM_DISTANCE - lastIndex;
        if (remainingDistance <= 0) return;

        BeamHelper.BeamProperties reflectedProperties = BeamHelper.BeamProperties.withDirection(beamProperties, reflectedDirection);
        Vec3i startVec = new Vec3i(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
        Vec3i endVec = startVec.offset(reflectedDirection.getNormal());
        addToBeamBlocks(startVec, endVec, reflectedProperties);
        addDependent(lastPos);

        BeamHelper.propagateLinearBeamVar(this, getBlockPos(), reflectedProperties, lastIndex + 1);
        propagateBeamWithLight(getBlockPos(), reflectedDirection, remainingDistance);
    }

    @Override
    public boolean useCenteredIncidence() {
        return true;
    }

    @Override
    public BeamHelper.BeamProperties getInitialBeamProperties() {
        if (canReflectSunlight()) {
            Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING);
            Direction reflectedDirection = calculateReflectedDirection(facing);
            if (reflectedDirection != null) {
                return new BeamHelper.BeamProperties(
                        1.0F, reflectedDirection, new Vec3i(255, 255, 200), BeamHelper.BeamType.DEFAULT
                );
            }
        }
        return null;
    }

    @Override
    public void addToBeamBlocks(Vec3i startPos, Vec3i endPos, BeamHelper.BeamProperties beamProperties) {
        beamBlocks.put(com.mojang.datafixers.util.Pair.of(startPos, endPos), beamProperties);
    }

    @Override
    public BlockPos getBlockPos() {
        return worldPosition;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Map<com.mojang.datafixers.util.Pair<Vec3i, Vec3i>, BeamHelper.BeamProperties> getBeamPropertiesMap() {
        return beamBlocks;
    }

    @Override
    public boolean isDependent(BlockPos pos) {
        return dependents.contains(pos);
    }

    @Override
    public void addDependent(BlockPos pos) {
        dependents.add(pos);
    }

    @Override
    public int getTickCount() {
        return tickCount;
    }

    @Override
    public boolean shouldRendererLaserBeam() {
        return true;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        previousAngle = angle;
        if (wasSunlightReflecting && !canReflectSunlight()) {
            clearLightSources();
            wasSunlightReflecting = false;
        } else if (!wasSunlightReflecting && canReflectSunlight()) {
            createSunlightBeam();
            wasSunlightReflecting = true;
        }
    }

    @Override
    public void remove() {
        if (!level.isClientSide) clearLightSources();
        super.remove();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.angle = compound.getFloat("AngularPosition");
        this.oAngle = this.angle;
        this.previousAngle = this.angle;
        this.rotVelocity = compound.getFloat("AngularVelocity");
        this.wasSunlightReflecting = compound.getBoolean("WasReflecting");

        lightPositions.clear();
        int[] posArray = compound.getIntArray("LightPositions");
        for (int i = 0; i < posArray.length; i += 3) {
            if (i + 2 < posArray.length) lightPositions.add(new BlockPos(posArray[i], posArray[i + 1], posArray[i + 2]));
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("AngularPosition", this.angle);
        compound.putFloat("AngularVelocity", this.rotVelocity);
        compound.putBoolean("WasReflecting", this.wasSunlightReflecting);

        int[] posArray = new int[lightPositions.size() * 3];
        for (int i = 0; i < lightPositions.size(); i++) {
            BlockPos pos = lightPositions.get(i);
            posArray[i * 3] = pos.getX();
            posArray[i * 3 + 1] = pos.getY();
            posArray[i * 3 + 2] = pos.getZ();
        }
        compound.putIntArray("LightPositions", posArray);
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
    }
}