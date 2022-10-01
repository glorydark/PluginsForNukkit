package testgame;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.item.*;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import gameapi.arena.Arena;
import gameapi.effect.Effect;
import gameapi.room.Room;
import gameapi.room.RoomRule;
import gameapi.room.RoomStatus;
import testgame.scripts.CustomSkill;
import testgame.scripts.TriggerListener;

import java.io.File;
import java.util.*;

public class MainClass extends PluginBase implements Listener {

    public static List<Room> roomListHashMap = new ArrayList<>();
    public static List<String> maps = new ArrayList<>();
    public static HashMap<String, List<Effect>> effectHashMap = new HashMap<>();

    public static HashMap<String, CustomSkill> skills = new HashMap<>();
    public static String path = null;
    public static Map<String, Object> scoreboardCfg = new HashMap<>();

    public static boolean skillEnabled;

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onDisable() {
        roomListHashMap.clear();
        maps.clear();
        effectHashMap.clear();
        scoreboardCfg.clear();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.getLogger().info("欢迎使用本插件【RecknessHero】");
        this.getLogger().info("您安装的本插件不需要收费购买，作者minebbs昵称:Glorydark！");
        this.getServer().getPluginManager().registerEvents(this,this);
        this.getServer().getPluginManager().registerEvents(new Event(),this);
        this.getServer().getCommandMap().register("暴走英雄",new GameCommand("drh"));
        path = getDataFolder().getPath();
        this.saveResource("blockaddons.yml",false);
        this.saveResource("rooms.yml",false);
        this.saveResource("maps.yml",false);
        this.saveResource("skills.yml",false);
        this.saveResource("scoreboard.yml",false);
        this.loadRooms();
        this.loadBlockAddons();
        this.loadScoreboardSetting();
        this.saveResource("skills/DRecknessHero_SpeedUp.json", false);
        this.saveResource("skills/DRecknessHero_JumpBoost.json", false);
        this.saveResource("skills/DRecknessHero_Builder.json", false);
        Config config = new Config(path+"/skills.yml");
        skillEnabled = config.getBoolean("enabled", true);
        if(skillEnabled) {
            this.getServer().getPluginManager().registerEvents(new TriggerListener(), this);
            for (String fileName : new ArrayList<>(config.getStringList("scripts"))) {
                File file = new File(this.getDataFolder().getPath() + "/skills/" + fileName + ".json");
                if (file.exists()) {
                    CustomSkill skill = new CustomSkill(file);
                    skill.loadSkill();
                }
            }
        }
        Config mapsCfg = new Config(path+"/maps.yml");
        maps.addAll(mapsCfg.getKeys(false));
    }

    public void loadScoreboardSetting(){
        this.getLogger().info("正在加载计分板设置...");
        Config config = new Config(this.getDataFolder()+"/scoreboard.yml",Config.YAML);
        scoreboardCfg = config.getAll();
        this.getLogger().info("记分板设置加载成功！");
    }

    public static String getScoreboardSetting(String key){
        if(scoreboardCfg.containsKey(key)){
            return String.valueOf(scoreboardCfg.get(key));
        }else{
            return "null";
        }
    }

    public void loadBlockAddons(){
        Config config = new Config(this.getDataFolder()+"/blockaddons.yml",Config.YAML);
        effectHashMap = new HashMap<>();
        for(String string: config.getKeys(false)){
            this.getLogger().info("正在加载方块"+string+"的拓展数据");
            for(Room room:roomListHashMap){
                RoomRule roomRule = room.getRoomRule();
                roomRule.canBreakBlocks.add(string);
                room.setRoomRule(roomRule);
                this.getLogger().info("方块"+string+"已被允许在游戏中破坏");
            }
            List<Effect> effectList = new ArrayList<>();
            for(String effectStr: config.getStringList(string+".effects")) {
                String[] effectSplit = effectStr.split(":");
                if(effectSplit.length == 3) {
                   gameapi.effect.Effect effect = new gameapi.effect.Effect(Integer.parseInt(effectSplit[0]),Integer.parseInt(effectSplit[1]),Integer.parseInt(effectSplit[2]));
                   this.getLogger().info(effect.toString());
                   effectList.add(effect);
                }
            }
            effectHashMap.put(string,effectList);
        }
        this.getLogger().info("方块拓展加载成功！");
    }

    public void loadRooms(){
        Config config = new Config(path+"/rooms.yml",Config.YAML);
        for(String s:config.getKeys(false)) {
            this.loadRoom(config.getString(s+".map", ""), s,config.getInt(s+".min", 1),config.getInt(s+".max", 16));
        }
    }

    public void loadRoom(String map, String roomName, Integer min, Integer max){
        RoomRule roomRule = new RoomRule(0);
        Config config = new Config(path+"/maps.yml", Config.YAML);
        roomRule.allowBreakBlock = false;
        roomRule.allowPlaceBlock = false;
        roomRule.allowFallDamage = false;
        roomRule.allowDamagePlayer = false;
        roomRule.allowHungerDamage = false;
        roomRule.allowFoodLevelChange = false;
        roomRule.noStartWalk = false;
        roomRule.canBreakBlocks.add("100:14");
        roomRule.canPlaceBlocks.add("152:0");
        roomRule.canBreakBlocks.addAll(effectHashMap.keySet());
        Room room = new Room("DRecknessHero", roomRule, "", 1);
        room.setRoomName(roomName);
        if (config.exists(map + ".LoadWorld")) {
            String backup = config.getString(map + ".LoadWorld");
            room.setRoomLevelBackup(backup);
            if (Server.getInstance().getLevelByName(config.getString(map)) == null) {
                String newName = room.getGameName() + "_" + backup + "_" + UUID.randomUUID();
                if (Arena.copyWorldAndLoad(newName, backup)) {
                    if (Server.getInstance().isLevelLoaded(newName)) {
                        Server.getInstance().getLevelByName(newName).setAutoSave(false);
                        this.getLogger().info("房间【" + backup + "】加载！");

                        if(config.exists(map+".WaitSpawn")){
                            room.setWaitSpawn(config.getString(map+".WaitSpawn").replace(backup, newName));
                        }else{
                            this.getLogger().info("房间【"+map+"】加载失败,请检查等待点配置！");
                            return;
                        }

                        if(config.exists(map+".StartSpawn")){
                            room.addStartSpawn(config.getString(map+".StartSpawn").replace(backup, newName));
                        }else{
                            this.getLogger().info("房间【"+map+"】加载失败,请检查出生地配置！");
                            return;
                        }

                        if(config.exists(map+".WaitTime")){
                            room.setWaitTime(config.getInt(map+".WaitTime"));
                        }else{
                            this.getLogger().info("房间【"+map+"】加载失败,请检查等待时间配置！");
                            return;
                        }

                        if(config.exists(map+".GameTime")){
                            room.setGameTime(config.getInt(map+".GameTime"));
                        }else{
                            this.getLogger().info("房间【"+map+"】加载失败,请检查游戏时间配置！");
                            return;
                        }
                        room.setMinPlayer(min);
                        room.setMaxPlayer(max);
                        room.setEndSpawn(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation());
                        Event.roomFinishPlayers.put(room,new ArrayList<>());
                        Room.loadRoom(room);
                        roomListHashMap.add(room);
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
                        room.setWinConsoleCommands(new ArrayList<>(config.getStringList(map+".WinCommands")));
                        room.setLoseConsoleCommands(new ArrayList<>(config.getStringList(map+".FailCommands")));
                        this.getLogger().info("房间【"+map+"】加载成功！");
                    } else {
                        this.getLogger().info("房间【" + backup + "】地图加载失败！");
                    }
                } else {
                    this.getLogger().info("房间【" + map + "】地图复制失败！");
                }
            } else {
                this.getLogger().info("房间【" + map + "】加载失败,请检查地图是否存在！");
            }
        } else {
            this.getLogger().info("房间【" + map + "】加载失败,请检查地图是否存在！");
        }
    }

    public static void processJoin(Room room, Player p){
        if(room != null){
            RoomStatus rs = room.getRoomStatus();
            if(rs == RoomStatus.ROOM_STATUS_WAIT || rs == RoomStatus.ROOM_STATUS_PreStart) {
                if(room.addPlayer(p)) {
                    p.getInventory().clearAll();
                    p.getUIInventory().clearAll();
                    p.setGamemode(2);
                    Item addItem1 = new ItemBookEnchanted();
                    addItem1.setCustomName("§l§c退出房间");
                    p.getInventory().setItem(0,addItem1);

                    Item addItem2 = new ItemEmerald();
                    addItem2.setCustomName("§l§a历史战绩");
                    p.getInventory().setItem(7,addItem2);

                    if(skillEnabled) {
                        room.setPlayerProperties(p.getName(), "skill1", "DRecknessHero_SpeedUp");
                        Item addItem3 = new ItemTotem(0);
                        addItem3.setCustomName("§l§e选择职业");
                        p.getInventory().setItem(8, addItem3);
                    }

                    if(room.getTemporary()) {
                        Item addItem4 = new ItemPaper(0);
                        addItem4.setCustomName("§l§e选择地图");
                        p.getInventory().setItem(1, addItem4);
                    }
                }else{
                    p.sendMessage("房间人数已满！");
                }
            }else{
                p.sendMessage("游戏已经开始！");
            }
        }else{
            p.sendMessage("该房间不存在！");
        }
    }

    public static List<Effect> getBlockAddonsInit(Block block){
        int blockid = block.getId();
        int blockmeta = block.getDamage();
        String s = blockid+":"+blockmeta;
        for(String string: MainClass.effectHashMap.keySet()){
            if(s.equals(string)){
                return MainClass.effectHashMap.get(string);
            }
        }
        return null;
    }
}
