package com.adonis.creategeography.content.item;

import com.adonis.creategeography.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BrineBottleItem extends Item {
    
    public BrineBottleItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        
        // 检查是否是泥土或草方块
        if (blockState.is(Blocks.DIRT) || blockState.is(Blocks.GRASS_BLOCK)) {
            if (!level.isClientSide) {
                // 将方块转换为盐碱泥巴
                level.setBlock(pos, BlockRegistry.SALINE_MUD.get().defaultBlockState(), 3);
                
                // 播放声音
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                
                // 如果不是创造模式，消耗物品并给予空瓶
                if (player != null && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.setItemInHand(context.getHand(), new ItemStack(Items.GLASS_BOTTLE));
                    } else {
                        if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                            player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
                        }
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        
        return super.useOn(context);
    }
}