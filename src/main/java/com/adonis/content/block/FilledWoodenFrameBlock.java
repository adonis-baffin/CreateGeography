package com.adonis.content.block;

import com.adonis.registry.BlockRegistry;
import com.adonis.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class FilledWoodenFrameBlock extends ThinFrameBlock {
    public FilledWoodenFrameBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this == BlockRegistry.BRINE_FILLED_WOODEN_FRAME.get() && random.nextFloat() < 0.1f) {
            level.setBlock(pos, BlockRegistry.SALT_FILLED_WOODEN_FRAME.get().defaultBlockState(), 3);
        }
        if (this == BlockRegistry.MUD_FILLED_WOODEN_FRAME.get() && random.nextFloat() < 0.1f) {
            level.setBlock(pos, BlockRegistry.DIRT_CLOD_FILLED_WOODEN_FRAME.get().defaultBlockState(), 3);
        }
        if (this == BlockRegistry.SAND_SLURRY_FILLED_WOODEN_FRAME.get() && random.nextFloat() < 0.1f) {
            level.setBlock(pos, BlockRegistry.SAND_DUST_FILLED_WOODEN_FRAME.get().defaultBlockState(), 3);
        }
    }
}