package com.adonis.event;

import com.adonis.content.block.IndustrialAnvilBlock;
import com.adonis.content.block.IndustrialComposterBlock;
import com.adonis.content.block.IndustrialFurnaceBlock;
import com.adonis.registry.BlockRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 完整的工业方块交互处理器
 * 功能：
 * 1. 处理溜槽连接到工业方块（使用机械动力的连接逻辑）
 * 2. 处理工业堆肥桶的批量堆肥（Shift+右键）
 * 3. 处理原版方块转换为工业方块（使用工业铁块右键）
 */
@Mod.EventBusSubscriber
public class IndustrialBlockInteractionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("CreateGeography-ChuteConnection");

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        ItemStack heldItem = player.getItemInHand(event.getHand());

        // 处理溜槽连接（最高优先级）
        if (heldItem.getItem() == AllBlocks.CHUTE.asItem()) {
            if (handleChuteConnection(event, state, level, pos, player, heldItem)) {
                return;
            }
        }

        // 处理工业铁块转换原版方块
        if (AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldItem)) {
            if (handleBlockConversion(event, state, level, pos, player, heldItem)) {
                return;
            }
        }

        // 处理工业堆肥桶的批量堆肥
        if (state.getBlock() instanceof IndustrialComposterBlock && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                IndustrialComposterBlock composter = (IndustrialComposterBlock) state.getBlock();
                composter.bulkCompost(state, level, pos, player, event.getHand(), event.getHitVec());
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    /**
     * 处理溜槽连接 - 完全模仿机械动力的溜槽放置逻辑
     */
    private static boolean handleChuteConnection(PlayerInteractEvent.RightClickBlock event, BlockState state,
                                                 Level level, BlockPos pos, Player player, ItemStack heldItem) {
        // 检查目标方块是否为工业熔炉或工业堆肥桶
        if (!(state.getBlock() instanceof IndustrialFurnaceBlock || state.getBlock() instanceof IndustrialComposterBlock)) {
            return false;
        }

        Direction face = event.getFace();

        // 模仿 ChuteBlock.getStateForPlacement 的条件检查
        if (face.getAxis().isHorizontal() && !player.isShiftKeyDown()) {

            if (!level.isClientSide) {
                // 溜槽放置在目标方块的斜上方（与机械动力一致）
                BlockPos chutePos = pos.above().relative(face);

                // 检查位置是否可以放置溜槽
                BlockState existingState = level.getBlockState(chutePos);
                if (!existingState.canBeReplaced() && !existingState.isAir()) {
                    return false;
                }

                // === 关键：完全模仿机械动力的 getStateForPlacement 逻辑 ===
                // 创建一个模拟的 BlockPlaceContext
                BlockPlaceContext simulatedContext = new BlockPlaceContext(
                        level,
                        player,
                        event.getHand(),
                        heldItem,
                        new BlockHitResult(
                                Vec3.atCenterOf(chutePos),
                                face,
                                chutePos,
                                false
                        )
                );

                // 获取 ChuteBlock 实例
                ChuteBlock chuteBlock = (ChuteBlock) AllBlocks.CHUTE.get();

                // 使用机械动力的 getStateForPlacement 方法
                BlockState chuteState = chuteBlock.getStateForPlacement(simulatedContext);

                if (chuteState == null) {
                    return false;
                }

                // 放置溜槽
                level.setBlockAndUpdate(chutePos, chuteState);

                // 验证溜槽是否成功放置
                BlockState placedState = level.getBlockState(chutePos);

                if (!AbstractChuteBlock.isChute(placedState)) {
                    return false;
                }

                // === 更新目标方块的连接状态 ===
                // 延迟更新连接状态，确保溜槽完全放置完成
                level.scheduleTick(pos, state.getBlock(), 2);

                // 强制更新渲染
                level.sendBlockUpdated(pos, state, level.getBlockState(pos), 3);
                level.sendBlockUpdated(chutePos, existingState, placedState, 3);

                // 播放放置声音
                level.playSound(null, chutePos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

                // 消耗物品
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return true;
        }

        return false;
    }

    /**
     * 处理原版方块转换为工业方块
     */
    private static boolean handleBlockConversion(PlayerInteractEvent.RightClickBlock event, BlockState state,
                                                 Level level, BlockPos pos, Player player, ItemStack heldItem) {
        if (level.isClientSide) return false;

        boolean converted = false;

        // 堆肥桶转换
        if (state.getBlock() instanceof ComposterBlock && !(state.getBlock() instanceof IndustrialComposterBlock)) {
            int currentLevel = state.getValue(ComposterBlock.LEVEL);
            level.setBlockAndUpdate(pos, BlockRegistry.INDUSTRIAL_COMPOSTER.get().defaultBlockState()
                    .setValue(ComposterBlock.LEVEL, currentLevel));
            converted = true;
        }
        // 铁砧转换
        else if (state.getBlock() instanceof AnvilBlock && !(state.getBlock() instanceof IndustrialAnvilBlock)) {
            level.setBlockAndUpdate(pos, BlockRegistry.INDUSTRIAL_ANVIL.get().defaultBlockState()
                    .setValue(AnvilBlock.FACING, state.getValue(AnvilBlock.FACING)));
            converted = true;
        }
        // 熔炉转换
        else if (state.getBlock() instanceof FurnaceBlock && !(state.getBlock() instanceof IndustrialFurnaceBlock)) {
            level.setBlockAndUpdate(pos, BlockRegistry.INDUSTRIAL_FURNACE.get().defaultBlockState()
                    .setValue(FurnaceBlock.FACING, state.getValue(FurnaceBlock.FACING))
                    .setValue(FurnaceBlock.LIT, state.getValue(FurnaceBlock.LIT)));
            converted = true;
        }

        if (converted) {
            level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return true;
        }

        return false;
    }
}