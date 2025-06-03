//package com.adonis.content.block;
//
//import com.adonis.CreateGeography;
//import com.adonis.fluid.GeographyFluids;
//import com.adonis.registry.ItemRegistry;
//import com.simibubi.create.content.processing.basin.BasinBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.Component;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.BlockItem;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.context.UseOnContext;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.material.Fluids;
//import net.minecraftforge.common.capabilities.ForgeCapabilities;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//
//public class WoodenFrameItem extends BlockItem {
//    public WoodenFrameItem(Block block, Properties properties) {
//        super(block, properties);
//    }
//
//    @Override
//    public InteractionResult useOn(UseOnContext context) {
//        Level level = context.getLevel();
//        BlockPos pos = context.getClickedPos();
//        Player player = context.getPlayer();
//
//        BlockEntity blockEntity = level.getBlockEntity(pos);
//        if (blockEntity instanceof BasinBlockEntity basin) {
//            var fluidHandlerOpt = basin.getCapability(ForgeCapabilities.FLUID_HANDLER);
//
//            if (fluidHandlerOpt.isPresent()) {
//                IFluidHandler fluidHandler = fluidHandlerOpt.orElse(null);
//
//                // 遍历 tanks 查找非空流体
//                FluidStack fluidStack = FluidStack.EMPTY;
//                for (int i = 0; i < fluidHandler.getTanks(); i++) {
//                    fluidStack = fluidHandler.getFluidInTank(i);
//                    if (!fluidStack.isEmpty()) {
//                        break;
//                    }
//                }
//
//
//
//                if (!fluidStack.isEmpty() && fluidStack.getAmount() >= 250) {
//                    ItemStack resultStack;
//                    if (fluidStack.getFluid() == Fluids.WATER) {
//                        resultStack = new ItemStack(ItemRegistry.WATER_FILLED_WOODEN_FRAME.get());
//                    } else if (fluidStack.getFluid() == GeographyFluids.BRINE.getSource()) {
//                        resultStack = new ItemStack(ItemRegistry.BRINE_FILLED_WOODEN_FRAME.get());
//                    } else if (fluidStack.getFluid() == GeographyFluids.MUD.getSource()) {
//                        resultStack = new ItemStack(ItemRegistry.MUD_FILLED_WOODEN_FRAME.get()); // 需要新增
//                    } else if (fluidStack.getFluid() == GeographyFluids.SAND_SLURRY.getSource()) {
//                        resultStack = new ItemStack(ItemRegistry.SAND_SLURRY_FILLED_WOODEN_FRAME.get()); // 需要新增
//                    } else {
//                        player.displayClientMessage(Component.translatable("message.creategeography.wooden_frame.unsupported_fluid"), true);
//                        return InteractionResult.FAIL;
//                    }
//
//                    // 消耗250mB流体
//                    fluidHandler.drain(250, IFluidHandler.FluidAction.EXECUTE);
//                    basin.setChanged();
//
//                    // 替换玩家手中的木框
//                    ItemStack heldItem = context.getItemInHand();
//                    if (heldItem.getCount() > 1) {
//                        heldItem.shrink(1);
//                        if (!player.getInventory().add(resultStack)) {
//                            player.drop(resultStack, false);
//                        }
//                    } else {
//                        heldItem.setCount(0);
//                        player.setItemInHand(context.getHand(), resultStack);
//                    }
//
//                    // 播放声音
//                    level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
//                    return InteractionResult.SUCCESS;
//                } else {
//                    player.displayClientMessage(Component.translatable("message.creategeography.wooden_frame.not_enough_fluid"), true);
//                    return InteractionResult.FAIL;
//                }
//            }
//        }
//
//        // 如果不是工作盆，则正常放置方块
//        return super.useOn(context);
//    }
//}