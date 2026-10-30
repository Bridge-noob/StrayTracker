package com.straytracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StrayTrackerClient implements ClientModInitializer {
    public static PeakTracker tracker = new PeakTracker();
    public static KeyBinding hotkey;
    private StrayTrackerHudOverlay overlay;
    private int autoRequestTimer = 0;
    private boolean autoTrackStarted = false;

    @Override
    public void onInitializeClient() {
        StrayTrackerConfig.load();
        hotkey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.straytracker.toggle", InputUtil.Type.KEYSYM, StrayTrackerConfig.hotkeyCode, "category.straytracker"));
        overlay = new StrayTrackerHudOverlay(tracker);

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);

        HudElementRegistry.addLast(
                Identifier.of("straytracker", "hud"),
                (context, tickCounter) -> overlay.render(context));

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlayVisible) -> {
            String raw = message.getString();

            // Join / leave tracking -> overall peak of distinct guild members online.
            if (GuildChatParser.isGuildJoinOutput(raw)) {
                String player = GuildChatParser.parseJoinPlayer(raw);
                tracker.recordJoin(player);
                var joinPlayer = MinecraftClient.getInstance().player;
                if (joinPlayer != null && tracker.isTracking()) {
                    joinPlayer.sendMessage(Text.literal("§7[StrayTracker] §a" + player + " joined the server. "
                            + "Online guild members: " + tracker.getOnlineGuildCount()
                            + " (peak: " + tracker.getJoinLeavePeak() + ")."), false);
                }
                return true;
            }
            if (GuildChatParser.isGuildLeaveOutput(raw)) {
                String player = GuildChatParser.parseLeavePlayer(raw);
                tracker.recordLeave(player);
                var leavePlayer = MinecraftClient.getInstance().player;
                if (leavePlayer != null && tracker.isTracking()) {
                    leavePlayer.sendMessage(Text.literal("§7[StrayTracker] §c" + player + " left the server. "
                            + "Online guild members: " + tracker.getOnlineGuildCount()
                            + " (peak: " + tracker.getJoinLeavePeak() + ")."), false);
                }
                return true;
            }

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
        if (player == null) {
            // Left the world — let auto-track engage again on the next world join.
            autoTrackStarted = false;
            return;
        }

        // Hotkey [-]: toggle tracking (works freely even with auto-track on)
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

        // Auto-track: start once per world join. (Previously this re-asserted every tick,
        // which instantly undid the hotkey's stop — tracking could never be stopped.)
        if (StrayTrackerConfig.autoTrack && !autoTrackStarted && !tracker.isTracking()) {
            tracker.start();
            autoTrackStarted = true;
            autoRequestTimer = 0;
            player.sendMessage(Text.literal("§7[StrayTracker] §aAuto-tracking started — press §f-§a to stop."), false);
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
