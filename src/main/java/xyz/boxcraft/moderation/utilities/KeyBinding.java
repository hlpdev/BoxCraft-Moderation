package xyz.boxcraft.moderation.utilities;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
    public static final String KEY_CATEGORY = "key.category.moderation";
    public static final String KEY_MODERATION_MENU = "key.moderation.menu";

    public static final KeyMapping MODERATION_MENU = new KeyMapping(KEY_MODERATION_MENU, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, KEY_CATEGORY);


}
