package com.straytracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class StrayTrackerClient implements ClientModInitializer {
    public static PeakTracker tracker = new PeakTracker();
    private KeyBinding hotkey;
    private StrayTrackerHudOverlay overlay;
    private int autoRequestTimer = 0;

    @Override
    public void onInitializeClient() {
        StrayTrackerConfig.load();
        hotkey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.straytracker.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_MINUS, "category.straytracker"));
        overlay = new StrayTrackerHudOverlay(tracker);

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);

        HudRenderCallback.EVENT.register((context, tickDelta) -> overlay.render(context));

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlayVisible) -> {
            String raw = message.getString();
            if (!GuildChatParser.isGuildOnlineOutput(raw)) return true;

            var entries = GuildChatParser.parseChat(raw);
            if (entries.isEmpty()) return true;

            tracker.record(entries);
            var player = MinecraftClient.getInstance().player;
            if (player != null && tracker.isTracking()) {
                player.sendMessage(Text.literal(
                        "§7[StrayTracker] Recorded " + entries.size() + " guild entr" + (entries.size() == 1 ? "y" : "ies") + "."), false);
            }
            return true;
        });
    }

    private void onEndTick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;

        // Hotkey [-]: toggle tracking
        if (hotkey.wasPressed()) {
            if (tracker.isTracking()) {
                tracker.stop();
                player.sendMessage(Text.literal("§eTracking stopped. §aPeak overview:\n" + tracker.getOverview()), false);
            } else {
                tracker.start();
                autoRequestTimer = 0;
                player.sendMessage(Text.literal("§aTracking started — auto-polling /guild online every "
                        + StrayTrackerConfig.intervalSeconds + "s."), false);
            }
        }

        // Safety net: if auto-track is on, keep tracking active
        if (StrayTrackerConfig.autoTrack && !tracker.isTracking()) {
            tracker.start();
            autoRequestTimer = 0;
        }

        // Auto-send "/guild online" on a timer while tracking
        if (tracker.isTracking()) {
            if (++autoRequestTimer >= StrayTrackerConfig.intervalSeconds * 20) {
                autoRequestTimer = 0;
                if (player.networkHandler != null) {
                    player.networkHandler.sendChatCommand("guild online");
                }
            }
        }
    }
}
