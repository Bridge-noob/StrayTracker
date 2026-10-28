package com.straytracker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Simple flat-JSON config persisted at config/straytracker.json. */
public final class StrayTrackerConfig {
    static boolean hudEnabled = true;
    static int hudX = 10;
    static int hudY = 10;
    static boolean autoTrack = true;
    static int maxEntries = 40;
    /** Seconds between automatic /guild online requests while tracking. */
    static int intervalSeconds = 30;
    /** GLFW key code of the tracking toggle hotkey (45 = GLFW_KEY_MINUS). */
    static int hotkeyCode = 45;

    private static final Path FILE = Paths.get("config", "straytracker.json");
    private static final int[] INTERVAL_CHOICES = {10, 15, 30, 60, 120, 300};

    private StrayTrackerConfig() {}

    static int nextInterval() {
        for (int c : INTERVAL_CHOICES) if (c > intervalSeconds) return c;
        return INTERVAL_CHOICES[0];
    }

    static void load() {
        try {
            if (!Files.exists(FILE)) return;
            String s = Files.readString(FILE);
            hudEnabled = readBool(s, "hudEnabled", hudEnabled);
            hudX = readInt(s, "hudX", hudX);
            hudY = readInt(s, "hudY", hudY);
            autoTrack = readBool(s, "autoTrack", autoTrack);
            maxEntries = readInt(s, "maxEntries", maxEntries);
            intervalSeconds = readInt(s, "intervalSeconds", intervalSeconds);
            if (intervalSeconds < 5) intervalSeconds = 5;
            hotkeyCode = readInt(s, "hotKeyCode", hotkeyCode);
        } catch (Exception ignored) {
        }
    }

    static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE,
                    "{\n" +
                    "  \"hudEnabled\": " + hudEnabled + ",\n" +
                    "  \"hudX\": " + hudX + ",\n" +
                    "  \"hudY\": " + hudY + ",\n" +
                    "  \"autoTrack\": " + autoTrack + ",\n" +
                    "  \"maxEntries\": " + maxEntries + ",\n" +
                    "  \"intervalSeconds\": " + intervalSeconds + ",\n" +
                    "  \"hotKeyCode\": " + hotkeyCode + "\n" +
                    "}\n");
        } catch (Exception ignored) {
        }
    }

    private static int readInt(String json, String key, int fallback) {
        try {
            String tail = json.split("\"" + key + "\"\\s*:\\s*", 2)[1];
            int end = Math.min(tail.indexOf(','), tail.indexOf('}'));
            if (end < 0) end = tail.length();
            return Integer.parseInt(tail.substring(0, end).trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private static boolean readBool(String json, String key, boolean fallback) {
        try {
            String tail = json.split("\"" + key + "\"\\s*:\\s*", 2)[1];
            return tail.startsWith("true");
        } catch (Exception e) {
            return fallback;
        }
    }
}
