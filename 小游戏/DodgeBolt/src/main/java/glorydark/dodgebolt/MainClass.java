package glorydark.dodgebolt;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import gameapi.room.Room;
import gameapi.room.RoomRule;
import gameapi.utils.AdvancedLocation;
import glorydark.dodgebolt.forms.GuiListener;
import glorydark.dodgebolt.listener.GameListener;
import glorydark.dodgebolt.utils.ExtendRoomData;
import glorydark.dodgebolt.utils.WallPos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//简单pvp写法
public class MainClass extends PluginBase {

    public static Config lang;

    public static LinkedHashMap<Room, ExtendRoomData> roomList = new LinkedHashMap<>();

    @Override
    public void onLoad() {
        this.getLogger().info("DodgeBolt onLoad");
    }

    @Override
    public void onEnable() {
        this.saveResource("rooms.yml", false);
        this.saveResource("lang.yml", false);
        Config config = new Config(this.getDataFolder().getPath()+"/rooms.yml", Config.YAML);
        lang = new Config(this.getDataFolder().getPath()+"/lang.yml", Config.YAML);
        RoomRule roomRule = new RoomRule(0);
        roomRule.allowDamagePlayer = true;
        roomRule.allowEntityExplosionDamage = true;
        roomRule.allowProjectTileDamage = true;
        roomRule.noStartWalk = false;
        roomRule.allowFoodLevelChange = false;
        //设置房间规则
        for(String key: config.getKeys(false)) {
            String backup = config.getString(key + ".world_name");
            int gameTime = config.getInt(key + ".game_time", 60);
            Room room = new Room("DodgeBolt", roomRule, backup, 1);
            if(gameTime != 0) {
                room.setGameTime(gameTime);
            }else{
                roomRule.noTimeLimit = true;
                room.setRoomRule(roomRule);
                room.setGameTime(3);
            }
            room.setWaitSpawn(config.getString(key + ".spawn_wait", "null"));
            room.setEndSpawn(config.getString(key + ".spawn_end", "null"));
            room.setWaitTime(config.getInt(key + ".wait_time", 10));
            room.setGameWaitTime(config.getInt(key + ".ready_time", 10));
            room.setCeremonyTime(config.getInt(key + ".ceremony_time", 10));
            room.setMaxPlayer(config.getInt(key + ".max_players", 10));
            room.setMinPlayer(2); //两边各一人至少
            room.setResetMap(false);
            room.setRoomName(config.getString(key+".name", "未命名"));
            room.setLoseConsoleCommands(new ArrayList<>(config.getStringList(key + ".lose_commands")));
            room.setWinConsoleCommands(new ArrayList<>(config.getStringList(key + ".win_commands")));
            List<String> spectatorSpawnsString = new ArrayList<>(config.getStringList(key+".spectator_spawn"));
            spectatorSpawnsString.forEach(room::addSpectatorSpawn);
            Room.loadRoom(room);
            ExtendRoomData roomData = new ExtendRoomData(new AdvancedLocation(config.getString(key + ".arrow_spawn1", "null")), new AdvancedLocation(config.getString(key + ".arrow_spawn2", "null")));
            WallPos pos = new WallPos(new AdvancedLocation(config.getString(key + ".wall_pos1", "null")), new AdvancedLocation(config.getString(key + ".wall_pos2", "null")));
            roomData.setWallPos(pos);
            roomData.setTeam1_spawn(new AdvancedLocation(config.getString(key + ".spawn_start_team1", "null")));
            roomData.setTeam2_spawn(new AdvancedLocation(config.getString(key + ".spawn_start_team2", "null")));
            roomData.setMax_score(config.getInt(key+".round", 1));
            roomList.put(room, roomData);
        }
        //房间设置完成
        this.getServer().getPluginManager().registerEvents(new GameListener(), this);
        this.getServer().getPluginManager().registerEvents(new GuiListener(), this);
        this.getServer().getCommandMap().register("", new GameCommand("dodgebolt"));
        this.getLogger().info("DodgeBolt onEnable");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DodgeBolt onDisable");
    }
}