package glorydark.sheepwar;

import cn.nukkit.Server;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import gameapi.arena.Arena;
import gameapi.room.Room;
import gameapi.room.RoomRule;
import gameapi.room.RoomStatus;
import glorydark.sheepwar.RoomEvent.SheepSpawnEvent;
import glorydark.sheepwar.command.SheepWarOpCommand;
import glorydark.sheepwar.command.SheepWarPlayerCommand;
import glorydark.sheepwar.gui.GuiListener;
import glorydark.sheepwar.settings.SetGameInit;
import glorydark.sheepwar.utils.*;

import java.util.*;

public class SheepWarMain extends PluginBase {
    public static LinkedHashMap<Room, ExtendRoomData> rooms = new LinkedHashMap<>(); //Save Rooms and Their ExtendData
    public static String path = "";
    public static List<RefreshItem> items = new ArrayList<>();

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        path = this.getDataFolder().getPath();
        this.saveResource("rooms.yml", false);
        this.saveResource("supplybox.yml", false);
        this.getServer().getPluginManager().registerEvents(new GuiListener(), this);
        this.getServer().getPluginManager().registerEvents(new SheepWarListener(), this);
        this.getServer().getPluginManager().registerEvents(new SetGameInit(), this);
        this.getServer().getCommandMap().register("", new SheepWarPlayerCommand());
        this.getServer().getCommandMap().register("", new SheepWarOpCommand());
        this.loadRoom();
        this.loadSupplyBox();
        try {
            Class.forName("com.smallaswater.npc.variable.VariableManage");
            try {
                com.smallaswater.npc.variable.VariableManage.addVariableV2("SheepWarVariable", RsNpcXVariableV2.class);
            } catch (Exception e) {
                com.smallaswater.npc.variable.VariableManage.addVariable("SheepWarVariable", RsNpcXVariable.class);
            }
        } catch (Exception ignored) {
            this.getLogger().info("您还未安装Rsnpcx！");
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static void spawnSheep(Room room, Position position, SheepType type){
        if(!rooms.containsKey(room)){ return; }
        position.setLevel(rooms.get(room).getStartLevel());
        EntitySheep sheep = new EntitySheep(position.getChunk(),EntitySheep.getDefaultNBT(position));
        DyeColor dyeColor = DyeColor.WHITE;
        switch (type){
            case NormalSheep:
                sheep.setNameTag(TextFormat.WHITE+"§l白羊羊");
                break;
            case Red_Sheep:
                dyeColor = DyeColor.RED;
                sheep.setNameTag(TextFormat.RED+"§l红羊羊");
                break;
            case Black_Sheep:
                dyeColor = DyeColor.BLACK;
                sheep.setNameTag(TextFormat.BLACK+"§l黑羊羊");
                break;
            case Green_Sheep:
                dyeColor = DyeColor.GREEN;
                sheep.setNameTag(TextFormat.GREEN+"§l绿羊羊");
                break;
            case Blue_Sheep:
                dyeColor = DyeColor.BLUE;
                sheep.setNameTag(TextFormat.BLUE+"§l蓝羊羊");
                break;
            case Purple_Sheep:
                dyeColor = DyeColor.PURPLE;
                sheep.setNameTag(TextFormat.LIGHT_PURPLE+"§l紫羊羊");
                break;
            case Gold_Sheep:
                dyeColor = DyeColor.ORANGE;
                sheep.setNameTag(TextFormat.GOLD+"§l金羊羊");
                break;
        }
        sheep.setColor(dyeColor.getWoolData());
        sheep.setNameTagVisible(true);
        sheep.setNameTagAlwaysVisible(true);
        sheep.spawnToAll();
        Server.getInstance().getPluginManager().callEvent(new SheepSpawnEvent(room, type, position));
    }

    public void loadSupplyBox(){
        Config config = new Config(path+"/supplybox.yml",Config.YAML);
        for(String string:config.getStringList("items")){
            String[] s1 = string.split("#");
            String itemString = s1[1];
            Item item = this.getItemByString(itemString);
            if(item != null) {
                items.add(new RefreshItem(item, Integer.parseInt(s1[0])));
            }
        }
    }

    public void loadRoom(){
        Config config = new Config(path+ "/rooms.yml",Config.YAML);
        Set<String> keys = config.getKeys(false);
        if(keys.size() < 1){ return; }
        for(String key: keys){
            Map<String, Object> map = (Map<String, Object>) config.get(key);
            //房间规则
            RoomRule roomRule = new RoomRule(0);
            roomRule.allowDamagePlayer = true;
            roomRule.allowPlaceBlock = false;
            roomRule.allowBreakBlock = false;
            roomRule.allowFallDamage = false;
            roomRule.allowFoodLevelChange = false;
            roomRule.noStartWalk = false;
            Room room = new Room("SheepWar",roomRule,map.get("游戏世界").toString(), 1);
            String newName = room.getGameName() + "_" + map.get("游戏世界").toString() + "_" + UUID.randomUUID();
            if (Arena.copyWorldAndLoad(newName, map.get("游戏世界").toString())) {
                if (Server.getInstance().isLevelLoaded(newName)) {
                    Server.getInstance().getLevelByName(newName).setAutoSave(false);
                }else{
                    continue;
                }
            }else{
                continue;
            }
            room.setRoomName(key);
            //基础配置
            room.setGameWaitTime(Integer.parseInt(map.get("预备时间").toString()));
            room.setMaxPlayer(Integer.parseInt(map.get("最大人数").toString()));
            room.setMinPlayer(Integer.parseInt(map.get("最小人数").toString()));
            room.setGameTime(Integer.parseInt(map.get("游戏时间").toString()));
            room.setWaitTime(Integer.parseInt(map.get("等待时间").toString()));
            room.setCeremonyTime(Integer.parseInt(map.get("颁奖时间").toString()));
            room.setResetMap(Boolean.parseBoolean(map.get("是否重置地图").toString()));
            //在设置房间
            String endPos = map.get("结束地点").toString();
            if(!Server.getInstance().isLevelLoaded(endPos.split(":")[3])) {
                endPos = endPos.replace(room.getRoomLevelBackup(), newName);
            }
            String waitPos = map.get("等待地点").toString().replace(room.getRoomLevelBackup(), newName);
            if(!Server.getInstance().isLevelLoaded(waitPos.split(":")[3])) {
                waitPos = waitPos.replace(room.getRoomLevelBackup(), newName);
            }
            room.setWaitSpawn(waitPos);
            room.setEndSpawn(endPos);
            //羊羊坐标
            HashMap<SheepType, List<Location>> hashMap = new HashMap<>();
            Level startLevel = Server.getInstance().getLevelByName(newName);
            if(startLevel == null){
                if(!Server.getInstance().loadLevel(map.get("游戏世界").toString())){
                    this.getLogger().warning("房间"+room.getRoomName()+"加载失败！");
                    continue;
                }
            }
            hashMap.put(SheepType.NormalSheep, parseLocations(startLevel, (List<String>) map.get("白羊羊刷新坐标")));
            hashMap.put(SheepType.Black_Sheep, parseLocations(startLevel,(List<String>) map.get("黑羊羊刷新坐标")));
            hashMap.put(SheepType.Blue_Sheep, parseLocations(startLevel,(List<String>) map.get("蓝羊羊刷新坐标")));
            hashMap.put(SheepType.Gold_Sheep, parseLocations(startLevel,(List<String>) map.get("金羊羊刷新坐标")));
            hashMap.put(SheepType.Green_Sheep, parseLocations(startLevel,(List<String>) map.get("绿羊羊刷新坐标")));
            hashMap.put(SheepType.Purple_Sheep, parseLocations(startLevel,(List<String>) map.get("紫羊羊刷新坐标")));
            hashMap.put(SheepType.Red_Sheep, parseLocations(startLevel,(List<String>) map.get("红羊羊刷新坐标")));
            //出生点坐标
            List<Location> positions = new ArrayList<>();
            for(String string: (List<String>) map.get("随机出生点")) {
                positions.add(parseLocation(startLevel, string));
            }
            //补给箱坐标
            List<Location> chestPos = new ArrayList<>();
            for(String string: (List<String>) map.get("资源箱坐标")) {
                chestPos.add(parseLocation(startLevel, string));
            }
            ExtendRoomData roomData = new ExtendRoomData(hashMap, positions, chestPos, startLevel);
            rooms.put(room,roomData);
            room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
            Room.loadRoom(room);
            this.getLogger().alert("加载房间成功！房间名:" + key);
        }
    }

    public Location parseLocation(Level level, String pos){
        String[] posSplit = pos.split(":");
        return new Position(Double.parseDouble(posSplit[0]),Double.parseDouble(posSplit[1]), Double.parseDouble(posSplit[2]), level).getLocation();
    }

    public List<Location> parseLocations(Level level, List<String> pos){
        List<Location> array = new ArrayList<>();
        for(String string: pos) {
            array.add(parseLocation(level, string));
        }
        return array;
    }

    public Item getItemByString(String s){
        String[] a = s.split(":");
        Item item = new Item(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
        if (!a[3].equals("null")) {
            CompoundTag tag = Item.parseCompoundTag(Binary.hexStringToBytes(a[3]));
            item.setNamedTag(tag);
        }
        return item;
    }
}
