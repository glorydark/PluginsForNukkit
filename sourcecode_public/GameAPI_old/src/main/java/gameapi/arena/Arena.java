package gameapi.arena;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.room.RoomLevelData;
import gameapi.room.RoomStatus;
import gameapi.utils.FileHandler;
import gameapi.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author Glorydark
 * Some methods using in this class came from others, and you can find the original author in some specific classes!
 */
public class Arena {
    public static Boolean saveWorld(Level world){
        String rootPath = Server.getInstance().getFilePath()+"/worlds/"+world.getName()+"/";
        String savePath = GameAPI.path+"/worlds/"+world.getName()+"/";
        return FileUtil.copy(rootPath,savePath);
    }

    public static Boolean delWorld(String prefix){
        String rootPath = Server.getInstance().getDataPath()+"/worlds/";
        for(File file: Objects.requireNonNull(new File(rootPath).listFiles())) {
            if(file.getName().startsWith(prefix+"_")) {
                if(Server.getInstance().isLevelLoaded(file.getName())){
                    if(!Server.getInstance().getLevelByName(file.getName()).unload(true)) {
                        GameAPI.plugin.getLogger().warning("发现地图无法卸载，地图名:"+file.getName());
                        continue;
                    }
                }
                GameAPI.plugin.getLogger().warning("删除已复制地图，地图名:"+file.getName());
                FileUtil.delete(file);
            }
        }
        return true;
    }

    public static Boolean delWorldByName(String name){
        String rootPath = Server.getInstance().getDataPath()+"/worlds/"+name+"/";
        FileUtil.delete(new File(rootPath));
        return true;
    }

    public static Boolean copyWorld(String loadName,String backupName){
        Server.getInstance().broadcastMessage(Server.getInstance().getDataPath());
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+loadName+"/";
        String savePath = GameAPI.path+"/worlds/"+backupName+"/";
        return FileUtil.copy(savePath, worldPath);
    }

    public static Boolean copyWorldAndLoad(String loadName,String backupName) {
        String savePath = GameAPI.path+"/worlds/"+backupName;
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+loadName;
        if(new File(savePath).exists()) {
            if (FileUtil.copy(savePath, worldPath)) {
                return Server.getInstance().loadLevel(loadName);
            }
        }
        return false;
    }

    public static Boolean deleteDir(String saveWorld){
        String worldPath = Server.getInstance().getDataPath()+"/worlds/"+saveWorld+"/";
        File file = new File(worldPath);
        return FileUtil.delete(file);
    }

    public static void createVoidWorld(String worldname){
        Server.getInstance().generateLevel(worldname);
    }

    /* 钻石大陆
    public static void createDIYWorld(String worldname){
        Generator.addGenerator(FlatDIY.class, "DIY", Generator.TYPE_FLAT);
        Server.getInstance().generateLevel(worldname,0, Generator.getGenerator("DIY"));
        Server.getInstance().loadLevel(worldname);
    }

     */

    public static void reloadLevel(Room room) {
        HashMap<String, List<RoomLevelData>> strings = new HashMap<>();
        room.getStartSpawn().forEach(advancedLocation -> {
            String levelName = advancedLocation.getLevel().getName();
            List<RoomLevelData> data = strings.getOrDefault(levelName, new ArrayList<>());
            data.add(new RoomLevelData(advancedLocation, room, 1));
            strings.put(levelName, data);
        });
        //start
        room.getSpectatorSpawn().forEach(advancedLocation -> {
            String levelName = advancedLocation.getLevel().getName();
            List<RoomLevelData> data = strings.getOrDefault(levelName, new ArrayList<>());
            data.add(new RoomLevelData(advancedLocation, room, 3));
            strings.put(levelName, data);
        });

        if(room.getWaitSpawn() != null) {
            String waitName = room.getWaitSpawn().getLevel().getName();
            List<RoomLevelData> waitData = strings.getOrDefault(waitName, new ArrayList<>());
            waitData.add(new RoomLevelData(room.getWaitSpawn(), room, 0));
            strings.put(room.getWaitSpawn().getLevel().getName(), waitData);
        }

        if(room.getEndSpawn() != null) {
            String endName = room.getEndSpawn().getLevel().getName();
            List<RoomLevelData> endData = strings.getOrDefault(endName, new ArrayList<>());
            endData.add(new RoomLevelData(room.getEndSpawn(), room, 2));
            strings.put(room.getEndSpawn().getLevel().getName(), endData);
        }
        room.setStartSpawn(new ArrayList<>());
        room.setSpectatorSpawn(new ArrayList<>());
        for (String levelName: strings.keySet()) {
            String[] levelSplits = levelName.split("_");
            if(levelSplits.length == 3 && levelSplits[0].equals(room.getGameName()) && levelSplits[1].equals(room.getRoomLevelBackup())) {
                reloadLevelByName(room, levelName, strings.get(levelName));
            }
        }
        room.initRoom();
    }

    public static void reloadLevelByName(Room room, String levelName, List<RoomLevelData> data){
        Level level = Server.getInstance().getLevelByName(levelName);
        if (level == null) {
            GameAPI.plugin.getLogger().error("§c游戏房间: " + room.getRoomName() + "地图不存在！");
            room.setRoomStatus(RoomStatus.ROOM_MapLoadFailed);
            return;
        }
        if (level.getPlayers().values().size() > 0) {
            for (Player p : level.getPlayers().values()) {
                p.teleportImmediate(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
            }
        }
        if (level.getPlayers().values().size() > 0) {
            for (Player p : level.getPlayers().values()) {
                p.kick("Due to a unprecedented error, please rejoin the server");
            }
        }
        for (Entity e : level.getEntities()) {
            e.kill();
            e.close();
        }
        File levelFile = new File(Server.getInstance().getFilePath() + "/worlds/" + levelName);
        String newName = room.getGameName() + "_" + room.getRoomLevelBackup() + "_" + UUID.randomUUID();
        File newLevelFile = new File(Server.getInstance().getFilePath() + "/worlds/" + newName);
        File backup = new File(GameAPI.path + "/worlds/" + room.getRoomLevelBackup());
        if (!backup.exists()) {
            GameAPI.plugin.getLogger().error("§c游戏房间: " + levelName + " 地图备份不存在！还原失败！");
        }
        Server.getInstance().unloadLevel(level, true);
        //lt-name CrystalWars
        CompletableFuture.runAsync(() -> {
            if (FileUtil.delete(levelFile) && FileUtil.copy(backup, newLevelFile)) {
                if (Server.getInstance().loadLevel(newName)) {
                    if (Server.getInstance().isLevelLoaded(newName)) {
                        Level loadLevel = Server.getInstance().getLevelByName(newName);
                        room.setPlayLevel(loadLevel);
                        data.forEach(roomLevelData -> {
                            roomLevelData.resetLevel(loadLevel);
                        });
                        GameAPI.plugin.getLogger().info("§a游戏房间: " + levelName + " 地图还原完成！");
                    }
                } else {
                    GameAPI.plugin.getLogger().error("§c游戏房间: " + levelName + " 地图还原失败！请检查文件权限！");
                    GameAPI.RoomHashMap.remove(room);
                }
            } else {
                GameAPI.plugin.getLogger().error("§c游戏房间: " + levelName + " 地图还原失败！请检查文件权限！");
                GameAPI.RoomHashMap.remove(room);
            }
        }, GameAPI.THREAD_POOL_EXECUTOR);
    }
}
