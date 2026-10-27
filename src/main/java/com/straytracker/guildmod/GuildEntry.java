package com.straytracker.guildmod;

public class GuildEntry {
    public final String guildName;
    public final String guildTag;
    public final int online;
    public final int total;

    public GuildEntry(String guildName, String guildTag, int online, int total) {
        this.guildName = guildName;
        this.guildTag = guildTag;
        this.online = online;
        this.total = total;
    }

    public int getPeakPossible() {
        return Math.max(online, 0);
    }
}
