package com.adonis.mixin;

import com.adonis.content.block.IndustrialComposterBlock;
import com.adonis.content.block.IndustrialFurnaceBlock;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 扩展AbstractChuteBlock的isChute方法
 * 让机械动力的溜槽系统识别工业方块为可连接的溜槽
 *
 * 注意：只扩展isChute方法，不处理getChuteFacing以避免类型转换错误
 */
@Mixin(value = AbstractChuteBlock.class, remap = false)
public class AbstractChuteBlockMixin {

    /**
     * 扩展isChute方法，让工业方块被识别为溜槽
     * 这样机械动力的溜槽就能连接到工业方块
     */
    @Inject(method = "isChute", at = @At("HEAD"), cancellable = true)
    private static void expandChuteDefinition(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof IndustrialFurnaceBlock ||
                state.getBlock() instanceof IndustrialComposterBlock) {
            cir.setReturnValue(true);
        }
    }

    /**
     * 修复getChuteFacing方法，避免类型转换错误
     * 当传入工业方块时，返回适当的朝向而不是强制转换
     */
    @Inject(method = "getChuteFacing", at = @At("HEAD"), cancellable = true)
    private static void fixChuteFactingForIndustrialBlocks(BlockState state, CallbackInfoReturnable<Direction> cir) {
        if (state.getBlock() instanceof IndustrialFurnaceBlock ||
                state.getBlock() instanceof IndustrialComposterBlock) {
            // 工业方块被当作溜槽时，返回DOWN朝向（表示可以接收物品）
            cir.setReturnValue(Direction.DOWN);
        }
    }
}