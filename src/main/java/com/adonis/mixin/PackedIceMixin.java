//package com.adonis.mixin;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.phys.BlockHitResult;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import static com.adonis.registry.BlockRegistry.CRACKED_PACKED_ICE;
//import static com.adonis.registry.ItemRegistry.GEOLOGICAL_HAMMER;
//import static com.adonis.registry.ItemRegistry.PACKED_ICE_SHARDS;
//
//@Mixin(Block.class)
//public abstract class PackedIceMixin {
//
//    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
//    private void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
//        if (this == Blocks.PACKED_ICE) {
//            if (level.random.nextFloat() < 0.05f) {
//                level.setBlock(pos, CRACKED_PACKED_ICE.get().defaultBlockState(), 3);
//                ci.cancel();
//            }
//        }
//    }
//
//    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
//    private void onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfo ci) {
//        ItemStack heldItem = player.getItemInHand(hand);
//        if (heldItem.getItem() == GEOLOGICAL_HAMMER.get()) {
//            if (this == Blocks.PACKED_ICE) {
//                level.setBlock(pos, CRACKED_PACKED_ICE.get().defaultBlockState(), 3);
//                ci.cancel();
//                ci.setReturnValue(InteractionResult.SUCCESS);
//            } else if (this == CRACKED_PACKED_ICE.get()) {
//                Block.popResource(level, pos, new ItemStack(PACKED_ICE_SHARDS.get(), level.random.nextInt(2, 4)));
//                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
//                ci.cancel();
//                ci.setReturnValue(InteractionResult.SUCCESS);
//            }
//        }
//    }
//
//    @Inject(method = "isRandomlyTicking", at = @At("HEAD"), cancellable = true)
//    private void onIsRandomlyTicking(BlockState state, CallbackInfo ci) {
//        if (this == Blocks.PACKED_ICE) {
//            ci.setReturnValue(true);
//        }
//    }
//}