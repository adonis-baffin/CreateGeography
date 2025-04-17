package com.adonis.data;

import com.adonis.CreateGeography;
import com.adonis.registry.ItemRegistry;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateGeography.MODID)
public class TagsProvider {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        // 在这里可以添加其他标签逻辑
    }

    // 在模组初始化时调用此方法
    public static void registerTags() {
        // 注意：在运行时无法直接修改标签
        // 需要通过数据生成或JSON文件来添加标签
        System.out.println("请通过数据生成或JSON文件添加爆炸箭到箭矢标签中");
    }
}