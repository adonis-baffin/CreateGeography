package com.adonis.content.block;

import com.adonis.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FrozenSoilBlock extends Block {
    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

    public FrozenSoilBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SNOWY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        return this.defaultBlockState().setValue(SNOWY, this.isSnowAbove(context.getLevel(), blockpos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (direction == Direction.UP) {
            return state.setValue(SNOWY, this.isSnowAbove(level, currentPos));
        }
        return state;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.shouldMelt(level, pos)) {
            // 融化成泥巴
            level.setBlockAndUpdate(pos, Blocks.MUD.defaultBlockState());
        }
    }

    private boolean shouldMelt(ServerLevel level, BlockPos pos) {
        // 1. 光照等级检查 - 修正的方法
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        int totalLight = Math.max(blockLight, skyLight);

        if (totalLight > 11) {
            return true;
        }

        // 或者更简单的方法：
        // int lightLevel = level.getMaxLocalRawBrightness(pos);
        // if (lightLevel > 11) {
        //     return true;
        // }

        // 2. 生物群系温度检查
        Biome biome = level.getBiome(pos).value();
        float temperature = biome.getBaseTemperature();
        if (temperature > 0.8f) {
            return true;
        }

        // 3. 检查附近是否有热源方块
        if (this.hasNearbyHeatSource(level, pos)) {
            return true;
        }

        // 4. 检查是否在下界
        if (level.dimension() == ServerLevel.NETHER) {
            return true;
        }

        return false;
    }

    private boolean hasNearbyHeatSource(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.is(Blocks.LAVA) ||
                    neighborState.is(Blocks.MAGMA_BLOCK) ||
                    neighborState.is(Blocks.FIRE) ||
                    neighborState.is(Blocks.CAMPFIRE) ||
                    neighborState.is(Blocks.FURNACE) ||
                    neighborState.is(Blocks.BLAST_FURNACE)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSnowAbove(LevelReader level, BlockPos pos) {
        BlockState upState = level.getBlockState(pos.above());
        return upState.is(Blocks.SNOW) || upState.is(Blocks.SNOW_BLOCK) || upState.is(Blocks.POWDER_SNOW);
    }
}