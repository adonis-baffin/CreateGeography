package com.adonis.utils;

import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一的溜槽连接检测工具类
 * 解决多个地方重复代码和逻辑不一致的问题
 */
public class ChuteConnectionHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("ChuteConnection");
    
    /**
     * 检查指定方向是否有溜槽连接
     * 这是统一的检测逻辑，所有地方都应该使用这个方法
     */
    public static boolean hasDirectionalChute(BlockGetter level, BlockPos pos, Direction direction) {
        LOGGER.debug("检查方向 {} 的溜槽连接，基准位置: {}", direction, pos);
        
        BlockPos chutePos;

        // 根据方向计算溜槽位置
        if (direction.getAxis().isHorizontal()) {
            // 水平方向：检查斜上方位置
            chutePos = pos.above().relative(direction);
            LOGGER.debug("水平方向 - 检查斜上方位置: {}", chutePos);
        } else {
            // 垂直方向：直接检查对应位置
            chutePos = pos.relative(direction);
            LOGGER.debug("垂直方向 - 检查位置: {}", chutePos);
        }

        BlockState chuteState = level.getBlockState(chutePos);
        LOGGER.debug("找到方块: {}", chuteState.getBlock().getClass().getSimpleName());
        
        if (!AbstractChuteBlock.isChute(chuteState)) {
            LOGGER.debug("不是溜槽方块");
            return false;
        }

        Direction chuteFacing = AbstractChuteBlock.getChuteFacing(chuteState);
        LOGGER.debug("溜槽朝向: {}", chuteFacing);
        
        // 连接判断逻辑
        boolean result = checkConnection(direction, chuteFacing);
        
        LOGGER.debug("方向 {} 连接检查结果: {}", direction, result);
        return result;
    }
    
    /**
     * 核心连接判断逻辑
     */
    private static boolean checkConnection(Direction expectedDirection, Direction chuteFacing) {
        if (expectedDirection.getAxis().isHorizontal()) {
            // 水平连接：接受以下情况
            // 1. 溜槽朝向与期望方向一致（完美匹配）
            // 2. 溜槽朝向DOWN（机械动力的默认行为）
            boolean perfectMatch = (chuteFacing == expectedDirection);
            boolean downwardChute = (chuteFacing == Direction.DOWN);
            
            boolean result = perfectMatch || downwardChute;
            
            LOGGER.debug("水平连接判断:");
            LOGGER.debug("  期望方向: {}", expectedDirection);
            LOGGER.debug("  溜槽朝向: {}", chuteFacing);
            LOGGER.debug("  完美匹配: {}", perfectMatch);
            LOGGER.debug("  向下溜槽: {}", downwardChute);
            LOGGER.debug("  连接结果: {}", result);
            
            return result;
            
        } else if (expectedDirection == Direction.UP) {
            // 向上连接：溜槽必须朝向DOWN
            boolean result = (chuteFacing == Direction.DOWN);
            LOGGER.debug("向上连接: 溜槽朝向({}) == DOWN = {}", chuteFacing, result);
            return result;
            
        } else if (expectedDirection == Direction.DOWN) {
            // 向下连接：溜槽必须朝向UP
            boolean result = (chuteFacing == Direction.UP);
            LOGGER.debug("向下连接: 溜槽朝向({}) == UP = {}", chuteFacing, result);
            return result;
        }

        return false;
    }
    
    /**
     * 批量检测所有方向的连接状态
     * 返回一个布尔数组，顺序为 [NORTH, SOUTH, EAST, WEST, UP, DOWN]
     */
    public static boolean[] checkAllDirections(BlockGetter level, BlockPos pos) {
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};
        boolean[] results = new boolean[6];
        
        for (int i = 0; i < directions.length; i++) {
            results[i] = hasDirectionalChute(level, pos, directions[i]);
        }
        
        return results;
    }
    
    /**
     * 获取指定方向的连接状态（带调试信息）
     */
    public static boolean hasDirectionalChuteWithDebug(BlockGetter level, BlockPos pos, Direction direction) {
        LOGGER.info("=== 详细检查方向 {} 的连接，位置: {} ===", direction, pos);
        boolean result = hasDirectionalChute(level, pos, direction);
        LOGGER.info("=== 方向 {} 连接检查完成，结果: {} ===", direction, result);
        return result;
    }
}