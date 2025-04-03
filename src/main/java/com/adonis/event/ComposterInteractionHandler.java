package com.adonis.event;

import com.adonis.content.block.IndustrialAnvilBlock;
import com.adonis.content.block.IndustrialComposterBlock;
import com.adonis.content.block.IndustrialFurnaceBlock;
import com.adonis.registry.BlockRegistry;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ComposterInteractionHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        ItemStack heldItem = player.getItemInHand(event.getHand());

        // 检查是否手持工业铁块
        if (AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(heldItem)) {
            if (!level.isClientSide) {
                // 转化原版堆肥桶为工业堆肥桶
                if (state.getBlock() instanceof ComposterBlock && !(state.getBlock() instanceof IndustrialComposterBlock)) {
                    int currentLevel = state.getValue(ComposterBlock.LEVEL);
                    level.setBlockAndUpdate(pos, BlockRegistry.INDUSTRIAL_COMPOSTER.get().defaultBlockState()
                            .setValue(ComposterBlock.LEVEL, currentLevel));
                    level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                }
                // 转化原版铁砧为工业铁砧
                else if (state.getBlock() instanceof AnvilBlock && !(state.getBlock() instanceof IndustrialAnvilBlock)) {
                    level.setBlockAndUpdate(pos, BlockRegistry.INDUSTRIAL_ANVIL.get().defaultBlockState()
                            .setValue(AnvilBlock.FACING, state.getValue(AnvilBlock.FACING)));
                    level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                }
                // 转化原版熔炉为工业熔炉
                else if (state.getBlock() instanceof FurnaceBlock && !(state.getBlock() instanceof IndustrialFurnaceBlock)) {
                    level.setBlockAndUpdate(pos, BlockRegistry.INDUSTRIAL_FURNACE.get().defaultBlockState()
                            .setValue(FurnaceBlock.FACING, state.getValue(FurnaceBlock.FACING))
                            .setValue(FurnaceBlock.LIT, state.getValue(FurnaceBlock.LIT)));
                    level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                } else {
                    return; // 如果不是可转化方块，直接返回
                }
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        // 处理工业堆肥桶的潜行右键批量堆肥
        if (state.getBlock() instanceof IndustrialComposterBlock && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                IndustrialComposterBlock composter = (IndustrialComposterBlock) state.getBlock();
                composter.bulkCompost(state, level, pos, player, event.getHand(), event.getHitVec());
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}