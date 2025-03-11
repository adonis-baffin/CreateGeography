package com.adonis.event;

import com.adonis.content.item.TrekkingPoles;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "creategeography", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoubleJumpHandler {
    private static final Map<Player, Boolean> CAN_DOUBLE_JUMP = new HashMap<>();

    // 服务端逻辑：重置跳跃状态
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }

        boolean isHoldingPoles = player.getMainHandItem().getItem() instanceof TrekkingPoles ||
                player.getOffhandItem().getItem() instanceof TrekkingPoles;

        if (!isHoldingPoles) {
            CAN_DOUBLE_JUMP.remove(player);
            return;
        }

        // 重置二段跳状态
        if (player.onGround()) {
            CAN_DOUBLE_JUMP.put(player, true);
        }
    }

    // 客户端逻辑：检测跳跃键输入并触发二段跳
    @Mod.EventBusSubscriber(modid = "creategeography", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientHandler {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            boolean isHoldingPoles = mc.player.getMainHandItem().getItem() instanceof TrekkingPoles ||
                    mc.player.getOffhandItem().getItem() instanceof TrekkingPoles;

            if (!isHoldingPoles) return;

            // 当玩家在空中按下跳跃键时，尝试触发二段跳
            if (mc.options.keyJump.isDown() && !mc.player.onGround() &&
                    CAN_DOUBLE_JUMP.getOrDefault(mc.player, false)) {
                mc.player.jumpFromGround();
                // 默认跳跃高度0.42D，增加15%为0.483D
                mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.483D, mc.player.getDeltaMovement().z);
                CAN_DOUBLE_JUMP.put(mc.player, false);

                // 播放音效
                mc.player.level().playSound(null, mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}