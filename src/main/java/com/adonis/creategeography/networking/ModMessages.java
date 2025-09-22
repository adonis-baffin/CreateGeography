package com.adonis.creategeography.networking;

import com.adonis.creategeography.CreateGeography;
import com.adonis.creategeography.networking.packet.BasinProcessingPacket;
import com.adonis.creategeography.networking.packet.BeltProcessingPacket;
import com.adonis.creategeography.networking.packet.DepotProcessingPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(CreateGeography.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(DepotProcessingPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(DepotProcessingPacket::new)
                .encoder(DepotProcessingPacket::toBytes)
                .consumerMainThread(DepotProcessingPacket::handle)
                .add();

        net.messageBuilder(BasinProcessingPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BasinProcessingPacket::new)
                .encoder(BasinProcessingPacket::toBytes)
                .consumerMainThread(BasinProcessingPacket::handle)
                .add();

        net.messageBuilder(BeltProcessingPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BeltProcessingPacket::new)
                .encoder(BeltProcessingPacket::toBytes)
                .consumerMainThread(BeltProcessingPacket::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}