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
    private static final Pattern JOIN =
            Pattern.compile("^\\[Guild\\]\\s+(?:Officer\\s+)?([A-Za-z0-9_]+)\\s+joined\\s+the\\s+server\\.?$");
    private static final Pattern LEAVE =
            Pattern.compile("^\\[Guild]\\s+(?:Officer\\s+)?([A-Za-z0-9_]+)\\s+left\\s+the\\s+server\\.?$");

    private GuildChatParser() {}

    public static boolean isGuildOnlineOutput(String text) {
        return text != null && text.contains(HEADER);
    }

    public static boolean isGuildJoinOutput(String text) {
        return text != null && JOIN.matcher(text).matches();
    }

    public static boolean isGuildLeaveOutput(String text) {
        return text != null && LEAVE.matcher(text).matches();
    }

    public static String parseJoinPlayer(String text) {
        if (text == null) return null;
        var m = JOIN.matcher(text);
        return m.matches() ? m.group(1) : null;
    }

    public static String parseLeavePlayer(String text) {
        if (text == null) return null;
        var m = LEAVE.matcher(text);
        return m.matches() ? m.group(1) : null;
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
