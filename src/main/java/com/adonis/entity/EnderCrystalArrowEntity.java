package com.adonis.entity;

import com.adonis.registry.EntityRegistry;
import com.adonis.registry.ItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class EnderCrystalArrowEntity extends AbstractArrow {
    
    private float explosionPower = 6.0f; // 末影水晶爆炸威力约为6
    
    public EnderCrystalArrowEntity(EntityType<? extends EnderCrystalArrowEntity> entityType, Level level) {
        super(entityType, level);
        this.setBaseDamage(4.0); // 设置基础伤害
    }
    
    public EnderCrystalArrowEntity(Level level, double x, double y, double z) {
        super(EntityRegistry.ENDER_CRYSTAL_ARROW.get(), x, y, z, level);
        this.setBaseDamage(4.0);
    }
    
    public EnderCrystalArrowEntity(Level level, LivingEntity shooter) {
        super(EntityRegistry.ENDER_CRYSTAL_ARROW.get(), shooter, level);
        this.setBaseDamage(4.0);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // 添加末影粒子效果
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.PORTAL, 
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.5, 
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.5, 
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.5, 
                        0.0D, 0.0D, 0.0D);
            }
            
            // 添加一些火焰粒子
            if (!this.inGround) {
                this.level().addParticle(ParticleTypes.FLAME, 
                        this.getX(), this.getY(), this.getZ(), 
                        0.0D, 0.0D, 0.0D);
            }
        }
    }
    
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        
        // 如果击中生物，给予火焰效果
        if (result.getEntity() instanceof LivingEntity living) {
            living.setSecondsOnFire(8); // 点燃8秒
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1)); // 凋零效果
        }
        
        if (!this.level().isClientSide) {
            explode();
        }
    }
    
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            explode();
        }
    }
    
    private void explode() {
        // 创建爆炸，设置为可以点燃方块
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 
                explosionPower, true, Level.ExplosionInteraction.TNT);
        this.discard();
    }
    
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ItemRegistry.ENDER_CRYSTAL_ARROW.get());
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}