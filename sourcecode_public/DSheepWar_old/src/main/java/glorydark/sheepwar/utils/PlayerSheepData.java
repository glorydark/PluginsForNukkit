package glorydark.sheepwar.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import gameapi.room.Room;

import java.util.HashMap;

public class PlayerSheepData {

    public HashMap<SheepType, Integer> playerSheepsCount = new HashMap<>();

    public PlayerSheepData(){
        playerSheepsCount.put(SheepType.NormalSheep, 0);
        playerSheepsCount.put(SheepType.Black_Sheep, 0);
        playerSheepsCount.put(SheepType.Blue_Sheep, 0);
        playerSheepsCount.put(SheepType.Gold_Sheep, 0);
        playerSheepsCount.put(SheepType.Green_Sheep, 0);
        playerSheepsCount.put(SheepType.Purple_Sheep, 0);
        playerSheepsCount.put(SheepType.Red_Sheep, 0);
    }

    public Integer getPlayerSheepCount(SheepType type) {
        return playerSheepsCount.get(type);
    }

    public void addPlayerSheepCount(Room room, Player player, SheepType type, Integer count) {
        this.playerSheepsCount.put(type, playerSheepsCount.get(type)+count);
        switch (type){
            case Black_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺黑羊羊！");
                break;
            case Gold_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺金羊羊！");
                break;
            case Blue_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺蓝羊羊！");
                break;
            case Purple_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺紫羊羊！");
                break;
            case Green_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺绿羊羊！");
                break;
            case Red_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺红羊羊！");
                break;
        }
    }

    public void addPlayerSheepCount(Room room, Player player, Player victim, SheepType type, Integer count) {
        this.playerSheepsCount.put(type, playerSheepsCount.get(type)+count);
        switch (type){
            case Black_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已从"+victim.getName()+"手中抢夺黑羊羊！");
                break;
            case Gold_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已从"+victim.getName()+"手中抢夺金羊羊！");
                break;
            case Blue_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已从"+victim.getName()+"手中抢夺蓝羊羊！");
                break;
            case Purple_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已从"+victim.getName()+"手中抢夺紫羊羊！");
                break;
            case Green_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已从"+victim.getName()+"手中抢夺绿羊羊！");
                break;
            case Red_Sheep:
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已从"+victim.getName()+"手中抢夺红羊羊！");
                break;
        }
    }

    public void addPlayerSheepCount(Room room, Player player, EntitySheep entitySheep) {
        SheepType type = SheepType.NormalSheep;
        switch (DyeColor.getByWoolData(entitySheep.getColor())){
            case WHITE:
                type = SheepType.NormalSheep;
                break;
            case BLACK:
                type = SheepType.Black_Sheep;
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺黑羊羊！");
                break;
            case BLUE:
                type = SheepType.Blue_Sheep;
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺蓝羊羊！");
                break;
            case GREEN:
                type = SheepType.Green_Sheep;
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺绿羊羊！");
                break;
            case ORANGE:
                type = SheepType.Gold_Sheep;
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺金羊羊！");
                break;
            case PURPLE:
                type = SheepType.Purple_Sheep;
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺紫羊羊！");
                break;
            case RED:
                type = SheepType.Red_Sheep;
                broadcastMessageToAll(room, TextFormat.YELLOW+"玩家"+player.getName()+"已抢夺红羊羊！");
                break;
        }
        this.playerSheepsCount.put(type, playerSheepsCount.getOrDefault(type, 0)+1);
    }

    /*
    public void resetPlayerSheepCount(SheepType type) {
        this.playerSheepsCount.put(type, 0);
    }

    public void resetPlayerAllSheepCount() {
        playerSheepsCount.put(SheepType.NormalSheep,0);
        playerSheepsCount.put(SheepType.Black_Sheep,0);
        playerSheepsCount.put(SheepType.Blue_Sheep,0);
        playerSheepsCount.put(SheepType.Gold_Sheep,0);
        playerSheepsCount.put(SheepType.Green_Sheep,0);
        playerSheepsCount.put(SheepType.Purple_Sheep,0);
        playerSheepsCount.put(SheepType.Red_Sheep,0);
    }

     */

    public void addPlayersSheeps(Room room, Player player, Player victim, PlayerSheepData data){
        this.addPlayerSheepCount(room, player, victim, SheepType.NormalSheep, data.getPlayerSheepCount(SheepType.NormalSheep));
        this.addPlayerSheepCount(room, player, victim, SheepType.Purple_Sheep, data.getPlayerSheepCount(SheepType.Purple_Sheep));
        this.addPlayerSheepCount(room, player, victim, SheepType.Gold_Sheep, data.getPlayerSheepCount(SheepType.Gold_Sheep));
        this.addPlayerSheepCount(room, player, victim, SheepType.Red_Sheep, data.getPlayerSheepCount(SheepType.Red_Sheep));
        this.addPlayerSheepCount(room, player, victim, SheepType.Green_Sheep, data.getPlayerSheepCount(SheepType.Green_Sheep));
        this.addPlayerSheepCount(room, player, victim, SheepType.Blue_Sheep, data.getPlayerSheepCount(SheepType.Blue_Sheep));
        this.addPlayerSheepCount(room, player, victim, SheepType.Black_Sheep, data.getPlayerSheepCount(SheepType.Black_Sheep));
    }

    public static void broadcastMessageToAll(Room room, String string){
        for(Player player : room.getPlayers()){
            player.sendMessage(string);
        }
    }

    public HashMap<SheepType, Integer> getPlayerSheeps() {
        return playerSheepsCount;
    }
}
