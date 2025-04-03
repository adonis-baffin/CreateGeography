package com.adonis.content.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SickleItem extends Item implements Vanishable {
    private final Tier tier;
    private final float attackDamage;
    private final float attackSpeed;


    // 攻击伤害修饰符UUID
    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    // 触及距离修饰符UUID
    private static final UUID REACH_DISTANCE_MODIFIER_UUID = UUID.fromString("5D4F0E1A-8B3F-4B4A-8D1E-3B4B7A4B1E2F");
    private static final UUID ATTACK_RANGE_MODIFIER_UUID = UUID.fromString("6D4F0E1A-8B3F-4B4A-8D1E-3B4B7A4B1E2F");

    // 直接使用资源位置获取属性
    private static final ResourceLocation REACH_DISTANCE_RL = new ResourceLocation("forge", "reach_distance");
    private static final ResourceLocation ATTACK_RANGE_RL = new ResourceLocation("forge", "attack_range");

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public SickleItem(Tier tier, float attackDamage, Properties properties) {
        super(properties.durability(tier.getUses()));
        this.tier = tier;
        this.attackDamage = attackDamage; // 传入修饰符值，例如 9.0 表示总伤害 10.0
        this.attackSpeed = -3.0F; // 基础 4.0 + (-3.0) = 1.0

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));

        Attribute reachDistance = ForgeRegistries.ATTRIBUTES.getValue(REACH_DISTANCE_RL);
        Attribute attackRange = ForgeRegistries.ATTRIBUTES.getValue(ATTACK_RANGE_RL);

        if (reachDistance != null) {
            builder.put(reachDistance, new AttributeModifier(REACH_DISTANCE_MODIFIER_UUID, "Sickle reach bonus", 2.5D, AttributeModifier.Operation.ADDITION));
        }

        if (attackRange != null) {
            builder.put(attackRange, new AttributeModifier(ATTACK_RANGE_MODIFIER_UUID, "Sickle attack range bonus", 2.5D, AttributeModifier.Operation.ADDITION));
        }

        this.defaultModifiers = builder.build();
    }



    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(attacker.getUsedItemHand()));

        if (target instanceof Animal) {
            int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, stack);

            if (target.getRandom().nextFloat() < 0.5F + (lootingLevel * 0.1F)) {
                target.spawnAtLocation(target.getPickResult());
            }

            // 暴击伤害
            float critDamage = attackDamage * 1.75F; // 暴击伤害为基础伤害的1.75倍
            target.hurt(target.damageSources().generic(), critDamage);
        }

        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        Block block = state.getBlock();

        // 加速挖掘特定方块
        if (block == Blocks.CAKE ||
                block == Blocks.COBWEB ||
                state.is(BlockTags.WOOL) ||
                state.is(BlockTags.LEAVES) ||
                block == Blocks.HAY_BLOCK ||
                block == Blocks.TARGET) {
            return 15.0F;
        }

        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, entity, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        // 允许的魔咒
        return enchantment == Enchantments.SHARPNESS ||
                enchantment == Enchantments.SMITE ||
                enchantment == Enchantments.BANE_OF_ARTHROPODS ||
                enchantment == Enchantments.KNOCKBACK ||
                enchantment == Enchantments.FIRE_ASPECT ||
                enchantment == Enchantments.MOB_LOOTING ||
                enchantment == Enchantments.BLOCK_EFFICIENCY ||
                enchantment == Enchantments.UNBREAKING ||
                enchantment == Enchantments.MENDING ||
                super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    // 新增右键收割功能
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (player == null || level.isClientSide()) {
            return InteractionResult.PASS;
        }

        int harvested = harvestAreaCrops(level, clickedPos, player, itemStack);

        if (harvested > 0) {
            // 播放收割音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROP_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);

            // 消耗工具耐久
            itemStack.hurtAndBreak(Math.max(1, harvested / 3), player,
                    (entity) -> entity.broadcastBreakEvent(context.getHand()));

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * 收割指定区域内的作物
     * @return 收割的方块数量
     */
    private int harvestAreaCrops(Level level, BlockPos centerPos, Player player, ItemStack sickle) {
        int harvested = 0;

        // 基于玩家朝向的收割模式
        Direction playerFacing = player.getDirection();
        Direction right = playerFacing.getClockWise();
        Direction left = playerFacing.getCounterClockWise();

        // 定义5x5的收割模式，玩家在底部（排除某些位置）
        List<BlockPos> harvestPositions = new ArrayList<>();

        // 顶部行（5个方块）
        BlockPos topRowCenter = centerPos.relative(playerFacing, 2);
        harvestPositions.add(topRowCenter);
        harvestPositions.add(topRowCenter.relative(right, 1));
        harvestPositions.add(topRowCenter.relative(right, 2));
        harvestPositions.add(topRowCenter.relative(left, 1));
        harvestPositions.add(topRowCenter.relative(left, 2));

        // 中间行（每行5个方块）
        BlockPos middleRowCenter1 = centerPos.relative(playerFacing, 1);
        harvestPositions.add(middleRowCenter1);
        harvestPositions.add(middleRowCenter1.relative(right, 1));
        harvestPositions.add(middleRowCenter1.relative(right, 2));
        harvestPositions.add(middleRowCenter1.relative(left, 1));
        harvestPositions.add(middleRowCenter1.relative(left, 2));

        // 中心行（包括玩家点击的中心位置）
        harvestPositions.add(centerPos);
        harvestPositions.add(centerPos.relative(right, 1));
        harvestPositions.add(centerPos.relative(right, 2));
        harvestPositions.add(centerPos.relative(left, 1));
        harvestPositions.add(centerPos.relative(left, 2));

        // 对于模式中的每个位置
        for (BlockPos pos : harvestPositions) {
            if (harvestBlock(level, pos, player, sickle)) {
                harvested++;
            }
        }

        return harvested;
    }

    /**
     * 尝试收获单个方块，如果是有效的可收割作物
     * @return 如果收割成功则返回true，否则返回false
     */
    private boolean harvestBlock(Level level, BlockState state, BlockPos pos, Player player, ItemStack sickle) {
        Block block = state.getBlock();

        // 检查方块是否可收割
        if (isHarvestable(block, state)) {
            // 对于带有age属性的作物，检查它们是否完全成熟
            if (block instanceof CropBlock) {
                // 获取作物的age属性
                IntegerProperty ageProperty = getBlockAgeProperty(block);
                if (ageProperty != null) {
                    int age = state.getValue(ageProperty);
                    int maxAge = getMaxAge(block);
                    if (age < maxAge) {
                        return false; // 未完全成熟
                    }
                }
            }

            // 浆果丛的特殊情况
            if (block instanceof SweetBerryBushBlock) {
                int age = state.getValue(SweetBerryBushBlock.AGE);
                if (age < 3) { // 只收获成熟的浆果
                    return false;
                }
            }

            // 获取方块的掉落物
            List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos,
                    level.getBlockEntity(pos), player, sickle);

            // 直接将掉落物添加到玩家的背包中
            for (ItemStack drop : drops) {
                ItemStack remainder = addToPlayerInventory(player, drop);
                if (!remainder.isEmpty()) {
                    // 如果背包已满，将物品掉落在世界中
                    Block.popResource(level, pos, remainder);
                }
            }

            // 如果是作物，则将其重置为种子状态
            if (block instanceof CropBlock) {
                IntegerProperty ageProperty = getBlockAgeProperty(block);
                if (ageProperty != null) {
                    level.setBlock(pos, state.setValue(ageProperty, 0), 2);
                }
            }
            // 完全移除花和其他植物
            else if (block instanceof BushBlock && !(block instanceof LeavesBlock) ||
                    block instanceof FlowerBlock ||
                    block == Blocks.TALL_GRASS ||
                    block == Blocks.GRASS) {
                level.removeBlock(pos, false);
            }
            // 将浆果丛重置为1龄
            else if (block instanceof SweetBerryBushBlock) {
                level.setBlock(pos, state.setValue(SweetBerryBushBlock.AGE, 1), 2);
            }
            // 处理特殊情况（可根据需要扩展）
            else {
                level.destroyBlock(pos, false);
            }

            return true;
        }

        return false;
    }

    // 重载方法，增加BlockState参数的使用
    private boolean harvestBlock(Level level, BlockPos pos, Player player, ItemStack sickle) {
        BlockState state = level.getBlockState(pos);
        return harvestBlock(level, state, pos, player, sickle);
    }

    /**
     * 获取方块的age属性
     */
    /**
     * 获取方块的age属性
     */
    private IntegerProperty getBlockAgeProperty(Block block) {
        if (block instanceof CropBlock) {
            // 由于getAgeProperty()是protected的，我们使用反射获取AGE属性
            // 大多数作物都使用CropBlock.AGE属性
            return CropBlock.AGE;
        } else if (block instanceof SweetBerryBushBlock) {
            return SweetBerryBushBlock.AGE;
        } else if (block == Blocks.NETHER_WART) {
            return NetherWartBlock.AGE;
        }
        return null;
    }

    /**
     * 获取作物的最大年龄
     */
    private int getMaxAge(Block block) {
        if (block instanceof CropBlock) {
            return ((CropBlock) block).getMaxAge();
        } else if (block instanceof SweetBerryBushBlock) {
            return 3;
        } else if (block == Blocks.NETHER_WART) {
            return 3;
        }
        return 0;
    }

    /**
     * 检查方块是否可被镰刀收割
     */
    private boolean isHarvestable(Block block, BlockState state) {
        return block instanceof CropBlock ||
                block instanceof FlowerBlock ||
                block == Blocks.GRASS ||
                block == Blocks.TALL_GRASS ||
                block == Blocks.FERN ||
                block == Blocks.LARGE_FERN ||
                block instanceof SweetBerryBushBlock ||
                block == Blocks.NETHER_WART ||
                (block instanceof BushBlock && !(block instanceof LeavesBlock)) ||
                state.is(BlockTags.FLOWERS);
    }

    /**
     * 将物品添加到玩家的背包中
     * @return 无法添加的剩余物品堆（如果全部添加则为空）
     */
    private ItemStack addToPlayerInventory(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // 首先尝试添加到现有的物品堆中
        ItemStack remaining = stack.copy();
        Inventory inventory = player.getInventory();

        // 首先检查主背包
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            remaining = tryAddStack(inventory, i, remaining);
            if (remaining.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return remaining;
    }

    /**
     * 尝试将物品堆添加到背包的特定槽位中
     */
    private ItemStack tryAddStack(Inventory inventory, int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack existingStack = inventory.getItem(slot);

        if (existingStack.isEmpty()) {
            inventory.setItem(slot, stack);
            return ItemStack.EMPTY;
        } else if (ItemStack.isSameItemSameTags(existingStack, stack)) {
            int spaceAvailable = Math.min(inventory.getMaxStackSize(), existingStack.getMaxStackSize()) - existingStack.getCount();

            if (spaceAvailable > 0) {
                int transferAmount = Math.min(spaceAvailable, stack.getCount());
                existingStack.grow(transferAmount);
                stack.shrink(transferAmount);

                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return stack;
    }
}