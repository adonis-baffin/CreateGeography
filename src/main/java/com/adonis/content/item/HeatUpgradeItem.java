package com.adonis.content.item;

//import net.dragonegg.moreburners.content.block.entity.ElectricBurnerBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.context.UseOnContext;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//public class HeatUpgradeItem extends Item {
//    public HeatUpgradeItem(Properties properties) {
//        super(properties.stacksTo(1));
//    }
//
//    public InteractionResult useOn(UseOnContext context) {
//        Player player = context.getPlayer();
//        if(player!=null && context.getHand()==InteractionHand.MAIN_HAND && !player.isShiftKeyDown()) {
//            Level level = context.getLevel();
//            BlockPos pos = context.getClickedPos();
//            BlockEntity entity = level.getBlockEntity(pos);
//            if(entity instanceof ElectricBurnerBlockEntity ebbe && !ebbe.upgraded) {
//                ebbe.setUpgrade(true);
//                player.setItemInHand(InteractionHand.MAIN_HAND, Items.AIR.getDefaultInstance());
//                level.playSound(null,pos,SoundEvents.SMITHING_TABLE_USE,SoundSource.BLOCKS);
//                return InteractionResult.CONSUME;
//            }
//        }
//        return InteractionResult.PASS;
//    }
//
//}
