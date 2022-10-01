package gameapi.scoreboard;

import cn.nukkit.Player;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glorydark
 * Depend: ScoreBoardPlugin
 */
public class ScoreboardAPI {
    public static ConcurrentHashMap<Player, Scoreboard> scoreboardConcurrentHashMap = new ConcurrentHashMap<>();

    public static void drawScoreBoardEntry(Player player, String objectiveName, String displayName, String string){
        removeScoreboard(player);
        Scoreboard scoreboard = de.theamychan.scoreboard.api.ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay( DisplaySlot.SIDEBAR, objectiveName, displayName );
        scoreboardDisplay.addLine(string,0);
        de.theamychan.scoreboard.api.ScoreboardAPI.setScoreboard(player,scoreboard);
        scoreboard.showFor(player);
        scoreboardConcurrentHashMap.put(player,scoreboard);
    }

    public static void drawScoreBoardEntry(Player player, String objectiveName, String displayName, String string, String string1){
        removeScoreboard(player);
        Scoreboard scoreboard = de.theamychan.scoreboard.api.ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay( DisplaySlot.SIDEBAR, objectiveName, displayName );
        scoreboardDisplay.addLine(string,0);
        scoreboardDisplay.addLine(string1,0);
        de.theamychan.scoreboard.api.ScoreboardAPI.setScoreboard(player,scoreboard);
        scoreboard.showFor(player);
        scoreboardConcurrentHashMap.put(player,scoreboard);
    }

    public static void drawScoreBoardEntry(Player player, Scoreboard scoreboard){
        removeScoreboard(player);
        scoreboard.showFor(player);
        scoreboardConcurrentHashMap.put(player,scoreboard);
    }

    public static void removeScoreboard(Player player){
        if(scoreboardConcurrentHashMap.get(player) != null){
            de.theamychan.scoreboard.api.ScoreboardAPI.removeScorebaord(player,scoreboardConcurrentHashMap.get(player));
        }
    }

    public static String secToTime(int seconds) {
        int hour = seconds / 3600;
        int minute = (seconds - hour * 3600) / 60;
        int second = (seconds - hour * 3600 - minute * 60);

        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            if(hour < 10) {
                sb.append("0").append(hour).append(":");
            }else{
                sb.append(hour).append(":");
            }
        }
        if (minute > 0) {
            if(minute < 10) {
                sb.append("0").append(minute).append(":");
            }else{
                sb.append(minute).append(":");
            }
        }else{
            sb.append("00:");
        }
        if (second > 0) {
            if(second < 10) {
                sb.append("0").append(second);
            }else{
                sb.append(second);
            }
        }
        if (second == 0) {
            sb.append("00");
        }
        return sb.toString();
    }
}
