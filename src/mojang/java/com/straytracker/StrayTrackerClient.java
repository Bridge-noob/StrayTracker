package com.straytracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

@Environment(EnvType.CLIENT)
public class StrayTrackerClient implements ClientModInitializer {
    public static PeakTracker tracker = new PeakTracker();
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("straytracker", "category"));
    private KeyMapping hotkey;
    private StrayTrackerHudOverlay overlay;
    private int autoRequestTimer = 0;

    @Override
    public void onInitializeClient() {
        StrayTrackerConfig.load();
        hotkey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.straytracker.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_MINUS, CATEGORY));
        overlay = new StrayTrackerHudOverlay(tracker);

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath("straytracker", "hud"),
                (graphics, deltaTracker) -> overlay.render(graphics));

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlayVisible) -> {
            String raw = message.getString();
            if (!GuildChatParser.isGuildOnlineOutput(raw)) return true;

            var entries = GuildChatParser.parseChat(raw);
            if (entries.isEmpty()) return true;

            tracker.record(entries);
            var player = Minecraft.getInstance().player;
            if (player != null && tracker.isTracking()) {
                player.sendSystemMessage(Component.literal(
                        "§7[StrayTracker] Recorded " + entries.size() + " guild entr" + (entries.size() == 1 ? "y" : "ies") + "."));
            }
            return true;
        });
    }

    private void onEndTick(Minecraft client) {
        var player = client.player;
        if (player == null) return;

        // Hotkey [-]: toggle tracking
        while (hotkey.consumeClick()) {
            if (tracker.isTracking()) {
                tracker.stop();
                player.sendSystemMessage(Component.literal("§eTracking stopped. §aPeak overview:\n" + tracker.getOverview()));
            } else {
                tracker.start();
                autoRequestTimer = 0;
                player.sendSystemMessage(Component.literal("§aTracking started — auto-polling /guild online every "
                        + StrayTrackerConfig.intervalSeconds + "s."));
            }        }

        // Safety net: if auto-track is on, keep tracking active
        if (StrayTrackerConfig.autoTrack && !tracker.isTracking()) {
            tracker.start();
            autoRequestTimer = 0;
        }

        // Auto-send "/guild online" on a timer while tracking
        if (tracker.isTracking()) {
            if (++autoRequestTimer >= StrayTrackerConfig.intervalSeconds * 20) {
                autoRequestTimer = 0;
                if (player.connection != null) {
                    player.connection.sendCommand("guild online");
                }
            }
        }
    }
}
