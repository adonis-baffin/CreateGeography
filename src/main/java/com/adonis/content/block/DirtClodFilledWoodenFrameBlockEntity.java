package com.adonis.content.block;

import com.adonis.registry.BlockEntityRegistry;
import com.adonis.registry.BlockRegistry;
import com.adonis.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DirtClodFilledWoodenFrameBlockEntity extends BlockEntity implements Container {
    private ItemStack dirtClod = new ItemStack(ItemRegistry.DIRT_CLOD.get(), 1);

    public DirtClodFilledWoodenFrameBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.DIRT_CLOD_FILLED_WOODEN_FRAME.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return dirtClod.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? dirtClod : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (index == 0 && !dirtClod.isEmpty()) {
            ItemStack removed = dirtClod.split(count);
            if (dirtClod.isEmpty() && level != null) {
                level.setBlock(worldPosition, BlockRegistry.WOODEN_FRAME.get().defaultBlockState(), 3);
            }
            setChanged();
            return removed;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (index == 0) {
            ItemStack item = dirtClod;
            dirtClod = ItemStack.EMPTY;
            return item;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 0) {
            dirtClod = stack;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        dirtClod = ItemStack.EMPTY;
    }
}