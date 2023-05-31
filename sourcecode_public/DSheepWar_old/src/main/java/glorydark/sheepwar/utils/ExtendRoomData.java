package glorydark.sheepwar.utils;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExtendRoomData {
    private final HashMap<SheepType, List<Location>> sheepSpawnPos;
    private final List<Location> randomSpawn;
    private final List<Location> chestPos;

    private Level startLevel;

    public ExtendRoomData(HashMap<SheepType, List<Location>> sheepSpawnPos, List<Location> randomSpawn, List<Location> chestPos, Level startLevel){
        this.sheepSpawnPos = sheepSpawnPos;
        this.randomSpawn = randomSpawn;
        this.chestPos = chestPos;
        this.startLevel = startLevel;
    }

    public Level getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(Level level) {
        this.startLevel = level;
    }

    public List<Location> getChestPos() {
        return chestPos;
    }

    public void removeChestPos(Location location) {
        chestPos.remove(location);
    }

    public void addChestPos(Location location) {
        chestPos.add(location);
    }

    public List<Location> getSheepSpawns(SheepType type) {
        return sheepSpawnPos.get(type);
    }

    public List<Location> getRandomSpawn() {
        return randomSpawn;
    }

    public void addSheepSpawn(SheepType sheepType, Location location){
        List<Location> loc = sheepSpawnPos.get(sheepType);
        if(loc == null){
            loc = new ArrayList<>();
        }
        loc.add(location);
        sheepSpawnPos.put(sheepType, loc);
    }

    public void removeSheepSpawn(SheepType sheepType, Location location) {
        List<Location> loc = sheepSpawnPos.get(sheepType);
        if(loc == null){
            loc = new ArrayList<>();
        }
        if(loc.remove(location)) {
            sheepSpawnPos.put(sheepType, loc);
        }
    }

    public void clearSheepSpawn(SheepType sheepType) {
        sheepSpawnPos.put(sheepType, new ArrayList<>());
    }

    public void addRandomSpawn(Location randomSpawn) {
        this.randomSpawn.add(randomSpawn);
    }

    public void clearRandomSpawn() {
        this.randomSpawn.clear();
    }

    public void removeRandomSpawn(Location randomSpawn) {
        this.randomSpawn.remove(randomSpawn);
    }

    public List<String> getRandomSpawnStrings(){
        List<String> strings = new ArrayList<>();
        if(randomSpawn == null){
            return new ArrayList<>();
        }
        for(Location location : randomSpawn){
            strings.add("\""+location.x+":"+location.y+":"+location.z+"\"");
        }
        return strings;
    }

    public List<String> getRandomSheepSpawnStrings(SheepType sheepType){
        List<String> strings = new ArrayList<>();
        if(sheepSpawnPos.get(sheepType) == null){
            return new ArrayList<>();
        } else {
            sheepSpawnPos.get(sheepType);
        }
        for(Location location : sheepSpawnPos.get(sheepType)){
            strings.add("\""+location.x+":"+location.y+":"+location.z+"\"");
        }
        return strings;
    }
}
