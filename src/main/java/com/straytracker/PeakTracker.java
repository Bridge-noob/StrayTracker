package com.straytracker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Records the peak online/total counts per guild while tracking is active. */
public final class PeakTracker {
    private boolean tracking;
    private final Map<String, int[]> peaks = new LinkedHashMap<>(); // key -> {online, total}

    public void start() {
        tracking = true;
        peaks.clear();
    }

    public void stop() {
        tracking = false;
    }

    public boolean isTracking() {
        return tracking;
    }

    public void record(List<GuildEntry> entries) {
        if (!tracking) return;
        for (GuildEntry e : entries) {
            int[] p = peaks.get(e.key());
            if (p == null) {
                peaks.put(e.key(), new int[]{e.online(), e.total()});
            } else {
                if (e.online() > p[0]) p[0] = e.online();
                if (e.total() > p[1]) p[1] = e.total();
            }
        }
    }

    public boolean hasData() {
        return !peaks.isEmpty();
    }

    /** Entries sorted by peak online count, descending. */
    public List<Map.Entry<String, int[]>> sortedPeaks() {
        List<Map.Entry<String, int[]>> list = new ArrayList<>(peaks.entrySet());
        list.sort((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]));
        return list;
    }

    public String getOverview() {
        if (peaks.isEmpty()) return "§7No data recorded yet.";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, int[]> e : sortedPeaks()) {
            sb.append("§f").append(e.getKey()).append(" §7• §a")
              .append(e.getValue()[0]).append("/").append(e.getValue()[1]).append("§r\n");
        }
        return sb.toString().stripTrailing();
    }
}
