package testgame.scripts;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import gameapi.GameAPI;
import gameapi.effect.Effect;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.utils.GsonAdapter;
import lombok.Getter;
import lombok.Setter;
import testgame.MainClass;
import testgame.scripts.controller.Controller;
import testgame.scripts.controller.type.TriggerControllerData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Glorydark
 */
@Setter
@Getter
public class CustomSkill { //常规药水技能
    private String customName;
    private String description;
    private List<Effect> effects = new ArrayList<>();
    private Item item;
    private Integer coolDownTick;
    private Integer duration = 0;
    private Map<String, Object> player_property = new LinkedHashMap<>();

    private boolean isCoolDown = false;

    private List<Controller> controllers = new ArrayList<>();

    //private List<Controller> controllers = new ArrayList<>();

    private List<String> startConsoleCommands = new ArrayList<>();

    private List<String> endConsoleCommands = new ArrayList<>();

    private List<String> startPlayerCommands = new ArrayList<>();

    private List<String> endPlayerCommands = new ArrayList<>();

    private String identifier = "";

    public CustomSkill(String identifier) {
        this(new File(GameAPI.path + "/scripts/skills/" + identifier + ".json"));
    }
    public CustomSkill(File file){
        this.identifier = file.getName().replace(".json", "");
        if(!file.exists()){
            GameAPI.plugin.getLogger().warning("Can not find the skill, identifier: " + identifier);
            return;
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>()
        {
        }.getType(), new GsonAdapter()).create();

        Map<String, Object> mainMap;
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)); //一定要以utf-8读取
            mainMap = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> skill_data = (Map<String, Object>) mainMap.get("skill_data");
        this.customName = (String) skill_data.get("name");
        this.description = (String) skill_data.get("description");
        this.coolDownTick = (Integer) skill_data.get("cooldown");
        this.duration = (Integer)skill_data.get("duration");
        Map<String, Object> item_data = (Map<String, Object>) skill_data.get("item_data");
        Item item = new Item((Integer)item_data.get("id"), (Integer)item_data.get("meta"), 1);
        item.setLore(description);
        item.setCustomName(customName);
        if(item_data.containsKey("compoundtag") && item_data.get("compoundtag") instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) item_data.get("compoundtag");
            CompoundTag tag;
            if (item.hasCompoundTag()) {
                tag = item.getNamedTag();
            } else {
                tag = new CompoundTag();
            }
            for (String key : map.keySet()) {

                if (map.get(key) instanceof Integer) {
                    tag.putInt(key, (Integer) map.get(key));
                }
                if (map.get(key) instanceof Double) {
                    tag.putDouble(key, (Double) map.get(key));
                }
                if (map.get(key) instanceof String) {
                    tag.putString(key, (String) map.get(key));
                }
                if (map.get(key) instanceof Boolean) {
                    tag.putBoolean(key, (Boolean) map.get(key));
                }
                if (map.get(key) instanceof Integer[]) {
                    tag.putIntArray(key, (int[]) map.get(key));
                }
                if (map.get(key) instanceof Float) {
                    tag.putFloat(key, (Float) map.get(key));
                }
                if (map.get(key) instanceof Long) {
                    tag.putLong(key, (Long) map.get(key));
                }
            }
            tag.putByte("minecraft:item_lock", 1);
            tag.putByte("minecraft:keep_on_death", 1);
            tag.putString("ItemType", "skillItem");
            item.setCompoundTag(tag);
            //tag.getAllTags().forEach(tag1 -> MainClass.plugin.getLogger().info(tag1.getName()+":"+ tag1.parseValue()));
        }
        this.item = item;
        if(skill_data.containsKey("player_property")){
            player_property = (Map<String, Object>) skill_data.get("player_property");
        }
        if(skill_data.containsKey("effects")){
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) skill_data.get("effects");
            for(Map<String, Object> map: mapList) {
                Effect effect = new Effect(map);
                effects.add(effect);
            }
        }
        if(skill_data.containsKey("controllers")){
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) skill_data.get("controllers");
            for(Map<String, Object> map: mapList) {
                Controller trigger = new Controller((String) map.get("binding_type"), (String) map.get("binding_value"));
                if(trigger.getBinding_type().startsWith("event")){
                    TriggerListener.addEventList((String) trigger.getBinding_value());
                }
                if(map.containsKey("check_data")) {
                    List<Map<String, Object>> dataMap = (List<Map<String, Object>>) map.get("check_data");
                    List<TriggerControllerData> dataList = new ArrayList<>();
                    dataMap.forEach(dataValue -> {
                        TriggerControllerData data = new TriggerControllerData((String) dataValue.get("object"), (String) dataValue.get("check_type"), dataValue.get("check_value"));
                        dataList.add(data);
                    });
                    trigger.setTriggerControllerData(dataList);
                }
                controllers.add(trigger);
            }
        }
        /*
        if(skill_data.containsKey("controllers")){
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) skill_data.get("controllers");
            for(Map<String, Object> map: mapList) {
                Controller trigger = new Controller((String) map.get("binding_type"), (String) map.get("binding_value"));
                controllers.add(trigger);
            }
        }
         */
        if(skill_data.containsKey("commands")){
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) skill_data.get("commands");
            for(Map<String, Object> map: mapList) {
                if(map.get("type").equals("start")){
                    if(map.getOrDefault("isConsole", true).equals(true)){
                        startConsoleCommands.add((String) map.get("text"));
                    }else{
                        startPlayerCommands.add((String) map.get("text"));
                    }
                }
                if(map.get("type").equals("end")){
                    if(map.getOrDefault("isConsole", true).equals(true)){
                        endConsoleCommands.add((String) map.get("text"));
                    }else{
                        endPlayerCommands.add((String) map.get("text"));
                    }
                }
            }
        }
    }

    //v1
    public CustomSkill(String customName, String description, Item item, List<Effect> effects, Integer coolDownTick){
        this.customName = customName;
        this.description = description;
        this.effects = effects;
        CompoundTag tag;
        if (item.hasCompoundTag()) {
            tag = item.getNamedTag();
        } else {
            tag = new CompoundTag();
        }
        item.setLore(description);
        item.setCustomName(customName);
        tag.putString("ItemType", "skillItem");
        item.setCompoundTag(tag);
        this.item = item;
        this.coolDownTick = coolDownTick;
    }

    public void giveSkillItem(Player player, Boolean sendMsg){
        removeSkillItem(player);
        player.getInventory().setItem(8,this.getItem());
        player.sendMessage(TextFormat.YELLOW + "You obtained the skill:" + customName);
        this.isCoolDown = false;
    }

    public void removeSkillItem(Player player){
        for(Item check: player.getInventory().getContents().values()) {
            if (check.hasCompoundTag()) {
                if (check.getNamedTag().contains("ItemType")) {
                    if (check.getNamedTag().getString("ItemType").equals("skillItem")) {
                        player.getInventory().remove(check);
                    }
                }
            }
        }
    }

    public void triggerSkill(Player player, Object... param){
        Room room = Room.getRoom(player);
        if(room == null){ return; }
        if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){ return; }
        Item item = player.getInventory().getItemInHand();
        if(!item.equals(this.getItem())){ return; }
        if(this.isCoolDown){
            player.sendMessage("Please wait for the coolDown");
            return;
        }
        this.isCoolDown = true;
        if(GameAPI.debug.contains(player)) {
            player.sendMessage("[GameTest] Check Controller success!");
        }
        player.sendMessage(TextFormat.GOLD + "You use the skill:" + this.getCustomName());
        Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.plugin, ()->{
            Room room1 = Room.getRoom(player);
            if(room1 == null){ return; }
            if(room1.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){ return; }
            this.giveSkillItem(player, false);
            player.sendMessage("Skill recharged!");
            for(String string: endConsoleCommands){
                Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName()));
                if(!player.isOnline()){ return; }
                player.setScale(player.getScale());
            }
            for(String string: endPlayerCommands){
                if(!player.isOnline()){ return; }
                Server.getInstance().dispatchCommand(player, string.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName()));
                player.setScale(player.getScale());
            }
        }, coolDownTick);
        for(String string: startConsoleCommands){
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName()));
        }
        if(!player.isOnline()){ return; }
        player.getInventory().remove(this.getItem());
        for(Effect effect: effects){
            effect.giveEffect(player);
        }
        for(String string: startPlayerCommands){
            if(param.length == 0) {
                Server.getInstance().dispatchCommand(player, string.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName()));
            }else{
                String modified = string.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName());
                modified = modified.replace("%damager%", ((Player)param[0]).getName());
                modified = modified.replace("%victim%", ((Player)param[1]).getName());
                Server.getInstance().dispatchCommand(player, modified);
            }
        }
        for(String property: player_property.keySet()){
            switch (property){
                case "scale":
                    player.setScale(player.getScale()*(Integer)player_property.get(property));
                    break;
                case "delta_health":
                    player.setHealth(player.getHealth()+(int)player_property.get(property));
                    break;
                case "motion":
                    Map<String, Integer> map = (Map<String, Integer>) player_property.get("motion");
                    if(map.getOrDefault("x", null) != null && map.getOrDefault("y", null) != null && map.getOrDefault("z", null) != null){
                        player.addMotion(map.get("x"),map.get("y"),map.get("z"));
                    }
                    if(map.getOrDefault("multiply", null) != null){
                        knockBack(player, map.get("multiply"), map.get("multiply"), 1.0);
                    }
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "CustomSkill{" +
                "customName='" + customName + '\'' +
                ", description='" + description + '\'' +
                ", effects=" + effects +
                ", item=" + item +
                ", coolDownTick=" + coolDownTick +
                ", duration=" + duration +
                ", player_property=" + player_property +
                ", triggers=" + controllers +
                ", startConsoleCommands=" + startConsoleCommands +
                ", endConsoleCommands=" + endConsoleCommands +
                ", startPlayerCommands=" + startPlayerCommands +
                ", endPlayerCommands=" + endPlayerCommands +
                ", identifier='" + identifier + '\'' +
                '}';
    }

    public void knockBack(Player entity, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (!(f <= 0.0)) {
            f = 1.0 / f;
            Vector3 motion = new Vector3();
            motion.x /= 2.0;
            motion.y /= 2.0;
            motion.z /= 2.0;
            motion.x += x * f * base;
            motion.y += base;
            motion.z += z * f * base;
            if (motion.y > base) {
                motion.y = base;
            }
            entity.setMotion(motion);
        }
    }

    public static CustomSkill getSkill(String identifier){
        if(!MainClass.skills.containsKey(identifier)){ GameAPI.plugin.getLogger().warning("Can not get skill script!"); return null;}
        return MainClass.skills.get(identifier);
    }

    public void loadSkill(){
        GameAPI.plugin.getLogger().alert("Loading skill, loading name:"+ this.getIdentifier());
        if(!MainClass.skills.containsValue(this)) {
            MainClass.skills.put(this.getIdentifier(), this);
        }
    }
}
