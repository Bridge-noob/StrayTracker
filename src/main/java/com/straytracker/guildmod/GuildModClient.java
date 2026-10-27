package com.straytracker.guildmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

@Environment(EnvType.CLIENT)
public class GuildModClient implements ClientModInitializer {
    public static PeakTracker tracker = new PeakTracker();
    private static KeyBinding hotkey;
    private static GuildHudOverlay overlay;
    private static boolean wasPressed = false;
    private static int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        // Initialize hotkey first
        hotkey = new KeyBinding("key.guildmod.hotkey", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_MINUS, "category.guildmod");
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(hotkey);

        GuildConfig.load();
        overlay = new GuildHudOverlay(tracker);
        tracker.start();

        // Register command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            GuildOnlineCommand.register(dispatcher);
        });

        // Register tick listener for chat parsing and hotkey
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Hotkey handling
            while (hotkey.wasPressed()) {
                if (!wasPressed) {
                    if (tracker.isTracking()) {
                        tracker.stop();
                        client.player.sendMessage(Text.literal("§eGuild tracking stopped.\n§aPeak overview:\n" + tracker.getOverview()), false);
                    } else {
                        tracker.start();
                        client.player.sendMessage(Text.literal("§aGuild tracking started."), false);
                    }
                }
                wasPressed = true;
            }
            if (!hotkey.isPressed()) {
                wasPressed = false;
            }

            // Auto-track if enabled
            if (!tracker.isTracking() && GuildConfig.autoTrack) {
                tracker.start();
            }

            // Auto-send /guild online while tracking
            if (tracker.isTracking()) {
                tickCounter++;
                int targetTicks = Math.max(10, GuildConfig.intervalSeconds) * 20; // 20 ticks/sec
                if (tickCounter >= targetTicks) {
                    tickCounter = 0;
                    if (client.player != null && client.player.networkHandler != null) {
                        client.player.networkHandler.sendChatCommand("guild online");
                    }
                    client.player.sendMessage(Text.literal("§a[GuildMod] Auto-requesting /guild online..."), false);
                }
            } else {
                tickCounter = 0;
            }
        });

        // HUD render event
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            overlay.render(drawContext, tickDelta);
        });

        // Chat listener for parsing /guild online output
        net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            String raw = message.getString();
            if (raw.contains("Guilds Online Here")) {
                java.util.List<GuildEntry> entries = GuildChatParser.parseChat(raw);
                tracker.record(entries);
                if (tracker.isTracking()) {
                    MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("§7[GuildMod] Recorded " + entries.size() + " guild entries (tracking...)"), false);
                }
            }
            return true;
        });
    }
}
