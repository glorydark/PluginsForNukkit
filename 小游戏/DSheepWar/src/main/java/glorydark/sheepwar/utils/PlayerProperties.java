package glorydark.sheepwar.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.utils.TextFormat;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import de.theamychan.scoreboard.network.SortOrder;
import gameapi.room.Room;
import gameapi.scoreboard.ScoreboardAPI;

import java.util.HashMap;

public class PlayerProperties {
    public static void addPlayerSheep(Player player, SheepType type, Integer count){
        Room room = Room.getRoom("SheepWar", player);
        if(room != null) {
            PlayerSheepData data = (PlayerSheepData) room.getPlayerProperties(player, "SheepWar");
            data.addPlayerSheepCount(room, player, type, count);
            room.setPlayerProperties(player, "SheepWar", data);
        }
    }

    public static void addPlayerSheep(Player player, EntitySheep entitySheep){
        Room room = Room.getRoom("SheepWar", player);
        if(room != null) {
            PlayerSheepData data = (PlayerSheepData) room.getPlayerProperties(player, "SheepWar");
            data.addPlayerSheepCount(room, player,entitySheep);
            room.setPlayerProperties(player, "SheepWar", data);
        }
    }

    public static void addPlayerSheep(Player player, Player victim){
        Room room = Room.getRoom("SheepWar", player);
        if(room != null) {
            PlayerSheepData data = (PlayerSheepData) room.getPlayerProperties(player, "SheepWar");
            PlayerSheepData addData = (PlayerSheepData) room.getPlayerProperties(victim, "SheepWar");
            data.addPlayersSheeps(room, player, victim, addData);
            room.setPlayerProperties(player, "SheepWar", data);
        }
    }

    public static void resetPlayerSheep(Player player){
        Room room = Room.getRoom("SheepWar", player);
        if(room != null) {
            room.setPlayerProperties(player, "SheepWar", new PlayerSheepData());
        }
    }

    public static Integer getPlayerSheep(Player player, SheepType type){
        Room room = Room.getRoom("SheepWar", player);
        if(room != null) {
            PlayerSheepData sheepData = (PlayerSheepData) room.getPlayerProperties(player, "SheepWar"); // error: string can not cast to PlayerSheepData
            if(sheepData != null){
                return sheepData.getPlayerSheepCount(type);
            }
        }
        return 0;
    }

    public static void showPlayerScoreBoard(Player player){
        Room room = Room.getRoom("SheepWar", player);
        if(room != null) {
            PlayerSheepData sheepData = (PlayerSheepData) room.getPlayerProperties(player, "SheepWar");
            HashMap<SheepType, Integer> cache = sheepData.getPlayerSheeps();
            Scoreboard scoreboard = de.theamychan.scoreboard.api.ScoreboardAPI.createScoreboard();
            ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay( DisplaySlot.SIDEBAR, "scoreboard.sheepwar_scoreboard_entry_final", "???????????????" );
            scoreboardDisplay.setSortOrder(SortOrder.ASCENDING); // ascend
            if(sheepData != null){
                Integer normal = cache.get(SheepType.NormalSheep);
                Integer black = cache.get(SheepType.Black_Sheep);
                Integer green = cache.get(SheepType.Green_Sheep);
                Integer purple = cache.get(SheepType.Purple_Sheep);
                Integer blue = cache.get(SheepType.Blue_Sheep);
                Integer red = cache.get(SheepType.Red_Sheep);
                Integer gold = cache.get(SheepType.Gold_Sheep);
                Integer score = normal*50+black*200+green*500+purple*1000+blue*1500+red*2000+gold*12000;
                scoreboardDisplay.addLine("??e??l???????????????",0);
                scoreboardDisplay.addLine("??a??l????????? "+ score,1);
                scoreboardDisplay.addLine("??? ???????????? " + normal,2);
                scoreboardDisplay.addLine("??? ???????????? " + (black > 0? TextFormat.GREEN+" ???": TextFormat.RED+" ??"),3);
                scoreboardDisplay.addLine("??? ???????????? " + (green > 0? TextFormat.GREEN+" ???": TextFormat.RED+" ??"),4);
                scoreboardDisplay.addLine("??? ???????????? " + (purple > 0? TextFormat.GREEN+" ???": TextFormat.RED+" ??"),5);
                scoreboardDisplay.addLine("??? ???????????? " + (blue > 0? TextFormat.GREEN+" ???": TextFormat.RED+" ??"),6);
                scoreboardDisplay.addLine("??? ???????????? " + (red > 0? TextFormat.GREEN+" ???": TextFormat.RED+" ??"),7);
                scoreboardDisplay.addLine("??? ???????????? " + (gold > 0? TextFormat.GREEN+" ???": TextFormat.RED+" ??"),8);
            }
            scoreboardDisplay.addLine("??????????????? "+ScoreboardAPI.secToTime(room.getGameTime() - room.getTime()), 9);
            ScoreboardAPI.drawScoreBoardEntry(player, scoreboard);
        }
    }

    public static Integer getScore(Player player){
        Room room = Room.getRoom("SheepWar", player);
        if(room != null) {
            PlayerSheepData sheepData = (PlayerSheepData) room.getPlayerProperties(player, "SheepWar");
            HashMap<SheepType, Integer> cache = sheepData.getPlayerSheeps();
            Scoreboard scoreboard = de.theamychan.scoreboard.api.ScoreboardAPI.createScoreboard();
            ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "scoreboard.sheepwar_scoreboard_entry_final", "???????????????");
            scoreboardDisplay.setSortOrder(SortOrder.ASCENDING); // ascend
            if (sheepData != null) {
                Integer normal = cache.get(SheepType.NormalSheep);
                Integer black = cache.get(SheepType.Black_Sheep);
                Integer green = cache.get(SheepType.Green_Sheep);
                Integer purple = cache.get(SheepType.Purple_Sheep);
                Integer blue = cache.get(SheepType.Blue_Sheep);
                Integer red = cache.get(SheepType.Red_Sheep);
                Integer gold = cache.get(SheepType.Gold_Sheep);
                Integer score = normal * 50 + black * 200 + green * 500 + purple * 1000 + blue * 1500 + red * 2000 + gold * 12000;
                return score;
            }
        }
        return 0;
    }

    public static PlayerSheepData getPlayerSheepData(Player player){
        Room room = Room.getRoom("SheepWar", player);
        if(room != null) {
            return (PlayerSheepData) room.getPlayerProperties(player, "SheepWar");
        }else{
            return null;
        }
    }
}
