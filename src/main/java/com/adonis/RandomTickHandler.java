//package com.adonis;
//
//import com.adonis.registry.BlockRegistry;
//import com.adonis.utils.BlockTransformUtils;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraftforge.event.level.BlockEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//@Mod.EventBusSubscriber(modid = "creategeography")
//public class RandomTickHandler {
//
//    @SubscribeEvent
//    public static void onBlockTick(BlockEvent event) {
//        BlockState state = event.getState();
//        // 处理 DIRT 和 GRASS_BLOCK
//        if (state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) {
//            System.out.println("[RandomTickHandler] Block tick for " + state.getBlock() + " at " + event.getPos());
//            if (event.getLevel() instanceof ServerLevel level) {
//                BlockPos pos = event.getPos();
//                // 检查是否在冰雪群系
//                if (BlockTransformUtils.isSpecificBiome(level, pos, "snowy_tundra") ||
//                        BlockTransformUtils.isSpecificBiome(level, pos, "snowy_taiga") ||
//                        BlockTransformUtils.isSpecificBiome(level, pos, "frozen_ocean")) {
//                    int lightLevel = level.getMaxLocalRawBrightness(pos);
//                    System.out.println("[RandomTickHandler] " + state.getBlock() + " at " + pos + ", light: " + lightLevel);
//                    if (lightLevel <= 9) {
//                        level.setBlock(pos, BlockRegistry.FROZEN_DIRT.get().defaultBlockState(), 3);
//                    }
//                }
//            }
//        }
//    }
//}