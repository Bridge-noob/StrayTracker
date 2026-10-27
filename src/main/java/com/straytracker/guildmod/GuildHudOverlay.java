package com.straytracker.guildmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;

public class GuildHudOverlay {
    private final PeakTracker tracker;

    public GuildHudOverlay(PeakTracker tracker) {
        this.tracker = tracker;
    }

    public void render(DrawContext context, float tickDelta) {
        if (!GuildConfig.hudEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        int x = GuildConfig.hudX;
        int y = GuildConfig.hudY;
        int colorOn = 0xFFAAFFAA;
        int colorOff = 0xFFFF8888;

        // Tracking indicator
        String status = tracker.isTracking() ? "§a● Tracking" : "§c● Stopped";
        context.drawText(textRenderer, status, x, y, colorOn, false);

        if (tracker.isTracking()) {
            context.drawText(textRenderer, "§7Hotkey: press again to stop & show peaks", x, y + 12, 0xFFFFFFFF, false);
        } else {
            Map<String, Integer> peaks = tracker.getPeaks();
            if (!peaks.isEmpty()) {
                context.drawText(textRenderer, "§e=== Peak Online ===", x, y + 12, 0xFFFFFFAA, false);
                int row = y + 24;
                List<String> sorted = new ArrayList<>(peaks.keySet());
                sorted.sort((a,b) -> Integer.compare(peaks.get(b), peaks.get(a)));
                int shown = 0;
                for (String k : sorted) {
                    if (shown >= GuildConfig.maxEntries) break;
                    String line = k + " • " + peaks.get(k) + "/" + tracker.getTotalPeaks().getOrDefault(k, 0);
                    context.drawText(textRenderer, line, x, row, 0xFFFFFFFF, false);
                    row += 10;
                    shown++;
                }
            }
        }
    }
}
