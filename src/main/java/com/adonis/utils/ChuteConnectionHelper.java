//package com.adonis.utils;
//
//import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.block.state.BlockState;
//
///**
// * 恢复原本工作的溜槽连接检测工具类
// * 保持原本逻辑不变，只移除日志输出
// */
//public class ChuteConnectionHelper {
//
//    /**
//     * 检查指定方向是否有溜槽连接
//     * 这是统一的检测逻辑，所有地方都应该使用这个方法
//     */
//    public static boolean hasDirectionalChute(BlockGetter level, BlockPos pos, Direction direction) {
//        BlockPos chutePos;
//
//        // 根据方向计算溜槽位置
//        if (direction.getAxis().isHorizontal()) {
//            // 水平方向：检查斜上方位置
//            chutePos = pos.above().relative(direction);
//        } else {
//            // 垂直方向：直接检查对应位置
//            chutePos = pos.relative(direction);
//        }
//
//        BlockState chuteState = level.getBlockState(chutePos);
//
//        if (!AbstractChuteBlock.isChute(chuteState)) {
//            return false;
//        }
//
//        Direction chuteFacing = AbstractChuteBlock.getChuteFacing(chuteState);
//
//        // 连接判断逻辑
//        return checkConnection(direction, chuteFacing);
//    }
//
//    /**
//     * 核心连接判断逻辑
//     */
//    private static boolean checkConnection(Direction expectedDirection, Direction chuteFacing) {
//        if (expectedDirection.getAxis().isHorizontal()) {
//            // 水平连接：接受以下情况
//            // 1. 溜槽朝向与期望方向一致（完美匹配）
//            // 2. 溜槽朝向DOWN（机械动力的默认行为）
//            boolean perfectMatch = (chuteFacing == expectedDirection);
//            boolean downwardChute = (chuteFacing == Direction.DOWN);
//
//            return perfectMatch || downwardChute;
//
//        } else if (expectedDirection == Direction.UP) {
//            // 向上连接：溜槽必须朝向DOWN
//            return (chuteFacing == Direction.DOWN);
//
//        } else if (expectedDirection == Direction.DOWN) {
//            // 向下连接：溜槽必须朝向UP
//            return (chuteFacing == Direction.UP);
//        }
//
//        return false;
//    }
//
//    /**
//     * 批量检测所有方向的连接状态
//     * 返回一个布尔数组，顺序为 [NORTH, SOUTH, EAST, WEST, UP, DOWN]
//     */
//    public static boolean[] checkAllDirections(BlockGetter level, BlockPos pos) {
//        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};
//        boolean[] results = new boolean[6];
//
//        for (int i = 0; i < directions.length; i++) {
//            results[i] = hasDirectionalChute(level, pos, directions[i]);
//        }
//
//        return results;
//    }
//}