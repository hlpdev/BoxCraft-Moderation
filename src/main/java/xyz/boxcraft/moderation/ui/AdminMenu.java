package xyz.boxcraft.moderation.ui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.boxcraft.moderation.Moderation;

import java.util.Collection;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Moderation.MODID, value = Dist.CLIENT)
public class AdminMenu extends Screen {

    private static Collection<PlayerInfo> players;
    private PlayerInfo selectedPlayer;

    public AdminMenu() {
        super(Component.literal("BoxCraft Administrator Menu"));

        selectedPlayer = null;
    }

    private ImGuiImplGlfw imguiGlfw = new ImGuiImplGlfw();
    private ImGuiImplGl3 imguiGl3 = new ImGuiImplGl3();

    private static boolean initializedImGuiIO = false;
    private static boolean menuOpen = false;

    private static ImString usernameSearch = new ImString(16);

    @Override
    public void init() {
        ImGui.createContext();

        ImGui.getIO().setIniFilename("boxcraft-moderation-cache.ini");
        ImGui.getIO().getFonts().addFontDefault();
        ImGui.getIO().getFonts().build();
        imguiGlfw.init(Minecraft.getInstance().getWindow().getWindow(), false);
        imguiGl3.init("#version 110");

        menuOpen = true;
        initializedImGuiIO = true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean charTyped(char character, int keyCode) {
        if (ImGui.getIO().getWantTextInput()) {
            ImGui.getIO().addInputCharacter(character);
        }

        super.charTyped(character, keyCode);
        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (ImGui.getIO().getWantCaptureMouse()) {
            ImGui.getIO().setMouseWheel((float)delta);
        }

        super.mouseScrolled(x, y, delta);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ImGui.getIO().getWantCaptureKeyboard() && keyCode != 256) {
            ImGui.getIO().setKeysDown(keyCode, true);
        }

        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        ImGui.getIO().setKeysDown(keyCode, false);

        super.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public void onClose() {
        menuOpen = false;
        super.onClose();
    }

    @SubscribeEvent
    public static void mouseButtonPressed(ScreenEvent.MouseButtonPressed event) {
        if (!initializedImGuiIO) return;
        ImGui.getIO().setMouseDown(event.getButton(), true);
    }

    @SubscribeEvent
    public static void mouseButtonReleased(ScreenEvent.MouseButtonReleased event) {
        if (!initializedImGuiIO) return;
        ImGui.getIO().setMouseDown(event.getButton(), false);
    }

    @SubscribeEvent
    public static void tick(TickEvent event) {
        if (menuOpen) {
            players = Objects.requireNonNull(Minecraft.getInstance().getConnection()).getOnlinePlayers();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
        imguiGlfw.newFrame();
        imguiGl3.newFrame();
        ImGui.newFrame();

        int centerX = Minecraft.getInstance().getWindow().getScreenWidth() / 2;
        int centerY = Minecraft.getInstance().getWindow().getScreenHeight() / 2;

        ImGui.setNextWindowPos(centerX - 512, centerY - 300);
        ImGui.setNextWindowSize(200, 600);
        ImGui.begin("Players", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

        ImGui.setNextItemWidth(186);
        ImGui.inputText("#searchPlayer", usernameSearch);
        if (ImGui.button("Search Player", new ImVec2(186, 0))) {
            // TODO SEARCH FOR PLAYER
        }

        ImGui.separator();

        ImGui.text("Online Players:");

        for (PlayerInfo player : players) {
            if (ImGui.button(player.getProfile().getName(), new ImVec2(186, 0))) {
                selectedPlayer = player;
            }
        }

        ImGui.end();

        ImGui.setNextWindowPos(centerX - 306, centerY - 300);
        ImGui.setNextWindowSize(800, 600);
        ImGui.begin("Administrator Menu", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

        if (selectedPlayer == null) {
            ImGui.text("Select a player to continue");
        } else {
            ImGui.text(selectedPlayer.getProfile().getId().toString());
        }

        ImGui.end();

        ImGui.render();
        imguiGl3.renderDrawData(ImGui.getDrawData());
    }
}
