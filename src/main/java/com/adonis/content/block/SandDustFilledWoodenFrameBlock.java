package com.adonis.content.block;

import com.adonis.registry.BlockEntityRegistry;
import com.adonis.registry.BlockRegistry;
import com.adonis.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SandDustFilledWoodenFrameBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 0.125, 1);

    public SandDustFilledWoodenFrameBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.SAND_DUST_FILLED_WOODEN_FRAME.get().create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof SandDustFilledWoodenFrameBlockEntity frameEntity) {
                // 玩家空手右键
                if (player.getItemInHand(hand).isEmpty()) {
                    ItemStack sandDust = frameEntity.removeItem(0, 1);
                    if (!sandDust.isEmpty()) {
                        player.getInventory().add(sandDust);
                        // 变回普通木框
                        level.setBlock(pos, BlockRegistry.WOODEN_FRAME.get().defaultBlockState(), 3);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SandDustFilledWoodenFrameBlockEntity frameEntity) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                        frameEntity.getItem(0));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}