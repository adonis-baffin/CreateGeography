package com.adonis.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BeltProcessingPacket {

    private final Vec3 pos; // 使用Vec3来精确定位
    private final ItemStack particleStack;

    public BeltProcessingPacket(Vec3 pos, ItemStack particleStack) {
        this.pos = pos;
        this.particleStack = particleStack;
    }

    public BeltProcessingPacket(FriendlyByteBuf buf) {
        this.pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.particleStack = buf.readItem();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeItem(particleStack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            // 模拟Create的Belt压制粒子效果
            for (int i = 0; i < 15; ++i) {
                Vec3 motion = com.simibubi.create.foundation.utility.VecHelper.offsetRandomly(Vec3.ZERO, level.random, 0.125F).multiply(1.0, 0.0, 1.0);
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, particleStack),
                        pos.x, pos.y, pos.z,
                        motion.x, motion.y + 0.125, motion.z);
            }
        });
        return true;
    }
}