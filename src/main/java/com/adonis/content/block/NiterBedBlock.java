package com.adonis.content.block;

import com.adonis.config.NaturalTransformConfig;
import com.adonis.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class NiterBedBlock extends Block {

    public static final BooleanProperty CRYSTALLIZED = BooleanProperty.create("crystallized");

    public NiterBedBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CRYSTALLIZED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CRYSTALLIZED);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return NaturalTransformConfig.ENABLE_NITER_BED_FORMATION.get();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!NaturalTransformConfig.ENABLE_NITER_BED_FORMATION.get()) {
            return;
        }

        // 如果还未结晶，有概率结晶
        if (!state.getValue(CRYSTALLIZED) && random.nextFloat() < NaturalTransformConfig.NITER_BED_FORMATION_CHANCE.get()) {
            level.setBlock(pos, state.setValue(CRYSTALLIZED, true), 2);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
        // 如果已经结晶，尝试在上方生成硝晶
        else if (state.getValue(CRYSTALLIZED)) {
            handleNiterCrystalGeneration(level, pos, random);
        }
    }

    /**
     * 处理硝晶生成逻辑
     */
    private void handleNiterCrystalGeneration(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos abovePos = pos.above();

        // 检查上方是否为空
        if (!level.isEmptyBlock(abovePos)) {
            return;
        }

        // 有一定概率生成硝晶
        if (random.nextFloat() < NaturalTransformConfig.NITER_BED_FORMATION_CHANCE.get() * 0.5f) { // 降低生成概率
            BlockState niterCrystalState = BlockRegistry.NITER_CRYSTAL.get().defaultBlockState();
            level.setBlock(abovePos, niterCrystalState, 3);
            level.playSound(null, abovePos, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 0.3F, 1.2F);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 玩家可以手动重置结晶状态（用于调试或特殊情况）
        if (state.getValue(CRYSTALLIZED) && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(CRYSTALLIZED, false), 2);
                level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 0.5F, 0.8F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}