package com.straytracker.StrayTracker;

import java.util.*;
import java.util.regex.*;

public class GuildChatParser {
    private static final Pattern LINE_PATTERN = Pattern.compile(
        "^(.*?)\\s*\\[([^\\]]+)\\]\\s*•\\s*(\\d+)/(\\d+)\\s*online$"
    );

    public static List<GuildEntry> parseChat(String text) {
        List<GuildEntry> result = new ArrayList<>();
        if (text == null || text.isEmpty()) return result;
        String[] lines = text.split("\\n");
        boolean inGuildSection = false;
        for (String raw : lines) {
            String line = raw.trim();
            if (line.contains("Guilds Online Here")) {
                inGuildSection = true;
                continue;
            }
            if (inGuildSection) {
                if (line.isEmpty()) continue;
                // Stop if we hit a non-matching line after entries
                Matcher m = LINE_PATTERN.matcher(line);
                if (m.find()) {
                    String name = m.group(1).trim();
                    String tag = m.group(2).trim();
                    int online = Integer.parseInt(m.group(3).trim());
                    int total = Integer.parseInt(m.group(4).trim());
                    result.add(new GuildEntry(name, tag, online, total));
                } else if (line.contains("[") && line.contains("]")) {
                    // Try looser match for lines like "Forsaken [Forsaken] • 10/219 online"
                    // Already handled by regex above
                }
            }
        }
        return result;
    }

    public static boolean isGuildOnlineOutput(String text) {
        return text != null && text.contains("Guilds Online Here");
    }
}
