package com.adonis.transform;

import com.adonis.config.NaturalTransformConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 区块转换计数器 - 追踪每个区块中的转换方块数量，防止过度转换
 */
public class ChunkTransformCounter {
    
    // 使用ConcurrentHashMap保证线程安全
    private static final Map<ChunkCounterKey, Integer> CRACKED_ICE_COUNT = new ConcurrentHashMap<>();
    private static final Map<ChunkCounterKey, Integer> FROZEN_SOIL_COUNT = new ConcurrentHashMap<>();
    private static final Map<ChunkCounterKey, Integer> SALINE_BLOCKS_COUNT = new ConcurrentHashMap<>();
    
    // 清理间隔计数器
    private static int cleanupCounter = 0;
    private static final int CLEANUP_INTERVAL = 6000; // 每5分钟清理一次过期数据

    /**
     * 检查是否可以在指定区块添加破裂冰
     */
    public static boolean canAddCrackedIce(ServerLevel level, BlockPos pos) {
        ChunkCounterKey key = new ChunkCounterKey(level, pos);
        int currentCount = getCurrentCrackedIceCount(level, new ChunkPos(pos));
        int maxCount = NaturalTransformConfig.MAX_CRACKED_ICE_PER_CHUNK.get();
        
        return currentCount < maxCount;
    }

    /**
     * 检查是否可以在指定区块添加冻土
     */
    public static boolean canAddFrozenSoil(ServerLevel level, BlockPos pos) {
        ChunkCounterKey key = new ChunkCounterKey(level, pos);
        int currentCount = getCurrentFrozenSoilCount(level, new ChunkPos(pos));
        int maxCount = NaturalTransformConfig.MAX_FROZEN_SOIL_PER_CHUNK.get();
        
        return currentCount < maxCount;
    }

    /**
     * 检查是否可以在指定区块添加盐碱方块（仅限红树林沼泽）
     */
    public static boolean canAddSalineBlock(ServerLevel level, BlockPos pos) {
        ChunkCounterKey key = new ChunkCounterKey(level, pos);
        int currentCount = getCurrentSalineBlocksCount(level, new ChunkPos(pos));
        int maxCount = NaturalTransformConfig.MAX_SALINE_BLOCKS_PER_CHUNK.get();
        
        return currentCount < maxCount;
    }

    /**
     * 记录破裂冰的添加
     */
    public static void recordCrackedIceAdded(ServerLevel level, BlockPos pos) {
        ChunkCounterKey key = new ChunkCounterKey(level, pos);
        CRACKED_ICE_COUNT.merge(key, 1, Integer::sum);
        performPeriodicCleanup();
    }

    /**
     * 记录冻土的添加
     */
    public static void recordFrozenSoilAdded(ServerLevel level, BlockPos pos) {
        ChunkCounterKey key = new ChunkCounterKey(level, pos);
        FROZEN_SOIL_COUNT.merge(key, 1, Integer::sum);
        performPeriodicCleanup();
    }

    /**
     * 记录盐碱方块的添加
     */
    public static void recordSalineBlockAdded(ServerLevel level, BlockPos pos) {
        ChunkCounterKey key = new ChunkCounterKey(level, pos);
        SALINE_BLOCKS_COUNT.merge(key, 1, Integer::sum);
        performPeriodicCleanup();
    }

    /**
     * 获取区块中当前的破裂冰数量（通过实际扫描）
     */
    private static int getCurrentCrackedIceCount(ServerLevel level, ChunkPos chunkPos) {
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) return 0;
        
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        int count = 0;
        
        try {
            // 扫描整个区块
            int minY = level.getMinBuildHeight();
            int maxY = level.getMaxBuildHeight();
            
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = minY; y < maxY; y++) {
                        BlockPos pos = new BlockPos(chunkPos.x * 16 + x, y, chunkPos.z * 16 + z);
                        Block block = level.getBlockState(pos).getBlock();
                        
                        if (isCrackedIceBlock(block)) {
                            count++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 扫描失败时使用缓存的计数
            ChunkCounterKey key = new ChunkCounterKey(level, chunkPos);
            return CRACKED_ICE_COUNT.getOrDefault(key, 0);
        }
        
        return count;
    }

    /**
     * 获取区块中当前的冻土数量（通过实际扫描）
     */
    private static int getCurrentFrozenSoilCount(ServerLevel level, ChunkPos chunkPos) {
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) return 0;
        
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        int count = 0;
        
        try {
            // 扫描整个区块
            int minY = level.getMinBuildHeight();
            int maxY = level.getMaxBuildHeight();
            
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = minY; y < maxY; y++) {
                        BlockPos pos = new BlockPos(chunkPos.x * 16 + x, y, chunkPos.z * 16 + z);
                        Block block = level.getBlockState(pos).getBlock();
                        
                        if (isFrozenSoilBlock(block)) {
                            count++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 扫描失败时使用缓存的计数
            ChunkCounterKey key = new ChunkCounterKey(level, chunkPos);
            return FROZEN_SOIL_COUNT.getOrDefault(key, 0);
        }
        
        return count;
    }

    /**
     * 获取区块中当前的盐碱方块数量（通过实际扫描）
     */
    private static int getCurrentSalineBlocksCount(ServerLevel level, ChunkPos chunkPos) {
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) return 0;
        
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        int count = 0;
        
        try {
            // 扫描整个区块
            int minY = level.getMinBuildHeight();
            int maxY = level.getMaxBuildHeight();
            
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = minY; y < maxY; y++) {
                        BlockPos pos = new BlockPos(chunkPos.x * 16 + x, y, chunkPos.z * 16 + z);
                        Block block = level.getBlockState(pos).getBlock();
                        
                        if (isSalineBlock(block)) {
                            count++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 扫描失败时使用缓存的计数
            ChunkCounterKey key = new ChunkCounterKey(level, chunkPos);
            return SALINE_BLOCKS_COUNT.getOrDefault(key, 0);
        }
        
        return count;
    }

    /**
     * 定期清理过期的计数数据
     */
    private static void performPeriodicCleanup() {
        cleanupCounter++;
        if (cleanupCounter >= CLEANUP_INTERVAL) {
            cleanupCounter = 0;
            
            // 清理所有计数器
            CRACKED_ICE_COUNT.clear();
            FROZEN_SOIL_COUNT.clear();
            SALINE_BLOCKS_COUNT.clear();
            
            if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                com.adonis.CreateGeography.LOGGER.debug("Cleaned up chunk transform counters");
            }
        }
    }

    /**
     * 手动清理所有计数器
     */
    public static void clearAllCounters() {
        CRACKED_ICE_COUNT.clear();
        FROZEN_SOIL_COUNT.clear();
        SALINE_BLOCKS_COUNT.clear();
        cleanupCounter = 0;
    }

    // ==================== 工具方法 ====================

    private static boolean isCrackedIceBlock(Block block) {
        String blockName = block.toString();
        return blockName.contains("cracked_ice") || blockName.contains("cracked_packed_ice") || blockName.contains("cracked_blue_ice");
    }

    private static boolean isFrozenSoilBlock(Block block) {
        String blockName = block.toString();
        return blockName.contains("frozen_soil");
    }

    private static boolean isSalineBlock(Block block) {
        String blockName = block.toString();
        return blockName.contains("saline_mud") || blockName.contains("saline_soil") || blockName.contains("black_mud") || blockName.contains("black_soil");
    }

    /**
     * 区块计数器键 - 用于唯一标识世界中的区块
     */
    private static class ChunkCounterKey {
        private final String dimensionKey;
        private final int chunkX;
        private final int chunkZ;

        public ChunkCounterKey(ServerLevel level, BlockPos pos) {
            this.dimensionKey = level.dimension().location().toString();
            this.chunkX = pos.getX() >> 4;
            this.chunkZ = pos.getZ() >> 4;
        }

        public ChunkCounterKey(ServerLevel level, ChunkPos chunkPos) {
            this.dimensionKey = level.dimension().location().toString();
            this.chunkX = chunkPos.x;
            this.chunkZ = chunkPos.z;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ChunkCounterKey)) return false;
            ChunkCounterKey other = (ChunkCounterKey) obj;
            return chunkX == other.chunkX && chunkZ == other.chunkZ && dimensionKey.equals(other.dimensionKey);
        }

        @Override
        public int hashCode() {
            return dimensionKey.hashCode() * 31 * 31 + chunkX * 31 + chunkZ;
        }

        @Override
        public String toString() {
            return String.format("ChunkKey{%s, %d, %d}", dimensionKey, chunkX, chunkZ);
        }
    }
}