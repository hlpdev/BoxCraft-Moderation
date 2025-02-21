package xyz.boxcraft.moderation.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.boxcraft.moderation.Moderation;
import xyz.boxcraft.moderation.packets.PermissionsRequestPacket;
import xyz.boxcraft.moderation.packets.PermissionsResponsePacket;
import xyz.boxcraft.moderation.utilities.Permissions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PermissionsRequestChannel {
    public static SimpleChannel Channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Moderation.MODID, "permissions-request-channel"),
            () -> Moderation.VERSION,
            Moderation.VERSION::equals,
            Moderation.VERSION::equals
    );

    private static final AtomicInteger requestId = new AtomicInteger(Integer.MIN_VALUE);
    private static final ConcurrentHashMap<Integer, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    public static void registerPackets() {
        int id = 0;

        Channel.messageBuilder(PermissionsRequestPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PermissionsRequestPacket::toBytes)
                .decoder(PermissionsRequestPacket::new)
                .consumerMainThread(PermissionsRequestPacket::handleOnServer)
                .add();

        Channel.messageBuilder(PermissionsResponsePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PermissionsResponsePacket::toBytes)
                .decoder(PermissionsResponsePacket::new)
                .consumerMainThread(PermissionsResponsePacket::handleOnClient)
                .add();
    }

    public static CompletableFuture<Boolean> requestPermissions(Permissions... permissions) {
        int id = requestId.getAndIncrement();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(id, future);

        Channel.sendToServer(new PermissionsRequestPacket(id, permissions));
        return future;
    }

    public static void completeRequest(int requestId, boolean response) {
        CompletableFuture<Boolean> future = pendingRequests.remove(requestId);
        if (future != null) {
            future.complete(response);
        }
    }
}
