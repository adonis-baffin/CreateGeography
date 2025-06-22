package com.adonis.transform;

import com.adonis.config.NaturalTransformConfig;
import com.adonis.transform.ChunkTransformCounter;
import com.adonis.transform.TransformConditions;
import com.adonis.CreateGeography;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static com.adonis.fluid.GeographyFluids.BRINE;
import static com.adonis.fluid.GeographyFluids.ASH_WATER;
import static com.adonis.registry.BlockRegistry.*;

/**
 * 自然转换处理器 - 负责所有自然转换的核心逻辑
 * 基于区块的随机tick机制，类似原版的作物生长和水结冰
 */
@Mod.EventBusSubscriber(modid = CreateGeography.MODID)
public class NaturalTransformHandler {
    
    private static int globalTickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        globalTickCounter++;
        
        // 检查全局开关
        if (!NaturalTransformConfig.isNaturalTransformsEnabled()) return;
        
        // 按配置的间隔执行
        int interval = NaturalTransformConfig.PROCESS_INTERVAL_TICKS.get();
        if (globalTickCounter % interval != 0) return;

        try {
            for (ServerLevel level : event.getServer().getAllLevels()) {
                processLevelTransforms(level);
            }
        } catch (Exception e) {
            CreateGeography.LOGGER.error("❌ Error in natural transform handler: ", e);
        }
    }

    /**
     * 处理世界中的自然转换
     */
    private static void processLevelTransforms(ServerLevel level) {
        if (level.players().isEmpty()) return;
        
        // 类似原版机制：只处理玩家附近128格内的区块
        level.players().forEach(player -> {
            ChunkPos playerChunkPos = new ChunkPos(player.blockPosition());
            int chunkRadius = 8; // 128格 = 8区块
            
            for (int chunkX = playerChunkPos.x - chunkRadius; chunkX <= playerChunkPos.x + chunkRadius; chunkX++) {
                for (int chunkZ = playerChunkPos.z - chunkRadius; chunkZ <= playerChunkPos.z + chunkRadius; chunkZ++) {
                    
                    // 检查距离是否在128格内
                    double distanceToPlayer = Math.sqrt(
                        Math.pow((chunkX * 16 + 8) - player.getX(), 2) + 
                        Math.pow((chunkZ * 16 + 8) - player.getZ(), 2)
                    );
                    
                    if (distanceToPlayer <= 128) {
                        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
                        processChunkTransforms(level, chunkPos);
                    }
                }
            }
        });
    }

    /**
     * 处理单个区块的转换
     */
    private static void processChunkTransforms(ServerLevel level, ChunkPos chunkPos) {
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) return;
        
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        if (chunk == null) return;
        
        RandomSource random = level.random;
        int blocksPerTick = NaturalTransformConfig.BLOCKS_PER_CHUNK_TICK.get();
        
        // 类似原版randomTick：遍历区块的各个section
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        
        for (int sectionY = minY; sectionY < maxY; sectionY += 16) {
            // 每个section随机选择几个方块进行处理
            for (int i = 0; i < blocksPerTick; i++) {
                int localX = random.nextInt(16);
                int localY = random.nextInt(16);
                int localZ = random.nextInt(16);
                
                BlockPos pos = new BlockPos(
                    chunkPos.x * 16 + localX,
                    Math.min(sectionY + localY, maxY - 1),
                    chunkPos.z * 16 + localZ
                );
                
                try {
                    processBlockTransform(level, pos, random);
                } catch (Exception e) {
                    if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                        CreateGeography.LOGGER.debug("Error processing block at {}: {}", pos, e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 处理单个方块的转换
     */
    private static void processBlockTransform(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState state = level.getBlockState(pos);
        Block currentBlock = state.getBlock();

        // 1. 冰破裂转换
        if (TransformConditions.isIceBlock(currentBlock)) {
            processIceCracking(level, pos, currentBlock, random);
        }
        // 2. 土壤冻结转换
        else if (TransformConditions.isSoilBlock(currentBlock) || currentBlock == Blocks.MUD) {
            processSoilFreezing(level, pos, currentBlock, random);
        }
        
        // 3. 盐碱化转换（独立于上述条件）
        processSalinization(level, pos, currentBlock, random);
    }

    /**
     * 处理冰破裂转换
     */
    private static void processIceCracking(ServerLevel level, BlockPos pos, Block iceBlock, RandomSource random) {
        if (!NaturalTransformConfig.isIceCrackingEnabled()) return;
        
        // 获取目标破裂冰方块
        Block crackedIceBlock = getCrackedIceBlock(iceBlock);
        if (crackedIceBlock == null) return;
        
        // 检查条件
        if (!TransformConditions.IceCrackingConditions.canCrack(level, pos, iceBlock)) return;
        
        // 检查区块限制
        if (!ChunkTransformCounter.canAddCrackedIce(level, pos)) {
            if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                CreateGeography.LOGGER.debug("Chunk cracked ice limit reached at {}", pos);
            }
            return;
        }
        
        // 计算概率
        double probability = TransformConditions.IceCrackingConditions.getCrackProbability(level, pos, crackedIceBlock);
        
        if (random.nextFloat() < probability) {
            // 执行转换
            level.setBlock(pos, crackedIceBlock.defaultBlockState(), 3);
            ChunkTransformCounter.recordCrackedIceAdded(level, pos);
            
            logTransformation("Ice Cracking", iceBlock, crackedIceBlock, pos);
        }
    }

    /**
     * 处理土壤冻结转换
     */
    private static void processSoilFreezing(ServerLevel level, BlockPos pos, Block soilBlock, RandomSource random) {
        if (!NaturalTransformConfig.isSoilFreezingEnabled()) return;
        if (FROZEN_SOIL.get() == null) return;
        
        // 检查条件
        if (!TransformConditions.SoilFreezingConditions.canFreeze(level, pos, soilBlock)) return;
        
        // 检查区块限制
        if (!ChunkTransformCounter.canAddFrozenSoil(level, pos)) {
            if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                CreateGeography.LOGGER.debug("Chunk frozen soil limit reached at {}", pos);
            }
            return;
        }
        
        // 计算概率
        double probability = TransformConditions.SoilFreezingConditions.getFreezeProbability(level, pos);
        
        if (random.nextFloat() < probability) {
            // 执行转换
            level.setBlock(pos, FROZEN_SOIL.get().defaultBlockState(), 3);
            ChunkTransformCounter.recordFrozenSoilAdded(level, pos);
            
            logTransformation("Soil Freezing", soilBlock, FROZEN_SOIL.get(), pos);
        }
    }

    /**
     * 处理盐碱化转换
     */
    private static void processSalinization(ServerLevel level, BlockPos pos, Block currentBlock, RandomSource random) {
        if (!NaturalTransformConfig.isSalinizationEnabled()) return;
        
        // 1. 红树林沼泽中的泥巴盐碱化
        if (currentBlock == Blocks.MUD) {
            processMangroveSwampSalinization(level, pos, random);
        }
        
        // 2. 盐水附近的方块转换
        processBrineInfluence(level, pos, currentBlock, random);
        
        // 3. 灰水附近的方块转换
        processAshWaterInfluence(level, pos, currentBlock, random);
    }

    /**
     * 处理红树林沼泽中的盐碱化
     */
    private static void processMangroveSwampSalinization(ServerLevel level, BlockPos pos, RandomSource random) {
        if (SALINE_MUD.get() == null) return;
        
        // 检查条件
        if (!TransformConditions.SalinizationConditions.canSalinizeInMangrove(level, pos, Blocks.MUD)) return;
        
        // 检查区块限制
        if (!ChunkTransformCounter.canAddSalineBlock(level, pos)) {
            if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                CreateGeography.LOGGER.debug("Chunk saline block limit reached at {}", pos);
            }
            return;
        }
        
        // 计算概率
        double probability = TransformConditions.SalinizationConditions.getMangroveSalinizationProbability(level, pos, SALINE_MUD.get());
        
        if (random.nextFloat() < probability) {
            // 执行转换
            level.setBlock(pos, SALINE_MUD.get().defaultBlockState(), 3);
            ChunkTransformCounter.recordSalineBlockAdded(level, pos);
            
            logTransformation("Mangrove Salinization", Blocks.MUD, SALINE_MUD.get(), pos);
        }
    }

    /**
     * 处理盐水影响
     */
    private static void processBrineInfluence(ServerLevel level, BlockPos pos, Block currentBlock, RandomSource random) {
        if (BRINE.get() == null) return;
        
        Fluid brineFluid = BRINE.get().getSource();
        if (!TransformConditions.SalinizationConditions.canSalinizeNearBrine(level, pos, currentBlock, brineFluid)) return;
        
        double probability = TransformConditions.SalinizationConditions.getBrineProbability(currentBlock);
        if (random.nextFloat() < probability) {
            Block targetBlock = getBrineTransformTarget(currentBlock);
            if (targetBlock != null) {
                level.setBlock(pos, targetBlock.defaultBlockState(), 3);
                logTransformation("Brine Influence", currentBlock, targetBlock, pos);
            }
        }
    }

    /**
     * 处理灰水影响
     */
    private static void processAshWaterInfluence(ServerLevel level, BlockPos pos, Block currentBlock, RandomSource random) {
        if (ASH_WATER.get() == null) return;
        
        Fluid ashWaterFluid = ASH_WATER.get().getSource();
        if (!TransformConditions.SalinizationConditions.canBlackenNearAshWater(level, pos, currentBlock, ashWaterFluid)) return;
        
        double probability = TransformConditions.SalinizationConditions.getAshWaterProbability(currentBlock);
        if (random.nextFloat() < probability) {
            Block targetBlock = getAshWaterTransformTarget(currentBlock);
            if (targetBlock != null) {
                level.setBlock(pos, targetBlock.defaultBlockState(), 3);
                logTransformation("Ash Water Influence", currentBlock, targetBlock, pos);
            }
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取破裂冰方块
     */
    private static Block getCrackedIceBlock(Block iceBlock) {
        if (iceBlock == Blocks.ICE) {
            return CRACKED_ICE.get();
        } else if (iceBlock == Blocks.PACKED_ICE) {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "cracked_packed_ice"));
        } else if (iceBlock == Blocks.BLUE_ICE) {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "cracked_blue_ice"));
        }
        return null;
    }

    /**
     * 获取盐水转换目标方块
     */
    private static Block getBrineTransformTarget(Block sourceBlock) {
        if (sourceBlock == Blocks.MUD) {
            return SALINE_MUD.get();
        } else if (TransformConditions.isSoilBlock(sourceBlock)) {
            // 假设有盐碱土方块
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "saline_soil"));
        }
        return null;
    }

    /**
     * 获取灰水转换目标方块
     */
    private static Block getAshWaterTransformTarget(Block sourceBlock) {
        if (sourceBlock == Blocks.MUD) {
            // 假设有黑泥巴方块
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "black_mud"));
        } else if (TransformConditions.isSoilBlock(sourceBlock)) {
            // 假设有黑土方块
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "black_soil"));
        }
        return null;
    }

    /**
     * 记录转换日志
     */
    private static void logTransformation(String transformType, Block sourceBlock, Block targetBlock, BlockPos pos) {
        if (NaturalTransformConfig.isDebugLoggingEnabled()) {
            CreateGeography.LOGGER.info("🌍 {}: {} -> {} at {}", 
                transformType, sourceBlock, targetBlock, pos);
        }
    }
}