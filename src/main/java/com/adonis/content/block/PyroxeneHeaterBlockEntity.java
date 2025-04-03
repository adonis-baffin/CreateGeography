package com.adonis.content.block;

import com.adonis.content.BeamHelper;
import com.google.common.base.Strings;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PyroxeneHeaterBlockEntity extends SmartBlockEntity implements IBeamReceiver, IHaveGoggleInformation {
    private double heat = 0.0;
    private static final double MAX_HEAT = 100.0; // 最大热量
    private static final double HEATING_RATE = 2.0; // 每tick加热速率
    private static final double COOLING_RATE = 1.0; // 每tick冷却速率
    private boolean isReceivingLight = false;
    private static final int BAR_LENGTH = 10; // 热量条长度

    public PyroxeneHeaterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;

        // 更新热量
        if (isReceivingLight) {
            heat += HEATING_RATE;
        } else {
            heat -= COOLING_RATE;
        }
        heat = Mth.clamp(heat, 0.0, MAX_HEAT);

        // 更新热等级
        BlazeBurnerBlock.HeatLevel newLevel = getHeatLevel();
        if (newLevel != getBlockState().getValue(PyroxeneHeaterBlock.HEAT_LEVEL)) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PyroxeneHeaterBlock.HEAT_LEVEL, newLevel));
            setChanged();
        }

        // 重置光线接收状态
        isReceivingLight = false;
    }

    public void receiveLight(Direction lightDirection) {
        // 检查光线是否来自侧面
        if (lightDirection.getAxis().isHorizontal()) {
            isReceivingLight = true;
        }
    }

    private BlazeBurnerBlock.HeatLevel getHeatLevel() {
        if (heat >= 80.0) return BlazeBurnerBlock.HeatLevel.SEETHING;
        if (heat >= 60.0) return BlazeBurnerBlock.HeatLevel.KINDLED;
        if (heat >= 40.0) return BlazeBurnerBlock.HeatLevel.FADING;
        if (heat >= 20.0) return BlazeBurnerBlock.HeatLevel.SMOULDERING;
        return BlazeBurnerBlock.HeatLevel.NONE;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ChatFormatting formatting = switch (getHeatLevel()) {
            case NONE, SMOULDERING -> ChatFormatting.WHITE;
            case FADING, KINDLED -> ChatFormatting.GOLD;
            case SEETHING -> ChatFormatting.BLUE;
        };

        // 状态标题
        Lang.translate("creategeography.heater.status.title")
                .style(ChatFormatting.GRAY)
                .add(Lang.translate("creategeography.heater.status." + getHeatLevel().name().toLowerCase())
                        .style(formatting))
                .forGoggles(tooltip);

        // 热量条
        Lang.translate("creategeography.heater.heat")
                .style(ChatFormatting.GRAY)
                .add(getHeatComponent(true))
                .forGoggles(tooltip, 1);

        return true;
    }

    private MutableComponent getHeatComponent(boolean forGoggles) {
        int level = (int) (heat * BAR_LENGTH / MAX_HEAT);
        MutableComponent base = Components.empty()
                .append(bars(level, ChatFormatting.DARK_GREEN))
                .append(bars(BAR_LENGTH - level, ChatFormatting.DARK_RED));

        if (!forGoggles) return base;

        return Components.translatable("creategeography.heater.heat_bar")
                .withStyle(ChatFormatting.GRAY)
                .append(Components.literal(": ").withStyle(ChatFormatting.DARK_GRAY))
                .append(base);
    }

    private MutableComponent bars(int level, ChatFormatting format) {
        return Components.literal(Strings.repeat(String.valueOf('|'), level)).withStyle(format);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putDouble("heat", heat);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heat = tag.getDouble("heat");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        write(tag, false);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void receive(IBeamSource source, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        receiveLight(beamProperties.direction);
    }

    @Override
    public boolean useCenteredIncidence() {
        return true;
    }
}