package xyz.boxcraft.moderation.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.boxcraft.moderation.networking.PermissionsRequestChannel;

import java.util.function.Supplier;

public class PermissionsResponsePacket {
    private final int requestId;
    private final boolean hasPermissions;

    public PermissionsResponsePacket(int requestId, boolean hasPermissions) {
        this.requestId = requestId;
        this.hasPermissions = hasPermissions;
    }

    public PermissionsResponsePacket(FriendlyByteBuf buffer) {
        this.requestId = buffer.readInt();
        this.hasPermissions = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(requestId);
        buffer.writeBoolean(hasPermissions);
    }

    public void handleOnClient(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            PermissionsRequestChannel.completeRequest(requestId, hasPermissions);
        });
        context.setPacketHandled(true);
    }
}
