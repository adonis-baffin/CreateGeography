package com.adonis.creategeography.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that transforms gold blocks into diamond blocks when struck by lightning
 */
@Mixin(LightningBolt.class)
public class LightningGoldToDiamondMixin {

    @Shadow
    private int life;

    /**
     * Inject at the beginning of the tick method to check for gold blocks
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void transformGoldToDiamond(CallbackInfo ci) {
        // Only process on the first tick when lightning strikes
        if (this.life != 2) {
            return;
        }

        LightningBolt lightning = (LightningBolt) (Object) this;
        Level level = lightning.level();

        // Only process on server side
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos strikePos = lightning.blockPosition();

        // Check in a 3x3x3 area around the lightning strike
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = strikePos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);

                    // Check if the block is a gold block
                    if (state.is(Blocks.GOLD_BLOCK)) {
                        // Transform to diamond block
                        level.setBlock(checkPos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);

                        // Optional: Add some effects for visual feedback
                        serverLevel.levelEvent(2001, checkPos, 41); // Gold block break particles

                        // Optional: Play a sound effect
                        level.levelEvent(1501, checkPos, 0); // Anvil use sound
                    }
                }
            }
        }
    }
}