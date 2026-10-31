package com.straytracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;
import java.util.Map;

/** In-game HUD: tracking status + live sorted peak list. */
public final class StrayTrackerHudOverlay {
    private final PeakTracker tracker;
    private int renderCounter = 0;

    public StrayTrackerHudOverlay(PeakTracker tracker) {
        this.tracker = tracker;
    }

    public void render(GuiGraphicsExtractor graphics) {
        renderCounter++;
        if (renderCounter % 100 == 0) {
            Minecraft client = Minecraft.getInstance();
            java.util.logging.Logger.getLogger("StrayTracker").info("[StrayTracker] HUD render invoked (count=" + renderCounter + ") hudEnabled=" + StrayTrackerConfig.hudEnabled + " hideGui=" + client.options.hideGui + " playerNull=" + (client.player == null));
        }
        if (!StrayTrackerConfig.hudEnabled) return;

        Minecraft client = Minecraft.getInstance();
        if (client.options.hideGui || client.player == null) return;

        Font font = client.font;
        int x = StrayTrackerConfig.hudX;
        int y = StrayTrackerConfig.hudY;

        // DIAGNOSTIC: must render if the overlay method is being called at all
        // (clamped so it is never drawn off-screen — old value y-12 landed at y=-2 with default hudY=10)
        graphics.text(font, "§4§lSTRAYTRACKER OVERLAY ACTIVE §r", x, Math.max(y - 12, 40), 0xFFFF4444, true);

        if (tracker.isTracking()) {
            graphics.text(font, "§a● Started — polling /guild online every "
                    + StrayTrackerConfig.intervalSeconds + "s", x, y, 0xFFFFFFFF, true);
            int rows = drawPeaks(graphics, font, x, y + 10, true);
            graphics.text(font, "§7Overall peak: §a" + tracker.getOverallPeak(),
                    x, y + 10 + rows * 10 + 2, 0xFFFFFFFF, true);
        } else if (tracker.hasData()) {
            graphics.text(font, "§c● Stopped", x, y, 0xFFFFFFFF, true);
            int rows = drawPeaks(graphics, font, x, y + 10, false);
            graphics.text(font, "§7Overall peak: §a" + tracker.getOverallPeak(),
                    x, y + 10 + rows * 10 + 2, 0xFFFFFFFF, true);
        }
    }

    /** The peak list right below the status line — live while tracking, final afterwards. */
    private int drawPeaks(GuiGraphicsExtractor graphics, Font font, int x, int y, boolean live) {
        List<Map.Entry<String, int[]>> peaks = tracker.sortedPeaks();
        if (peaks.isEmpty()) {
            if (live) {
                graphics.text(font, "§7Waiting for the first /guild online response...", x, y, 0xFFA0A0A0, true);
                return 1;
            }
            return 0;
        }
        int rows = Math.min(peaks.size(), StrayTrackerConfig.maxEntries);
        for (int i = 0; i < rows; i++) {
            Map.Entry<String, int[]> e = peaks.get(i);
            graphics.text(font,
                    "§f" + e.getKey() + " §7• §a" + e.getValue()[0] + "/" + e.getValue()[1],
                    x, y + i * 10, 0xFFFFFFFF, true);
        }
        return rows;
    }
}
