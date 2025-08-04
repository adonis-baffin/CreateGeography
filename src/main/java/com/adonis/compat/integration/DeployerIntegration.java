package com.adonis.compat.integration;

import com.adonis.content.block.NiterBedBlock;
import com.adonis.registry.ItemRegistry;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 动力犁与硝化床的集成
 * 这个类提供了动力犁自动收获硝化床的功能
 */
public class DeployerIntegration {
    
    /**
     * 处理动力犁对硝化床的操作
     * @param state 方块状态
     * @param level 世界
     * @param pos 位置
     * @param player 玩家（可能是null，如果是机器操作）
     * @param heldItem 手持物品
     * @param ray 射线结果
     * @return 是否成功处理
     */
    public static boolean handleNiterBedDeployerApplication(BlockState state, Level level, BlockPos pos, 
                                                          Player player, ItemStack heldItem, BlockHitResult ray) {
        if (!(state.getBlock() instanceof NiterBedBlock)) {
            return false;
        }
        
        NiterBedBlock niterBed = (NiterBedBlock) state.getBlock();
        
        // 检查是否已结晶
        if (state.getValue(NiterBedBlock.CRYSTALLIZED)) {
            if (!level.isClientSide) {
                // 掉落硝石
                int amount = 1 + level.random.nextInt(2); // 1-2个硝石
                net.minecraft.world.level.block.Block.popResource(level, pos, new ItemStack(ItemRegistry.NITER_POWDER.get(), amount));
                
                // 重置为未结晶状态
                level.setBlock(pos, state.setValue(NiterBedBlock.CRYSTALLIZED, false), 2);
                level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * 检查动力犁是否可以操作硝化床
     * @param state 方块状态
     * @return 是否可以操作
     */
    public static boolean canDeployerUseOnNiterBed(BlockState state) {
        if (!(state.getBlock() instanceof NiterBedBlock)) {
            return false;
        }
        
        return state.getValue(NiterBedBlock.CRYSTALLIZED);
    }
}