package xyz.boxcraft.moderation.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import xyz.boxcraft.moderation.networking.PermissionsRequestChannel;
import xyz.boxcraft.moderation.utilities.Permissions;

import java.util.function.Supplier;

public class PermissionsRequestPacket {
    private final int[] requestedPermissions;
    private final int requestId;

    public PermissionsRequestPacket(int requestId, Permissions... requestedPermissions) {
        this.requestId = requestId;
        this.requestedPermissions = new int[requestedPermissions.length];
        for (int i = 0; i < requestedPermissions.length; i++) {
            this.requestedPermissions[i] = requestedPermissions[i].id();
        }
    }

    public PermissionsRequestPacket(FriendlyByteBuf buffer) {
        this.requestId = buffer.readInt();
        this.requestedPermissions = buffer.readVarIntArray();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(requestId);
        buffer.writeVarIntArray(this.requestedPermissions);
    }

    public Permissions[] getRequestedPermissions() {
        Permissions[] permissions = new Permissions[this.requestedPermissions.length];
        for (int i = 0; i < this.requestedPermissions.length; i++) {
            permissions[i] = Permissions.fromId(this.requestedPermissions[i]);
        }

        return permissions;
    }

    public void handleOnServer(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if (player != null) {
                boolean hasPermissions = true; // TODO

                PermissionsRequestChannel.Channel.sendTo(
                        new PermissionsResponsePacket(requestId, hasPermissions),
                        player.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            }
        });

        context.setPacketHandled(true);
    }
}
