package com.adonis.content.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import com.adonis.registry.BlockEntityRegistry;
import com.adonis.content.BeamHelper;

public class PyroxeneHeaterBlock extends BaseEntityBlock implements IWrenchable, IBeamReceiver {
    public static final EnumProperty<BlazeBurnerBlock.HeatLevel> HEAT_LEVEL = EnumProperty.create("blaze", BlazeBurnerBlock.HeatLevel.class);

    public PyroxeneHeaterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HEAT_LEVEL);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos.above());
            if (blockEntity instanceof BasinBlockEntity) {
                ((BasinBlockEntity) blockEntity).notifyChangeOfContents();
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.PYROXENE_HEATER.get().create(pos, state);
    }

    @Override
    public void receive(IBeamSource source, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        BlockEntity be = source.getLevel().getBlockEntity(source.getBlockPos());
        if (be instanceof PyroxeneHeaterBlockEntity heater) {
            heater.receiveLight(beamProperties.direction);
        }
    }

    @Override
    public boolean useCenteredIncidence() {
        return true;
    }
}