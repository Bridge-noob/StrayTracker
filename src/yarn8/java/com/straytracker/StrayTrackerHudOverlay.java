package com.straytracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;
import java.util.Map;

/** In-game HUD: tracking status + live sorted peak list. */
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

        // DIAGNOSTIC: must render if the overlay method is being called at all
        context.drawText(font, "§4§lSTRAYTRACKER OVERLAY ACTIVE §r", x, y - 12, 0xFF4444, true);

        if (tracker.isTracking()) {
            context.drawText(font, "§a● Started — polling /guild online every "
                    + StrayTrackerConfig.intervalSeconds + "s", x, y, 0xFFFFFF, true);
            int rows = drawPeaks(context, font, x, y + 10, true);
            context.drawText(font, "§7Overall peak: §a" + tracker.getOverallPeak(),
                    x, y + 10 + rows * 10 + 2, 0xFFFFFF, true);
        } else if (tracker.hasData()) {
            context.drawText(font, "§c● Stopped", x, y, 0xFFFFFF, true);
            int rows = drawPeaks(context, font, x, y + 10, false);
            context.drawText(font, "§7Overall peak: §a" + tracker.getOverallPeak(),
                    x, y + 10 + rows * 10 + 2, 0xFFFFFF, true);
        }
    }

    /** The peak list right below the status line — live while tracking, final afterwards. */
    private int drawPeaks(DrawContext context, TextRenderer font, int x, int y, boolean live) {
        List<Map.Entry<String, int[]>> peaks = tracker.sortedPeaks();
        if (peaks.isEmpty()) {
            if (live) {
                context.drawText(font, "§7Waiting for the first /guild online response...", x, y, 0xA0A0A0, true);
                return 1;
            }
            return 0;
        }
        int rows = Math.min(peaks.size(), StrayTrackerConfig.maxEntries);
        for (int i = 0; i < rows; i++) {
            Map.Entry<String, int[]> e = peaks.get(i);
            context.drawText(font,
                    "§f" + e.getKey() + " §7• §a" + e.getValue()[0] + "/" + e.getValue()[1],
                    x, y + i * 10, 0xFFFFFF, true);
        }
        return rows;
    }
}