package xyz.boxcraft.moderation.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.boxcraft.moderation.Moderation;
import xyz.boxcraft.moderation.networking.PermissionsRequestChannel;
import xyz.boxcraft.moderation.ui.AdminMenu;
import xyz.boxcraft.moderation.utilities.KeyBinding;
import xyz.boxcraft.moderation.utilities.Permissions;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Moderation.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (KeyBinding.MODERATION_MENU.consumeClick()) {
                PermissionsRequestChannel.requestPermissions(Permissions.CAN_OPEN_MENU).thenAccept(hasPerms -> {
                    if (hasPerms) {
                        Minecraft.getInstance().setScreen(new AdminMenu());
                    }
                });
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Moderation.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.MODERATION_MENU);
        }
    }
}
