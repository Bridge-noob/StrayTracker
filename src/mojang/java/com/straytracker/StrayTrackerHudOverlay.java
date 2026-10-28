package com.straytracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;
import java.util.Map;

/** In-game HUD: tracking status + live sorted peak list. */
public final class StrayTrackerHudOverlay {
    private final PeakTracker tracker;

    public StrayTrackerHudOverlay(PeakTracker tracker) {
        this.tracker = tracker;
    }

    public void render(GuiGraphicsExtractor graphics) {
        if (!StrayTrackerConfig.hudEnabled) return;

        Minecraft client = Minecraft.getInstance();
        if (client.options.hideGui || client.player == null) return;

        Font font = client.font;
        int x = StrayTrackerConfig.hudX;
        int y = StrayTrackerConfig.hudY;

        if (tracker.isTracking()) {
            graphics.text(font, "§a● Started — polling /guild online every "
                    + StrayTrackerConfig.intervalSeconds + "s", x, y, 0xFFFFFFFF, true);
            drawPeaks(graphics, font, x, y + 10, true);
        } else if (tracker.hasData()) {
            graphics.text(font, "§c● Stopped", x, y, 0xFFFFFFFF, true);
            drawPeaks(graphics, font, x, y + 10, false);
        }
    }

    /** The peak list right below the status line — live while tracking, final afterwards. */
    private void drawPeaks(GuiGraphicsExtractor graphics, Font font, int x, int y, boolean live) {
        List<Map.Entry<String, int[]>> peaks = tracker.sortedPeaks();
        if (peaks.isEmpty()) {
            if (live) {
                graphics.text(font, "§7Waiting for the first /guild online response...", x, y, 0xFFA0A0A0, true);
            }
            return;
        }
        int rows = Math.min(peaks.size(), StrayTrackerConfig.maxEntries);
        for (int i = 0; i < rows; i++) {
            Map.Entry<String, int[]> e = peaks.get(i);
            graphics.text(font,
                    "§f" + e.getKey() + " §7• §a" + e.getValue()[0] + "/" + e.getValue()[1],
                    x, y + i * 10, 0xFFFFFFFF, true);
        }
    }
}
