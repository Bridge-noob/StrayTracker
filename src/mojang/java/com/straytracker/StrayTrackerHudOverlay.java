package com.straytracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;
import java.util.Map;

/** In-game HUD: tracking status + sorted peak list. */
public final class StrayTrackerHudOverlay {
    private final PeakTracker tracker;

    public StrayTrackerHudOverlay(PeakTracker tracker) {
        this.tracker = tracker;
    }

    public void render(GuiGraphicsExtractor graphics) {
        if (!StrayTrackerConfig.hudEnabled) return;

        Minecraft client = Minecraft.getInstance();
        if (client.options.hidden || client.player == null) return;

        var font = client.font;
        int x = StrayTrackerConfig.hudX;
        int y = StrayTrackerConfig.hudY;

        if (tracker.isTracking()) {
            graphics.text(font, "§a● Tracking — auto-polling every " + StrayTrackerConfig.intervalSeconds + "s", x, y, 0xFFFFFFFF, true);
            graphics.text(font, "§7Press [-] to stop & show peaks", x, y + 10, 0xFFA0A0A0, true);
        } else if (tracker.hasData()) {
            graphics.text(font, "§c● Stopped", x, y, 0xFFFFFFFF, true);
            graphics.text(font, "§e— Peak online —", x, y + 10, 0xFFFFFF55, true);

            List<Map.Entry<String, int[]>> peaks = tracker.sortedPeaks();
            int rows = Math.min(peaks.size(), StrayTrackerConfig.maxEntries);
            for (int i = 0; i < rows; i++) {
                Map.Entry<String, int[]> e = peaks.get(i);
                String line = e.getKey() + " • " + e.getValue()[0] + "/" + e.getValue()[1];
                graphics.text(font, line, x, y + 20 + i * 10, 0xFFFFFFFF, true);
            }
        }
    }
}
