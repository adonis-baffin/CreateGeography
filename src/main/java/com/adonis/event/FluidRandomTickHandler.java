//package com.adonis.event;
//
//import com.adonis.config.NaturalTransformConfig;
//import com.adonis.fluid.GeographyFluids;
//import com.adonis.content.block.SalineFarmlandBlock;
//import com.adonis.registry.BlockRegistry;
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerChunkCache;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.level.block.FarmBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.chunk.LevelChunk;
//import net.minecraft.world.level.material.FluidState;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
///**
// * 流体随机刻处理器
// * 处理盐水的随机刻行为，包括影响周围方块
// */
//@Mod.EventBusSubscriber
//public class FluidRandomTickHandler {
//
//    @SubscribeEvent
//    public static void onWorldTick(TickEvent.LevelTickEvent event) {
//        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) {
//            return;
//        }
//
//        if (!NaturalTransformConfig.ENABLE_BRINE_TRANSFORM.get()) {
//            return;
//        }
//
//        ServerLevel level = (ServerLevel) event.level;
//        ServerChunkCache chunkSource = level.getChunkSource();
//        RandomSource random = level.random;
//
//        // 模拟流体随机刻
//        chunkSource.chunkMap.getChunks().forEach(chunk -> {
//            if (chunk != null && level.shouldTickBlocksAt(chunk.getPos().toLong())) {
//                processChunkFluids(level, chunk, random);
//            }
//        });
//    }
//
//    private static void processChunkFluids(ServerLevel level, LevelChunk chunk, RandomSource random) {
//        // 每个区块每tick有小概率处理流体
//        if (random.nextFloat() > 0.05f) { // 5%概率，可调整
//            return;
//        }
//
//        // 在区块中随机选择一些位置检查
//        int attempts = 3; // 每个区块尝试3个位置
//        for (int i = 0; i < attempts; i++) {
//            int x = chunk.getPos().getMinBlockX() + random.nextInt(16);
//            int z = chunk.getPos().getMinBlockZ() + random.nextInt(16);
//            int y = random.nextInt(level.getHeight());
//
//            BlockPos pos = new BlockPos(x, y, z);
//            FluidState fluidState = level.getFluidState(pos);
//
//            // 检查是否为盐水
//            if (fluidState.getType() == GeographyFluids.BRINE.get().getSource() ||
//                fluidState.getType() == GeographyFluids.BRINE.get().getFlowing()) {
//
//                processBrineEffects(level, pos, random);
//            }
//        }
//    }
//
//    private static void processBrineEffects(ServerLevel level, BlockPos brinePos, RandomSource random) {
//        int range = NaturalTransformConfig.BRINE_TRANSFORM_RANGE.get();
//
//        // 影响周围方块
//        for (BlockPos nearbyPos : BlockPos.betweenClosed(
//                brinePos.offset(-range, -1, -range),
//                brinePos.offset(range, 1, range))) {
//
//            if (nearbyPos.equals(brinePos)) continue;
//
//            BlockState nearbyState = level.getBlockState(nearbyPos);
//
//            // 特殊处理耕地的湿润和盐碱化
//            if (nearbyState.getBlock() instanceof FarmBlock ||
//                nearbyState.getBlock() == BlockRegistry.SALINE_FARMLAND.get()) {
//
//                handleFarmlandNearBrine(level, nearbyPos, nearbyState, random);
//            }
//            // 其他土壤转换
//            else if (random.nextFloat() < NaturalTransformConfig.BRINE_TRANSFORM_CHANCE.get()) {
//                NaturalTransformHandler.transformSoilToSaline(level, nearbyPos, nearbyState);
//            }
//        }
//    }
//
//    private static void handleFarmlandNearBrine(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
//        if (state.getBlock() instanceof FarmBlock) {
//            // 普通耕地转换为盐碱耕地
//            if (random.nextFloat() < NaturalTransformConfig.BRINE_TRANSFORM_CHANCE.get() * 0.5f) {
//                int moisture = state.getValue(FarmBlock.MOISTURE);
//                level.setBlock(pos,
//                        BlockRegistry.SALINE_FARMLAND.get().defaultBlockState()
//                                .setValue(SalineFarmlandBlock.MOISTURE, 7) // 被盐水湿润
//                                .setValue(SalineFarmlandBlock.SALINITY, 1), 2);
//            }
//        } else if (state.getBlock() == BlockRegistry.SALINE_FARMLAND.get()) {
//            // 盐碱耕地增加盐度
//            int currentSalinity = state.getValue(SalineFarmlandBlock.SALINITY);
//            if (currentSalinity < 3 && random.nextFloat() < NaturalTransformConfig.BRINE_TRANSFORM_CHANCE.get() * 0.3f) {
//                level.setBlock(pos, state
//                        .setValue(SalineFarmlandBlock.MOISTURE, 7)
//                        .setValue(SalineFarmlandBlock.SALINITY, currentSalinity + 1), 2);
//            }
//        }
//    }
//}