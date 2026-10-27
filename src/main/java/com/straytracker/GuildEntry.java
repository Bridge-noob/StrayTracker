package com.straytracker;

/** One parsed line of the "Guilds Online" output. */
public record GuildEntry(String name, String tag, int online, int total) {
    public String key() {
        return name + " [" + tag + "]";
    }
}
