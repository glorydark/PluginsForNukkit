package gameapi.room;

import cn.nukkit.Player;
import gameapi.utils.AdvancedLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
public class Team {
    private String registryName;

    private int score = 0;
    private String prefix;
    private List<Player> playerList = new ArrayList<>();

    private Room room;

    private int spawnIndex;

    private int maxPlayer;

    private boolean isAlive = true;

    private HashMap<String, Object> properties = new HashMap<>();

    public Team(Room room, String registryName, String prefix, int maxPlayer, int spawnIndex){
        this.room = room;
        this.registryName = registryName;
        this.prefix = prefix;
        this.spawnIndex = spawnIndex;
        this.maxPlayer = maxPlayer;
    }

    public void addPlayer(Player player){
        if(isAvailable()) {
            playerList.add(player);
        }else{
            player.sendMessage("该队伍已满！");
        }
    }

    public int getSize(){
        return playerList.size();
    }

    public boolean isAvailable(){
        return playerList.size() < maxPlayer;
    }

    public void removePlayer(Player player){
        playerList.remove(player);
    }

    public boolean hasPlayer(Player player){
        return playerList.contains(player);
    }

    public void resetAll(){
        this.playerList.clear();
        this.isAlive = true;
    }

    public boolean teleportToSpawn(){
        if(room.getStartSpawn().size() == 0){ return false; }
        if(room.getStartSpawn().size() < spawnIndex + 1){ return false; }
        AdvancedLocation location = room.getStartSpawn().get(spawnIndex);
        for(Player player: playerList){
            location.teleport(player);
        }
        return true;
    }

    @Override
    public String toString() {
        return "Team{" +
                "registryName='" + registryName + '\'' +
                ", score=" + score +
                ", prefix='" + prefix + '\'' +
                ", playerList=" + playerList +
                ", room=" + room +
                ", spawnIndex=" + spawnIndex +
                ", maxPlayer=" + maxPlayer +
                ", isAlive=" + isAlive +
                ", properties=" + properties +
                '}';
    }
}
