package com.adonis.content.block;

import com.adonis.registry.BlockEntityRegistry;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class IndustrialFurnaceBlock extends FurnaceBlock implements IWrenchable {

    public IndustrialFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(
                type,
                BlockEntityRegistry.INDUSTRIAL_FURNACE.get(),
                (lvl, pos, blockState, blockEntity) -> IndustrialFurnaceBlockEntity.industrialTick(lvl, pos, blockState, (IndustrialFurnaceBlockEntity) blockEntity)
        );
    }

    // 扳手交互
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player != null && player.isShiftKeyDown()) {
            ItemStack stack = new ItemStack(this.asItem());
            if (!stack.isEmpty()) {
                if (player.getInventory().add(stack)) {
                    level.removeBlock(pos, false);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    popResource(level, pos, stack);
                    level.removeBlock(pos, false);
                }
                player.getInventory().setChanged();
                return InteractionResult.SUCCESS;
            }
        }
        return IWrenchable.super.onWrenched(state, context);
    }
}