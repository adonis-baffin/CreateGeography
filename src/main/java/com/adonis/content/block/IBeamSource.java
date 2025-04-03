package com.adonis.content.block;

import com.adonis.content.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface IBeamSource {

    // 获取初始光束属性
    BeamHelper.BeamProperties getInitialBeamProperties();

    // 添加光束路径
    void addToBeamBlocks(Vec3i vec, Vec3i vec1, BeamHelper.BeamProperties beamProperties);

    // 获取方块位置
    BlockPos getBlockPos();

    // 获取世界
    Level getLevel();

    // 获取光束属性映射
    Map<com.mojang.datafixers.util.Pair<Vec3i, Vec3i>, BeamHelper.BeamProperties> getBeamPropertiesMap();

    // 判断是否依赖某个位置
    boolean isDependent(BlockPos pos);

    // 添加依赖位置
    void addDependent(BlockPos pos);

    // 获取 tick 计数
    int getTickCount();

    // 是否渲染光束
    boolean shouldRendererLaserBeam();

    // 静态方法：传播光束
    static void propagateLinearBeamVar(IBeamSource iBeamSource, BlockPos initialPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        BeamHelper.propagateLinearBeamVar(iBeamSource, initialPos, beamProperties, lastIndex);
    }
}