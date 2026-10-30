package com.straytracker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Records the peak online/total counts per guild while tracking is active. */
public final class PeakTracker {
    private boolean tracking;
    private final Map<String, int[]> peaks = new LinkedHashMap<>(); // key -> {online, total}
    /** Distinct guild members currently considered online (from join/leave messages). */
    private final Set<String> onlineGuildMembers = new HashSet<>();
    /** Running peak of distinct guild members online (from join/leave messages). */
    private int joinLeavePeak = 0;
    /** Highest /guild online snapshot count seen during tracking. */
    private int guildOnlinePeak = 0;

    public void start() {
        tracking = true;
        peaks.clear();
        onlineGuildMembers.clear();
        joinLeavePeak = 0;
        guildOnlinePeak = 0;
    }

    public void stop() {
        tracking = false;
    }

    public boolean isTracking() {
        return tracking;
    }

    /** Records a guild member joining — updates the online set and the running peak. */
    public void recordJoin(String player) {
        if (player == null || player.isEmpty() || !tracking) return;
        onlineGuildMembers.add(player);
        joinLeavePeak = Math.max(joinLeavePeak, onlineGuildMembers.size());
    }

    /** Records a guild member leaving — removes them from the online set. */
    public void recordLeave(String player) {
        if (player == null || player.isEmpty() || !tracking) return;
        onlineGuildMembers.remove(player);
    }

    public int getJoinLeavePeak() {
        return joinLeavePeak;
    }

    /** Highest /guild online snapshot count seen during tracking. */
    public int getGuildOnlinePeak() {
        return guildOnlinePeak;
    }

    /** Currently-tracked number of distinct guild members online (from join/leave messages). */
    public int getOnlineGuildCount() {
        return onlineGuildMembers.size();
    }

    /** Overall peak = max of the /guild online snapshots and the join/leave derived count. */
    public int getOverallPeak() {
        return Math.max(guildOnlinePeak, joinLeavePeak);
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
            guildOnlinePeak = Math.max(guildOnlinePeak, e.online());
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
        StringBuilder sb = new StringBuilder();
        sb.append("§7Overall peak: §a").append(getOverallPeak()).append("\n");
        if (peaks.isEmpty()) {
            sb.append("§7No data recorded yet.");
        }
        for (Map.Entry<String, int[]> e : sortedPeaks()) {
            sb.append("§f").append(e.getKey()).append(" §7• §a")
              .append(e.getValue()[0]).append("/").append(e.getValue()[1]).append("§r\n");
        }
        return sb.toString().stripTrailing();
    }
}
