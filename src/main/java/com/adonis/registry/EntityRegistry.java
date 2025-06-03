package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.entity.ThrownPebbleEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CreateGeography.MODID);
    
    // 注册投掷的卵石实体
    public static final RegistryObject<EntityType<ThrownPebbleEntity>> THROWN_PEBBLE = ENTITIES.register("thrown_pebble",
            () -> EntityType.Builder.<ThrownPebbleEntity>of(ThrownPebbleEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F) // 设置实体大小
                    .clientTrackingRange(4) // 客户端追踪范围
                    .updateInterval(10) // 更新间隔
                    .build(new ResourceLocation(CreateGeography.MODID, "thrown_pebble").toString()));

}