//package com.adonis.mixin;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.BlueIceBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.phys.BlockHitResult;
//import org.spongepowered.asm.mixin.Mixin;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.adonis.registry.BlockRegistry.*;
//import static com.adonis.registry.BlockRegistry.COAL_BEARING_BLUE_ICE;
//import static com.adonis.registry.BlockRegistry.COPPER_BEARING_BLUE_ICE;
//import static com.adonis.registry.BlockRegistry.CRACKED_BLUE_ICE;
//import static com.adonis.registry.BlockRegistry.GOLD_BEARING_BLUE_ICE;
//import static com.adonis.registry.BlockRegistry.IRON_BEARING_BLUE_ICE;
//import static com.adonis.registry.BlockRegistry.LAPIS_LAZULI_BEARING_BLUE_ICE;
//import static com.adonis.registry.BlockRegistry.REDSTONE_BEARING_BLUE_ICE;
//import static com.adonis.registry.ItemRegistry.*;
//
//@Mixin(BlueIceBlock.class)public abstract class BlueIceMixin extends Block {
//
//    private static final Map<Block, Block> ORE_TO_BEARING = new HashMap<>();
//    private static final Map<Block, Item> BEARING_TO_POWDER = new HashMap<>();
//
//    static {
//        ORE_TO_BEARING.put(Blocks.IRON_ORE, IRON_BEARING_BLUE_ICE.get());
//        ORE_TO_BEARING.put(Blocks.COPPER_ORE, COPPER_BEARING_BLUE_ICE.get());
//        ORE_TO_BEARING.put(Blocks.GOLD_ORE, GOLD_BEARING_BLUE_ICE.get());
//        ORE_TO_BEARING.put(Blocks.COAL_ORE, COAL_BEARING_BLUE_ICE.get());
//        ORE_TO_BEARING.put(Blocks.LAPIS_ORE, LAPIS_LAZULI_BEARING_BLUE_ICE.get());
//        ORE_TO_BEARING.put(Blocks.REDSTONE_ORE, REDSTONE_BEARING_BLUE_ICE.get());
//
//        BEARING_TO_POWDER.put(IRON_BEARING_BLUE_ICE.get(), IRON_ORE_POWDER.get());
//        BEARING_TO_POWDER.put(COPPER_BEARING_BLUE_ICE.get(), COPPER_ORE_POWDER.get());
//        BEARING_TO_POWDER.put(GOLD_BEARING_BLUE_ICE.get(), GOLD_ORE_POWDER.get());
//        BEARING_TO_POWDER.put(COAL_BEARING_BLUE_ICE.get(), COAL_POWDER.get());
//        BEARING_TO_POWDER.put(LAPIS_LAZULI_BEARING_BLUE_ICE.get(), LAPIS_LAZULI_POWDER.get());
//        BEARING_TO_POWDER.put(REDSTONE_BEARING_BLUE_ICE.get(), Items.REDSTONE); // 使用原版红石粉
//    }
//
//    public BlueIceMixin(Properties properties) {
//        super(properties);
//    }
//
//    @Override
//    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
//        Block belowBlock = level.getBlockState(pos.below()).getBlock();
//        if (level.random.nextFloat() < 0.05f) {
//            if (ORE_TO_BEARING.containsKey(belowBlock) && this == Blocks.BLUE_ICE) {
//                level.setBlock(pos, ORE_TO_BEARING.get(belowBlock).defaultBlockState(), 3);
//            } else if (this == Blocks.BLUE_ICE) {
//                level.setBlock(pos, CRACKED_BLUE_ICE.get().defaultBlockState(), 3);
//            }
//        }
//    }
//
//    @Override
//    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
//        ItemStack heldItem = player.getItemInHand(hand);
//        if (heldItem.getItem() == GEOLOGICAL_HAMMER.get()) {
//            if (this == Blocks.BLUE_ICE) {
//                level.setBlock(pos, CRACKED_BLUE_ICE.get().defaultBlockState(), 3);
//                return InteractionResult.SUCCESS;
//            } else if (this == CRACKED_BLUE_ICE.get()) {
//                Block.popResource(level, pos, new ItemStack(BLUE_ICE_SHARDS.get(), level.random.nextInt(2, 4)));
//                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
//                return InteractionResult.SUCCESS;
//            } else if (BEARING_TO_POWDER.containsKey(this)) {
//                Block.popResource(level, pos, new ItemStack(BEARING_TO_POWDER.get(this), level.random.nextInt(1, 3)));
//                level.setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 3);
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