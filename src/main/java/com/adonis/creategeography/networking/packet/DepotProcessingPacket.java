package com.adonis.creategeography.networking.packet;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DepotProcessingPacket {

    private final BlockPos pos;
    private final ItemStack particleStack;

    public DepotProcessingPacket(BlockPos pos, ItemStack particleStack) {
        this.pos = pos;
        this.particleStack = particleStack;
    }

    public DepotProcessingPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.particleStack = buf.readItem();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeItem(particleStack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 只在客户端执行
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            // 模拟Create的粒子效果
            Vec3 particlePos = VecHelper.getCenterOf(pos).add(0, 0.5, 0);
            for(int i = 0; i < 15; ++i) {
                Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, level.random, 0.125F).multiply(1.0, 0.0, 1.0);
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, particleStack),
                        particlePos.x, particlePos.y, particlePos.z,
                        motion.x, motion.y + 0.125, motion.z);
            }
        });
        return true;
    }
}