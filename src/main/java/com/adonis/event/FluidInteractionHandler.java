//package com.adonis.event;
//
//import com.adonis.CreateGeography;
//import com.adonis.fluid.GeographyFluids;
//import com.adonis.registry.ItemRegistry;
//import net.minecraft.core.BlockPos;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.PotionItem;
//import net.minecraft.world.item.alchemy.PotionUtils;
//import net.minecraft.world.item.alchemy.Potions;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.material.FluidState;
//import net.minecraft.world.level.material.Fluids;
//import net.minecraftforge.event.entity.player.PlayerInteractEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fluids.FluidType;
//
//@Mod.EventBusSubscriber(modid = CreateGeography.MODID)
//public class FluidInteractionHandler {
//
//    @SubscribeEvent
//    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
//        Player player = event.getEntity();
//        Level level = event.getLevel();
//        BlockPos pos = event.getPos();
//        ItemStack stack = event.getItemStack();
//        InteractionHand hand = event.getHand();
//
//        // 处理水瓶与草方块的交互
//        if (isWaterBottle(stack)) {
//            BlockState blockState = level.getBlockState(pos);
//            if (blockState.is(Blocks.GRASS_BLOCK)) {
//                if (!level.isClientSide) {
//                    // 将草方块转换为泥巴
//                    level.setBlock(pos, Blocks.MUD.defaultBlockState(), 3);
//
//                    // 播放声音
//                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
//
//                    // 如果不是创造模式，消耗物品并给予空瓶
//                    if (!player.getAbilities().instabuild) {
//                        stack.shrink(1);
//                        if (stack.isEmpty()) {
//                            player.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
//                        } else {
//                            if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
//                                player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
//                            }
//                        }
//                    }
//                }
//                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
//                event.setCanceled(true);
//                return;
//            }
//        }
//
//        // 处理空瓶与流体的交互
//        if (stack.is(Items.GLASS_BOTTLE)) {
//            // 首先检查点击的方块位置
//            FluidState fluidState = level.getFluidState(pos);
//
//            if (checkAndFillBrineBottle(fluidState, level, pos, player, stack, hand)) {
//                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
//                event.setCanceled(true);
//                return;
//            }
//
//            // 如果点击的方块没有流体，检查相邻位置
//            BlockPos adjacentPos = pos.relative(event.getFace());
//            FluidState adjacentFluid = level.getFluidState(adjacentPos);
//
//            if (checkAndFillBrineBottle(adjacentFluid, level, adjacentPos, player, stack, hand)) {
//                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
//                event.setCanceled(true);
//                return;
//            }
//        }
//    }
//
//    private static boolean checkAndFillBrineBottle(FluidState fluidState, Level level, BlockPos pos,
//                                                   Player player, ItemStack stack, InteractionHand hand) {
//        if (fluidState.isEmpty()) {
//            return false;
//        }
//
//        // 调试信息
//        if (!level.isClientSide) {
//            System.out.println("=== 流体调试信息 ===");
//            System.out.println("流体类型: " + fluidState.getType());
//            System.out.println("是否为源: " + fluidState.isSource());
//            System.out.println("BRINE flowing: " + GeographyFluids.BRINE.get());
//            System.out.println("BRINE source: " + GeographyFluids.BRINE.getSource());
//            System.out.println("流体 FluidType: " + fluidState.getFluidType());
//            System.out.println("BRINE FluidType: " + GeographyFluids.BRINE.getType());
//        }
//
//        // 获取流体类型并检查是否是盐水
//        // Create的Registrate系统中，需要同时检查源和流动状态
//        boolean isBrine = false;
//
//        // 使用 FluidType 来比较，这是更可靠的方式
//        if (fluidState.getFluidType() == GeographyFluids.BRINE.getType()) {
//            isBrine = true;
//        }
//
//        // 如果上面的方法不行，尝试直接比较流体
//        if (!isBrine) {
//            // 使用 getType() 方法来避免歧义
//            if (fluidState.getType() == GeographyFluids.BRINE.get() ||
//                    fluidState.getType() == GeographyFluids.BRINE.getSource()) {
//                isBrine = true;
//            }
//        }
//
//        // 只有源方块才能装瓶
//        if (isBrine && fluidState.isSource()) {
//            if (!level.isClientSide) {
//                System.out.println("=== 装瓶成功！ ===");
//
//                // 播放声音
//                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
//
//                // 如果不是创造模式，消耗空瓶并给予盐水瓶
//                if (!player.getAbilities().instabuild) {
//                    stack.shrink(1);
//                    ItemStack brineBottle = new ItemStack(ItemRegistry.BRINE_BOTTLE.get());
//
//                    if (stack.isEmpty()) {
//                        player.setItemInHand(hand, brineBottle);
//                    } else {
//                        if (!player.getInventory().add(brineBottle)) {
//                            player.drop(brineBottle, false);
//                        }
//                    }
//                }
//            }
//            return true;
//        }
//
//        return false;
//    }
//
//    /**
//     * 检查物品是否为水瓶
//     */
//    private static boolean isWaterBottle(ItemStack stack) {
//        if (stack.getItem() instanceof PotionItem) {
//            return PotionUtils.getPotion(stack) == Potions.WATER;
//        }
//        return false;
//    }
//}