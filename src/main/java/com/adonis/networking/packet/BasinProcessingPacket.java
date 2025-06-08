package com.adonis.networking.packet;

import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BasinProcessingPacket {

    private final BlockPos pos;
    private final List<ItemStack> particleStacks;

    public BasinProcessingPacket(BlockPos pos, List<ItemStack> particleStacks) {
        this.pos = pos;
        this.particleStacks = particleStacks;
    }

    public BasinProcessingPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        int size = buf.readVarInt();
        this.particleStacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.particleStacks.add(buf.readItem());
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeVarInt(particleStacks.size());
        for (ItemStack stack : particleStacks) {
            buf.writeItem(stack);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            // 模拟 Create 的 Basin 压实粒子效果
            Vec3 particlePos = VecHelper.getCenterOf(pos);
            for (ItemStack stack : particleStacks) {
                for (int i = 0; i < 20; ++i) {
                    Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, level.random, 0.175F).multiply(1.0, 0.0, 1.0);
                    level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                            particlePos.x, particlePos.y, particlePos.z,
                            motion.x, motion.y + 0.25, motion.z);
                }
            }
        });
        return true;
    }
}