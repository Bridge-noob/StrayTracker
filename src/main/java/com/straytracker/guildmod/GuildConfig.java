package com.straytracker.guildmod;

import java.io.*;
import java.nio.file.*;

public class GuildConfig {
    public static int hotkeyKey = 66; // B
    public static boolean hudEnabled = true;
    public static int hudX = 10;
    public static int hudY = 10;
    public static boolean autoTrack = true; // start tracking automatically
    public static int maxEntries = 40;

    private static final Path PATH = Paths.get("config", "guildmod.json");

    public static void load() {
        try {
            if (!Files.exists(PATH)) return;
            String s = Files.readString(PATH);
            // Very simple JSON parse for these keys
            if (s.contains("\"hotkeyKey\"")) hotkeyKey = extractInt(s, "hotkeyKey", hotkeyKey);
            if (s.contains("\"hudEnabled\"")) hudEnabled = extractBool(s, "hudEnabled", hudEnabled);
            if (s.contains("\"hudX\"")) hudX = extractInt(s, "hudX", hudX);
            if (s.contains("\"hudY\"")) hudY = extractInt(s, "hudY", hudY);
            if (s.contains("\"autoTrack\"")) autoTrack = extractBool(s, "autoTrack", autoTrack);
            if (s.contains("\"maxEntries\"")) maxEntries = extractInt(s, "maxEntries", maxEntries);
        } catch (Exception e) {}
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            String json = "{" +
                "\"hotkeyKey\":" + hotkeyKey + "," +
                "\"hudEnabled\":" + hudEnabled + "," +
                "\"hudX\":" + hudX + "," +
                "\"hudY\":" + hudY + "," +
                "\"autoTrack\":" + autoTrack + "," +
                "\"maxEntries\":" + maxEntries +
            "}";
            Files.writeString(PATH, json);
        } catch (Exception e) {}
    }

    private static int extractInt(String s, String key, int def) {
        try {
            String p = s.split("\"" + key + "\"")[1];
            int i = p.indexOf(':');
            int end = p.indexOf(',', i+1);
            if (end < 0) end = p.indexOf('}', i+1);
            return Integer.parseInt(p.substring(i+1, end).trim());
        } catch (Exception e) { return def; }
    }

    private static boolean extractBool(String s, String key, boolean def) {
        try {
            String p = s.split("\"" + key + "\"")[1];
            int i = p.indexOf(':');
            int end = p.indexOf(',', i+1);
            if (end < 0) end = p.indexOf('}', i+1);
            String val = p.substring(i+1, end).trim();
            return val.equalsIgnoreCase("true");
        } catch (Exception e) { return def; }
    }
}
