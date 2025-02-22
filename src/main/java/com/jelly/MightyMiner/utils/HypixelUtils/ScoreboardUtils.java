package com.jelly.MightyMiner.utils.HypixelUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.jelly.MightyMiner.utils.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScoreboardUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();
        if (mc.theWorld == null) return lines;
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) return lines;

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return lines;

        Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = scores.stream()
            .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName()
                .startsWith("#"))
            .collect(Collectors.toList());

        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }

        for (Score score : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            lines.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
        }

        return lines;
    }

    public static List<String> getCleanScoreboard() {
        List<String> scoreboard = new ArrayList<>();
        for (int i = getScoreboardLines().size() - 1; i >= 0; i--) {
            scoreboard.add(cleanSB(getScoreboardLines().get(i)));
        }
        return scoreboard;
    }

    public static int getCoinsInPurse() {
        Pattern coinsPattern = Pattern.compile("(Purse|Piggy):\\s+(\\d+)");
        for (String line : getCleanScoreboard()) {
            if (!(line.contains("Purse") || line.contains("Piggy"))) continue;
            Matcher coinMatcher = coinsPattern.matcher(line.replace(",", ""));
            if (!coinMatcher.find()) return -1;
            return Integer.parseInt(coinMatcher.group(coinMatcher.groupCount()));
        }
        return -1;
    }

    public static int getTotalBits() { // hehe.. tits
        Pattern bitsPattern = Pattern.compile("Bits:\\s+(\\d+)");
        for (String line : getCleanScoreboard()) {
            if (!line.contains("Bits")) continue;
            Matcher coinMatcher = bitsPattern.matcher(line.replace(",", ""));
            if (!coinMatcher.find()) return -1;
            return Integer.parseInt(coinMatcher.group(coinMatcher.groupCount()));
        }
        return -1;
    }

    public static String getScoreboardTitle() {
        if (mc.theWorld == null) return "";
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) return "";

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return "";

        return StringUtils.stripControlCodes(objective.getDisplayName());
    }

    public static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.stripControlCodes(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();

        for (char c : nvString) {
            if ((int) c > 20 && (int) c < 127) {
                cleaned.append(c);
            }
        }

        return cleaned.toString();
    }

    public static String getScoreboardDisplayName(int line) {
        try {
            return mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(line).getDisplayName();
        } catch (Exception e) {
            LogUtils.debugLog("Error in getting scoreboard " + e);
            return "";
        }
    }
}
