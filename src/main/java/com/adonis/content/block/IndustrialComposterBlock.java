package com.adonis.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class IndustrialComposterBlock extends ComposterBlock {

    public IndustrialComposterBlock(Properties properties) {
        super(properties);
        // 确保默认状态与父类一致
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 获取玩家手中的物品
        ItemStack itemInHand = player.getItemInHand(hand);
        int currentLevel = state.getValue(LEVEL);

        // 如果玩家手中是骨粉，并且堆肥桶已满，则正常产出骨粉
        if (currentLevel == 8 && itemInHand.is(Items.BONE_MEAL)) {
            return super.use(state, level, pos, player, hand, hit);
        }

        // 如果玩家空手右键，则尝试一键堆肥
        if (itemInHand.isEmpty() && hand == InteractionHand.MAIN_HAND) {
            return bulkCompost(state, level, pos, player);
        }

        // 其他情况使用原版堆肥桶逻辑
        return super.use(state, level, pos, player, hand, hit);
    }

    // 一键堆肥功能
    private InteractionResult bulkCompost(BlockState state, Level level, BlockPos pos, Player player) {
        int currentLevel = state.getValue(LEVEL);

        // 如果堆肥桶已满，不执行操作
        if (currentLevel == 8) {
            return InteractionResult.PASS;
        }

        // 收集玩家背包中所有可堆肥的非食物物品
        List<Integer> compostableSlots = new ArrayList<>();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            // 跳过空物品栏
            if (stack.isEmpty()) {
                continue;
            }

            // 检查物品是否可堆肥且不是食物
            float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
            if (chance > 0.0F && !stack.getItem().isEdible()) {
                compostableSlots.add(i);
            }
        }

        // 如果没有找到可堆肥物品，不执行操作
        if (compostableSlots.isEmpty()) {
            return InteractionResult.PASS;
        }

        // 开始批量堆肥
        boolean anySuccess = false;
        RandomSource random = level.getRandom();

        for (int slot : compostableSlots) {
            ItemStack stack = player.getInventory().getItem(slot);

            // 获取堆肥成功率
            float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);

            // 尝试堆肥每个物品
            while (!stack.isEmpty() && currentLevel < 8) {
                if (random.nextFloat() < chance) {
                    // 堆肥成功
                    currentLevel++;
                    level.levelEvent(1500, pos, 1);
                    level.setBlock(pos, state.setValue(LEVEL, currentLevel), 3);
                    anySuccess = true;
                }

                // 减少物品数量
                stack.shrink(1);

                // 如果堆肥桶已满，停止处理
                if (currentLevel >= 8) {
                    break;
                }
            }

            // 更新物品栏
            player.getInventory().setItem(slot, stack);

            // 如果堆肥桶已满，停止处理
            if (currentLevel >= 8) {
                break;
            }
        }

        // 如果有任何成功的堆肥，播放声音
        if (anySuccess) {
            level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 保持原版堆肥桶的tick逻辑
        super.tick(state, level, pos, random);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(LEVEL);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        // 当堆肥桶满时发出微弱光源
        return state.getValue(LEVEL) == 8 ? 5 : 0;
    }
}