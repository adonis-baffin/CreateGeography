package com.adonis.event;

import com.adonis.CreateGeography;
import com.adonis.fluid.GeographyFluids;
import com.adonis.registry.ItemRegistry;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = CreateGeography.MODID)
public class BasinFluidInteractionHandler {

    private static final int BOTTLE_VOLUME = 250; // mB，一瓶的容量

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();
        InteractionHand hand = event.getHand();

        // 只处理空瓶
        if (!stack.is(Items.GLASS_BOTTLE)) {
            return;
        }

        // 获取方块实体
        BlockEntity blockEntity = level.getBlockEntity(pos);

        // 检查是否是工作盆
        if (!(blockEntity instanceof BasinBlockEntity basin)) {
            return;
        }

        CreateGeography.LOGGER.debug("检测到空瓶右键工作盆");

        // 获取流体处理器
        var fluidHandlerOpt = basin.getCapability(ForgeCapabilities.FLUID_HANDLER);

        if (fluidHandlerOpt.isPresent()) {
            IFluidHandler fluidHandler = fluidHandlerOpt.orElse(null);

            // 遍历所有储罐查找盐水
            FluidStack targetFluid = FluidStack.EMPTY;
            for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                FluidStack fluidInTank = fluidHandler.getFluidInTank(tank);

                CreateGeography.LOGGER.debug("储罐 {} 中的流体: {} 数量: {}",
                        tank,
                        fluidInTank.isEmpty() ? "空" : ForgeRegistries.FLUIDS.getKey(fluidInTank.getFluid()),
                        fluidInTank.getAmount());

                // 检查是否是盐水（源或流动）
                if (!fluidInTank.isEmpty() &&
                        (fluidInTank.getFluid() == GeographyFluids.BRINE.get() ||
                                fluidInTank.getFluid() == GeographyFluids.BRINE.getSource())) {
                    targetFluid = fluidInTank;
                    break;
                }
            }

            // 如果找到盐水且数量足够
            if (!targetFluid.isEmpty() && targetFluid.getAmount() >= BOTTLE_VOLUME) {
                CreateGeography.LOGGER.debug("找到足够的盐水，尝试装瓶");

                if (!level.isClientSide) {
                    // 创建要抽取的流体栈
                    FluidStack toDrain = new FluidStack(targetFluid.getFluid(), BOTTLE_VOLUME);

                    // 模拟抽取，检查是否可以抽取
                    FluidStack simulated = fluidHandler.drain(toDrain, IFluidHandler.FluidAction.SIMULATE);

                    if (!simulated.isEmpty() && simulated.getAmount() == BOTTLE_VOLUME) {
                        // 实际抽取流体
                        FluidStack drained = fluidHandler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);

                        CreateGeography.LOGGER.debug("成功抽取 {} mB 盐水", drained.getAmount());

                        // 播放声音
                        level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                                SoundSource.BLOCKS, 1.0F, 1.0F);

                        // 给予盐水瓶
                        ItemStack brineBottle = new ItemStack(ItemRegistry.BRINE_BOTTLE.get());

                        // 处理物品栈
                        if (!player.getAbilities().instabuild) {
                            if (stack.getCount() > 1) {
                                stack.shrink(1);
                                if (!player.getInventory().add(brineBottle)) {
                                    player.drop(brineBottle, false);
                                }
                            } else {
                                player.setItemInHand(hand, brineBottle);
                            }
                        }

                        // 标记工作盆需要更新
                        basin.setChanged();
                        basin.notifyChangeOfContents();
                    }
                }

                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
            }
        }
    }
}