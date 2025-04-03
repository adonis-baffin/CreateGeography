package com.adonis.content.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class IndustrialAnvilBlock extends FallingBlock implements IWrenchable {

    public static final DirectionProperty FACING = AnvilBlock.FACING;

    // 自定义形状，复制自原版铁砧的形状
    private static final VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    private static final VoxelShape X_LEG1 = Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
    private static final VoxelShape X_LEG2 = Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    private static final VoxelShape X_TOP = Block.box(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
    private static final VoxelShape Z_LEG1 = Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
    private static final VoxelShape Z_LEG2 = Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
    private static final VoxelShape Z_TOP = Block.box(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);

    // 组合形状
    private static final VoxelShape X_AXIS_AABB = Shapes.or(BASE, X_LEG1, X_LEG2, X_TOP);
    private static final VoxelShape Z_AXIS_AABB = Shapes.or(BASE, Z_LEG1, Z_LEG2, Z_TOP);

    private static final Component CONTAINER_TITLE = Component.translatable("container.repair");

    // 附魔转移的配置参数
    private static final int ENCHANTMENT_LIMIT = 10; // 最大转移附魔数量
    private static final boolean RETURN_ORIGINAL_ITEM = true; // 是否返还原物品
    private static final int FIXED_COST = 0; // 固定经验消耗，0表示使用计算值
    private static final double COST_FACTOR = 0.5; // 经验消耗系数

    public IndustrialAnvilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(getMenuProvider(state, level, pos));
        player.awardStat(Stats.INTERACT_WITH_ANVIL);
        return InteractionResult.CONSUME;
    }

    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) -> {
            return new AnvilMenu(id, inventory, ContainerLevelAccess.create(level, pos)) {
                // 重写 mayPickup 方法，确保玩家可以取出物品
                @Override
                public boolean mayPickup(Player player, boolean hasStack) {
                    return true;
                }

                // 重写 getCost 方法，始终返回0，确保不消耗经验
                @Override
                public int getCost() {
                    return 0;
                }

                // 重写 createResult 方法，添加附魔转移功能
                @Override
                public void createResult() {
                    super.createResult();

                    // 检查是否满足附魔转移条件：左侧有附魔物品，右侧是普通书
                    ItemStack leftStack = this.inputSlots.getItem(0);
                    ItemStack rightStack = this.inputSlots.getItem(1);

                    if (leftStack.isEnchanted() && rightStack.getItem() == Items.BOOK && rightStack.getCount() == 1) {
                        // 获取左侧物品的附魔
                        Map<Enchantment, Integer> rawEnchantments = EnchantmentHelper.getEnchantments(leftStack);
                        Map<Enchantment, Integer> enchantments = new HashMap<>();

                        // 限制转移的附魔数量
                        int count = 0;
                        for (Map.Entry<Enchantment, Integer> entry : rawEnchantments.entrySet()) {
                            if (count >= ENCHANTMENT_LIMIT) {
                                break;
                            }
                            enchantments.put(entry.getKey(), entry.getValue());
                            count++;
                        }

                        // 创建附魔书
                        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                            EnchantedBookItem.addEnchantment(enchantedBook,
                                    new EnchantmentInstance(entry.getKey(), entry.getValue()));
                        }

                        // 计算经验消耗
                        int cost = 1;
                        if (FIXED_COST != 0) {
                            cost = FIXED_COST;
                        } else {
                            int baseCost = 0;
                            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                                Enchantment enchantment = entry.getKey();
                                int level = entry.getValue();

                                int enchCost = 0;
                                switch (enchantment.getRarity()) {
                                    case COMMON:
                                        enchCost = 1;
                                        break;
                                    case UNCOMMON:
                                        enchCost = 2;
                                        break;
                                    case RARE:
                                        enchCost = 4;
                                        break;
                                    case VERY_RARE:
                                        enchCost = 8;
                                }

                                baseCost += enchCost * level;
                            }

                            cost = (int) Math.round(baseCost * COST_FACTOR);
                            if (cost < 1) cost = 1;
                        }

                        // 设置输出和经验消耗
                        this.resultSlots.setItem(0, enchantedBook);
                        // 修复私有字段访问问题
                        // this.cost.set(0); // 工业铁砧不消耗经验
                        // 使用setData方法设置cost
                        this.setData(0, 0); // 工业铁砧不消耗经验
                    }
                }
                // 重写 onTake 方法，处理物品取出逻辑
                @Override
                protected void onTake(Player player, ItemStack stack) {
                    ItemStack leftStack = this.inputSlots.getItem(0);
                    ItemStack rightStack = this.inputSlots.getItem(1);

                    // 检查是否是附魔转移操作
                    if (!leftStack.isEmpty() && !rightStack.isEmpty() &&
                            leftStack.isEnchanted() && rightStack.getItem() == Items.BOOK) {

                        // 如果配置为返还原物品
                        if (RETURN_ORIGINAL_ITEM) {
                            // 创建无附魔的原物品副本
                            ItemStack disenchantedItem = leftStack.copy();
                            Map<Enchantment, Integer> emptyMap = new HashMap<>();
                            EnchantmentHelper.setEnchantments(emptyMap, disenchantedItem);

                            // 将无附魔物品添加到玩家物品栏
                            if (!player.getInventory().add(disenchantedItem)) {
                                // 如果物品栏已满，掉落在地上
                                player.drop(disenchantedItem, false);
                            }
                        }

                        // 清空输入槽
                        this.inputSlots.setItem(0, ItemStack.EMPTY);
                        this.inputSlots.setItem(1, ItemStack.EMPTY);
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                    } else {
                        // 非附魔转移操作，使用默认逻辑
                        this.inputSlots.setItem(0, ItemStack.EMPTY);
                        this.inputSlots.setItem(1, ItemStack.EMPTY);
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                    }

                    player.awardStat(Stats.ITEM_CRAFTED.get(stack.getItem()));
                }

                // 重写 isValidBlock 方法，确保我们的方块被识别为有效的铁砧
                @Override
                protected boolean isValidBlock(BlockState state) {
                    return true;
                }
            };
        }, CONTAINER_TITLE);
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