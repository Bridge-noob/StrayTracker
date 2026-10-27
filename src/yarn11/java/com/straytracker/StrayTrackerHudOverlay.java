package com.straytracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;
import java.util.Map;

/** In-game HUD: tracking status + sorted peak list. */
public final class StrayTrackerHudOverlay {
    private final PeakTracker tracker;

    public StrayTrackerHudOverlay(PeakTracker tracker) {
        this.tracker = tracker;
    }

    public void render(DrawContext context) {
        if (!StrayTrackerConfig.hudEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.player == null) return;

        TextRenderer font = client.textRenderer;
        int x = StrayTrackerConfig.hudX;
        int y = StrayTrackerConfig.hudY;

        if (tracker.isTracking()) {
            context.drawText(font, "§a● Tracking — auto-polling every " + StrayTrackerConfig.intervalSeconds + "s", x, y, 0xFFFFFF, true);
            context.drawText(font, "§7Press [-] to stop & show peaks", x, y + 10, 0xA0A0A0, true);
        } else if (tracker.hasData()) {
            context.drawText(font, "§c● Stopped", x, y, 0xFFFFFF, true);
            context.drawText(font, "§e— Peak online —", x, y + 10, 0xFFFF55, true);

            List<Map.Entry<String, int[]>> peaks = tracker.sortedPeaks();
            int rows = Math.min(peaks.size(), StrayTrackerConfig.maxEntries);
            for (int i = 0; i < rows; i++) {
                Map.Entry<String, int[]> e = peaks.get(i);
                String line = e.getKey() + " • " + e.getValue()[0] + "/" + e.getValue()[1];
                context.drawText(font, line, x, y + 20 + i * 10, 0xFFFFFF, true);
            }
        }
    }
}
