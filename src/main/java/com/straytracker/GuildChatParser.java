package com.straytracker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the "Guilds Online Here" chat output, e.g.:
 *   Forsaken [Forsaken] • 10/219 online
 */
public final class GuildChatParser {
    private static final String HEADER = "Guilds Online Here";
    private static final Pattern LINE =
            Pattern.compile("^(.*?)\\s*\\[([^\\]]+)\\]\\s*•\\s*(\\d+)\\s*/\\s*(\\d+)\\s*(?:online)?\\s*$");

    private GuildChatParser() {}

    public static boolean isGuildOnlineOutput(String text) {
        return text != null && text.contains(HEADER);
    }

    public static List<GuildEntry> parseChat(String text) {
        List<GuildEntry> result = new ArrayList<>();
        if (text == null || text.isEmpty()) return result;

        boolean inSection = false;
        for (String raw : text.split("\n")) {
            String line = raw.trim();
            if (line.contains(HEADER)) {
                inSection = true;
                continue;
            }
            if (!inSection || line.isEmpty()) continue;

            Matcher m = LINE.matcher(line);
            if (m.matches()) {
                try {
                    result.add(new GuildEntry(m.group(1).trim(), m.group(2).trim(),
                            Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return result;
    }
}
