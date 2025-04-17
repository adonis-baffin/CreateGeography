package com.adonis.entity;

import com.adonis.registry.EntityRegistry;
import com.adonis.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class LightningArrowEntity extends AbstractArrow {
    
    private float explosionPower = 6.0f; // 闪电苦力怕爆炸威力
    
    public LightningArrowEntity(EntityType<? extends LightningArrowEntity> entityType, Level level) {
        super(entityType, level);
        this.setBaseDamage(3.5); // 设置基础伤害
    }
    
    public LightningArrowEntity(Level level, double x, double y, double z) {
        super(EntityRegistry.LIGHTNING_ARROW.get(), x, y, z, level);
        this.setBaseDamage(3.5);
    }
    
    public LightningArrowEntity(Level level, LivingEntity shooter) {
        super(EntityRegistry.LIGHTNING_ARROW.get(), shooter, level);
        this.setBaseDamage(3.5);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // 添加闪电粒子效果
        if (this.level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, 
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.5, 
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.5, 
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.5, 
                        0.0D, 0.0D, 0.0D);
            }
        }
    }
    
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        
        if (!this.level().isClientSide) {
            // 生成闪电
            summonLightning();
            // 爆炸
            explode();
        }
    }
    
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            // 生成闪电
            summonLightning();
            // 爆炸
            explode();
        }
    }
    
    private void summonLightning() {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockPos pos = this.blockPosition();
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightningBolt != null) {
                lightningBolt.moveTo(pos.getX(), pos.getY(), pos.getZ());
                lightningBolt.setCause(this.getOwner() instanceof net.minecraft.server.level.ServerPlayer ? (net.minecraft.server.level.ServerPlayer) this.getOwner() : null);
                serverLevel.addFreshEntity(lightningBolt);
            }
        }
    }
    
    private void explode() {
        // 创建爆炸
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 
                explosionPower, Level.ExplosionInteraction.MOB);
        this.discard();
    }
    
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ItemRegistry.LIGHTNING_ARROW.get());
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}