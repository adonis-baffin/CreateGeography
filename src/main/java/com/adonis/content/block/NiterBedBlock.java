package com.adonis.content.block;

import com.adonis.config.NaturalTransformConfig;
import com.adonis.registry.ItemRegistry;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

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
        return NaturalTransformConfig.ENABLE_NITER_BED_FORMATION.get() && !state.getValue(CRYSTALLIZED);
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!NaturalTransformConfig.ENABLE_NITER_BED_FORMATION.get()) {
            return;
        }
        
        if (!state.getValue(CRYSTALLIZED) && random.nextFloat() < NaturalTransformConfig.NITER_BED_FORMATION_CHANCE.get()) {
            level.setBlock(pos, state.setValue(CRYSTALLIZED, true), 2);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(CRYSTALLIZED)) {
            if (!level.isClientSide) {
                // 掉落硝石
                int amount = 1 + level.random.nextInt(2); // 1-2个硝石
                Block.popResource(level, pos, new ItemStack(ItemRegistry.NITER_POWDER.get(), amount));
                
                // 重置为未结晶状态
                level.setBlock(pos, state.setValue(CRYSTALLIZED, false), 2);
                level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
    
    // 支持机械动力的动力犁自动化
    public boolean canBeUsedByDeployer(BlockState state) {
        return state.getValue(CRYSTALLIZED);
    }
    
    // 当被动力犁等工具使用时
    public ItemStack getCloneItemStack(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(CRYSTALLIZED)) {
            return new ItemStack(ItemRegistry.NITER_POWDER.get(), 1 + level.random.nextInt(2));
        }
        return ItemStack.EMPTY;
    }
    
    // 处理工具动作（如动力犁）
    public boolean onDeployerApplied(BlockState state, Level level, BlockPos pos, Player player, ItemStack heldItem, BlockHitResult ray) {
        if (state.getValue(CRYSTALLIZED)) {
            if (!level.isClientSide) {
                // 掉落硝石
                int amount = 1 + level.random.nextInt(2);
                Block.popResource(level, pos, new ItemStack(ItemRegistry.NITER_POWDER.get(), amount));
                
                // 重置为未结晶状态
                level.setBlock(pos, state.setValue(CRYSTALLIZED, false), 2);
                level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        return false;
    }
}