package com.adonis.content.block;

import com.adonis.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class SalineDirtBlock extends Block {
    public static final IntegerProperty SALINITY = IntegerProperty.create("salinity", 0, 3);

    public SalineDirtBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SALINITY, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SALINITY);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 检查是否使用锄头
        if (itemStack.getItem() instanceof HoeItem) {
            if (!level.isClientSide) {
                // 转换为盐碱耕地
                BlockState farmlandState = BlockRegistry.SALINE_FARMLAND.get().defaultBlockState()
                        .setValue(SalineFarmlandBlock.MOISTURE, 0)
                        .setValue(SalineFarmlandBlock.SALINITY, state.getValue(SALINITY)); // 保持盐碱化等级

                level.setBlock(pos, farmlandState, 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, farmlandState));

                // 播放音效
                level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);

                // 损耗工具耐久度
                if (!player.getAbilities().instabuild) {
                    itemStack.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(hand));
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        // 支持Forge的ToolAction系统
        if (toolAction == ToolActions.HOE_TILL) {
            return BlockRegistry.SALINE_FARMLAND.get().defaultBlockState()
                    .setValue(SalineFarmlandBlock.MOISTURE, 0)
                    .setValue(SalineFarmlandBlock.SALINITY, state.getValue(SALINITY));
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int salinity = state.getValue(SALINITY);

        // 盐碱化加重逻辑 - 比耕地稍快一些
        if (random.nextFloat() < 0.15f && salinity < 3) { // 15%概率增加盐碱化
            level.setBlock(pos, state.setValue(SALINITY, salinity + 1), 2);
        }

        // 在高盐碱化等级时，可能对周围植物产生影响
        if (salinity >= 2) {
            affectNearbyVegetation(state, level, pos, random);
        }
    }

    // 影响周围植被
    private void affectNearbyVegetation(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int salinity = state.getValue(SALINITY);

        // 在极度盐碱化时，可能杀死周围的草和花
        if (salinity == 3 && random.nextFloat() < 0.05f) { // 5%概率
            for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 1, 1))) {
                if (!nearbyPos.equals(pos)) {
                    BlockState nearbyState = level.getBlockState(nearbyPos);
                    Block nearbyBlock = nearbyState.getBlock();

                    // 杀死草、花等植物
                    if (nearbyBlock instanceof net.minecraft.world.level.block.BushBlock &&
                            !(nearbyBlock instanceof net.minecraft.world.level.block.CropBlock)) {
                        if (random.nextFloat() < 0.3f) { // 30%概率杀死
                            level.destroyBlock(nearbyPos, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}