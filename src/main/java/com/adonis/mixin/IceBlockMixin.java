//package com.adonis.mixin;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.IceBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.phys.BlockHitResult;
//import org.spongepowered.asm.mixin.Mixin;
//import com.adonis.registry.ItemRegistry;
//
//
//import static com.adonis.registry.BlockRegistry.CRACKED_ICE;
//import static com.adonis.registry.ItemRegistry.*;
//
//@Mixin(IceBlock.class)
//public abstract class IceBlockMixin extends Block {
//
//    public IceBlockMixin(Properties properties) {
//        super(properties);
//    }
//
//    @Override
//    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
//        if (level.random.nextFloat() < 0.05f) { // 5%概率生成破裂冰
//            level.setBlock(pos, CRACKED_ICE.get().defaultBlockState(), 3);
//        }
//    }
//
//    @Override
//    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
//        ItemStack heldItem = player.getItemInHand(hand);
//        if (heldItem.getItem() == GEOLOGICAL_HAMMER.get()) {
//            if (this == Blocks.ICE) {
//                level.setBlock(pos, CRACKED_ICE.get().defaultBlockState(), 3);
//                return InteractionResult.SUCCESS;
//            } else if (this == CRACKED_ICE.get()) {
//                Block.popResource(level, pos, new ItemStack(ICE_SHARDS.get(), level.random.nextInt(2, 4)));
//                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
//                return InteractionResult.SUCCESS;
//            }
//        }
//        return InteractionResult.PASS;
//    }
//
//    @Override
//    public boolean isRandomlyTicking(BlockState state) {
//        return true;
//    }
//}