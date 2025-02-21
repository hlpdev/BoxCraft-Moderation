package xyz.boxcraft.moderation.utilities;

import javax.annotation.Nullable;

public enum Permissions {
    CAN_OPEN_MENU(0),
    CAN_VIEW_ALL_PLAYERS(1),
    CAN_SPECTATE(2),
    CAN_NOTE(3),
    CAN_WARN(4),
    CAN_KICK(5),
    CAN_BAN(6),
    CAN_VANISH(7);

    private final int id;

    Permissions(int id) {
        this.id = id;
    }

    public static @Nullable Permissions fromId(int id) {
        for (Permissions permission : Permissions.values()) {
            if (permission.id == id) {
                return permission;
            }
        }

        return null;
    }

    public int id() {
        return id;
    }
}
