package com.straytracker.StrayTracker;

import java.util.*;

public class PeakTracker {
    private boolean tracking = false;
    private final Map<String, Integer> peakOnline = new HashMap<>();
    private final Map<String, Integer> peakMembers = new HashMap<>();

    public void start() {
        tracking = true;
        peakOnline.clear();
        peakMembers.clear();
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
            String key = e.guildName + " [" + e.guildTag + "]";
            peakOnline.put(key, Math.max(peakOnline.getOrDefault(key, 0), e.online));
            peakMembers.put(key, Math.max(peakMembers.getOrDefault(key, 0), e.total));
        }
    }

    public Map<String, Integer> getPeaks() {
        return new HashMap<>(peakOnline);
    }

    public Map<String, Integer> getTotalPeaks() {
        return new HashMap<>(peakMembers);
    }

    public String getOverview() {
        if (peakOnline.isEmpty()) return "No data recorded.";
        StringBuilder sb = new StringBuilder();
        sb.append("§a=== Peak Guild Online ===\n");
        List<String> keys = new ArrayList<>(peakOnline.keySet());
        keys.sort((a,b) -> Integer.compare(peakOnline.get(b), peakOnline.get(a)));
        for (String k : keys) {
            sb.append(k).append(" • ").append(peakOnline.get(k)).append("/").append(peakMembers.getOrDefault(k, "?")).append("\n");
        }
        return sb.toString();
    }
}
