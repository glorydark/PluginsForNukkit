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
            this.getLogger().info("???????????????Rsnpcx???");
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
                sheep.setNameTag(TextFormat.WHITE+"??l?????????");
                break;
            case Red_Sheep:
                dyeColor = DyeColor.RED;
                sheep.setNameTag(TextFormat.RED+"??l?????????");
                break;
            case Black_Sheep:
                dyeColor = DyeColor.BLACK;
                sheep.setNameTag(TextFormat.BLACK+"??l?????????");
                break;
            case Green_Sheep:
                dyeColor = DyeColor.GREEN;
                sheep.setNameTag(TextFormat.GREEN+"??l?????????");
                break;
            case Blue_Sheep:
                dyeColor = DyeColor.BLUE;
                sheep.setNameTag(TextFormat.BLUE+"??l?????????");
                break;
            case Purple_Sheep:
                dyeColor = DyeColor.PURPLE;
                sheep.setNameTag(TextFormat.LIGHT_PURPLE+"??l?????????");
                break;
            case Gold_Sheep:
                dyeColor = DyeColor.ORANGE;
                sheep.setNameTag(TextFormat.GOLD+"??l?????????");
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
            //????????????
            RoomRule roomRule = new RoomRule(0);
            roomRule.allowDamagePlayer = true;
            roomRule.allowPlaceBlock = false;
            roomRule.allowBreakBlock = false;
            roomRule.allowFallDamage = false;
            roomRule.allowFoodLevelChange = false;
            roomRule.noStartWalk = false;
            Room room = new Room("SheepWar",roomRule,map.get("????????????").toString(), 1);
            String newName = room.getGameName() + "_" + map.get("????????????").toString() + "_" + UUID.randomUUID();
            if (Arena.copyWorldAndLoad(newName, map.get("????????????").toString())) {
                if (Server.getInstance().isLevelLoaded(newName)) {
                    Server.getInstance().getLevelByName(newName).setAutoSave(false);
                }else{
                    continue;
                }
            }else{
                continue;
            }
            room.setRoomName(key);
            //????????????
            room.setGameWaitTime(Integer.parseInt(map.get("????????????").toString()));
            room.setMaxPlayer(Integer.parseInt(map.get("????????????").toString()));
            room.setMinPlayer(Integer.parseInt(map.get("????????????").toString()));
            room.setGameTime(Integer.parseInt(map.get("????????????").toString()));
            room.setWaitTime(Integer.parseInt(map.get("????????????").toString()));
            room.setCeremonyTime(Integer.parseInt(map.get("????????????").toString()));
            room.setResetMap(Boolean.parseBoolean(map.get("??????????????????").toString()));
            //???????????????
            String endPos = map.get("????????????").toString();
            if(!Server.getInstance().isLevelLoaded(endPos.split(":")[3])) {
                endPos = endPos.replace(room.getRoomLevelBackup(), newName);
            }
            String waitPos = map.get("????????????").toString().replace(room.getRoomLevelBackup(), newName);
            if(!Server.getInstance().isLevelLoaded(waitPos.split(":")[3])) {
                waitPos = waitPos.replace(room.getRoomLevelBackup(), newName);
            }
            room.setWaitSpawn(waitPos);
            room.setEndSpawn(endPos);
            //????????????
            HashMap<SheepType, List<Location>> hashMap = new HashMap<>();
            Level startLevel = Server.getInstance().getLevelByName(newName);
            if(startLevel == null){
                if(!Server.getInstance().loadLevel(map.get("????????????").toString())){
                    this.getLogger().warning("??????"+room.getRoomName()+"???????????????");
                    continue;
                }
            }
            hashMap.put(SheepType.NormalSheep, parseLocations(startLevel, (List<String>) map.get("?????????????????????")));
            hashMap.put(SheepType.Black_Sheep, parseLocations(startLevel,(List<String>) map.get("?????????????????????")));
            hashMap.put(SheepType.Blue_Sheep, parseLocations(startLevel,(List<String>) map.get("?????????????????????")));
            hashMap.put(SheepType.Gold_Sheep, parseLocations(startLevel,(List<String>) map.get("?????????????????????")));
            hashMap.put(SheepType.Green_Sheep, parseLocations(startLevel,(List<String>) map.get("?????????????????????")));
            hashMap.put(SheepType.Purple_Sheep, parseLocations(startLevel,(List<String>) map.get("?????????????????????")));
            hashMap.put(SheepType.Red_Sheep, parseLocations(startLevel,(List<String>) map.get("?????????????????????")));
            //???????????????
            List<Location> positions = new ArrayList<>();
            for(String string: (List<String>) map.get("???????????????")) {
                positions.add(parseLocation(startLevel, string));
            }
            //???????????????
            List<Location> chestPos = new ArrayList<>();
            for(String string: (List<String>) map.get("???????????????")) {
                chestPos.add(parseLocation(startLevel, string));
            }
            ExtendRoomData roomData = new ExtendRoomData(hashMap, positions, chestPos, startLevel);
            rooms.put(room,roomData);
            room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
            Room.loadRoom(room);
            this.getLogger().alert("??????????????????????????????:" + key);
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
