package glorydark.DLevelEventPlus.utils;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;
import com.sun.istack.internal.NotNull;
import glorydark.DLevelEventPlus.MainClass;

import java.io.File;
import java.util.*;

public class ConfigUtil {
    public static HashMap<String, LinkedHashMap<String, Object>> TemplateCache = new HashMap<>();

    public static void whiteList(CommandSender sender, int type, String name, String levelname){
        if(!Server.getInstance().lookupName(name).isPresent()){
            sender.sendMessage("§c[DLevelEventPlus] Can not find player: "+ name);
            return;
        }
        Config worldcfg = new Config(MainClass.path + "/whitelists.yml", cn.nukkit.utils.Config.YAML);
        List<String> arrayList = new ArrayList<>(worldcfg.getStringList(levelname));
        Player player = Server.getInstance().getPlayer(name);
        switch (type) {
            case 0:
                if (!arrayList.contains(name)) {
                    arrayList.add(name);
                    worldcfg.set(levelname, arrayList);
                    worldcfg.save();
                    if(player != null){
                        player.sendMessage("§a[DLevelEventPlus] You are in this world's whitelist now!");
                    }
                    sender.sendMessage("§a[DLevelEventPlus] 新增成功！");
                }else{
                    sender.sendMessage("§c[DLevelEventPlus] 新增失败，该玩家已在白名单内！");
                }
                break;
            case 1:
                if (arrayList.contains(name)) {
                    arrayList.remove(name);
                    worldcfg.set(levelname, arrayList);
                    worldcfg.save();
                    if(player != null){
                        player.sendMessage("§c[DLevelEventPlus] You are no longer in this world's whitelist!");
                    }
                    sender.sendMessage("§a[DLevelEventPlus] 移除成功！");
                }else{
                    sender.sendMessage("§c[DLevelEventPlus] 移除失败，该玩家不在白名单内！");
                }
                break;
        }
    }

    public static void adminList(CommandSender sender, int type, String name){
        if(!Server.getInstance().lookupName(name).isPresent()){
            sender.sendMessage("§c[DLevelEventPlus] Can not find player: "+ name);
            return;
        }
        Config worldcfg = new Config(MainClass.path + "/admins.yml", cn.nukkit.utils.Config.YAML);
        Player player = Server.getInstance().getPlayer(name);
        List<String> arrayList = new ArrayList<>(worldcfg.getStringList("list"));
        switch (type) {
            case 0:
                if (!arrayList.contains(name)) {
                    arrayList.add(name);
                    worldcfg.set("list", arrayList);
                    worldcfg.save();
                    if(player != null){
                        player.sendMessage("§a[DLevelEventPlus] You are admin now!");
                    }
                    sender.sendMessage("§a[DLevelEventPlus] 新增成功！");
                }else{
                    sender.sendMessage("§c[DLevelEventPlus] 新增失败，该玩家已为管理员！");
                }
                break;
            case 1:
                if (worldcfg.exists("list")) {
                    if (arrayList.contains(name)) {
                        arrayList.remove(name);
                        worldcfg.set("list", arrayList);
                        worldcfg.save();
                        if(player != null){
                            player.sendMessage("§c[DLevelEventPlus] You are no longer an admin!");
                        }
                        sender.sendMessage("§a[DLevelEventPlus] 移除成功！");
                    }
                }else{
                    sender.sendMessage("§c[DLevelEventPlus] 移除失败，该玩家不是管理员！");
                }
                break;
        }
    }

    public static void operatorList(CommandSender sender, int type, String name, String levelname){
        if(!Server.getInstance().lookupName(name).isPresent()){
            sender.sendMessage("§c[DLevelEventPlus] Can not find player: "+ name);
            return;
        }
        Player player = Server.getInstance().getPlayer(name);
        Config worldcfg = new Config(MainClass.path + "/operators.yml", cn.nukkit.utils.Config.YAML);
        List<String> arrayList = new ArrayList<>(worldcfg.getStringList(levelname));
        switch (type) {
            case 0:
                if (!arrayList.contains(name)) {
                    arrayList.add(name);
                    worldcfg.set(levelname, arrayList);
                    worldcfg.save();
                    if(player != null){
                        player.sendMessage("§a[DLevelEventPlus] You are operator of this world now!");
                    }
                    sender.sendMessage("§a[DLevelEventPlus] 新增成功！");
                }else{
                    sender.sendMessage("§c[DLevelEventPlus] 新增失败，该玩家已在白名单内！");
                }
                break;
            case 1:
                if (worldcfg.exists(levelname)) {
                    if (arrayList.contains(name)) {
                        arrayList.remove(name);
                        worldcfg.set(levelname, arrayList);
                        worldcfg.save();
                        if(player != null){
                            player.sendMessage("§c[DLevelEventPlus] You are no longer operator of this world!");
                        }
                        sender.sendMessage("§a[DLevelEventPlus] 移除成功！");
                    }else{
                        sender.sendMessage("§c[DLevelEventPlus] 移除失败，该玩家不在白名单内！");
                    }
                }
                break;
        }
    }

    public static boolean isAdmin(Player p) {
        if(p == null){ return false; }
        File file = new File(MainClass.path + "/admins.yml");
        if (file.exists()) {
            cn.nukkit.utils.Config worldcfg = new cn.nukkit.utils.Config(MainClass.path + "/admins.yml", cn.nukkit.utils.Config.YAML);
            if(worldcfg.exists("list")){
                return worldcfg.getStringList("list").contains(p.getName());
            }
        }
        return false;
    }

    public static boolean isOperator(Player p, Level level) {
        if(p == null){ return false; }
        if(level == null){ return false; }
        File file = new File(MainClass.path + "/operators.yml");
        if (file.exists()) {
            cn.nukkit.utils.Config worldcfg = new cn.nukkit.utils.Config(MainClass.path + "/operators.yml", cn.nukkit.utils.Config.YAML);
            if (worldcfg.get(level.getName()) != null) {
                return worldcfg.getStringList(level.getName()).contains(p.getName());
            }
        }
        return false;
    }

    public static boolean isWhiteListed(Player p, Level level){
        if(p == null){ return false; }
        if(level == null){ return false; }
        File file = new File(MainClass.path+"/whitelists.yml");
        if(file.exists()) {
            cn.nukkit.utils.Config worldcfg = new cn.nukkit.utils.Config(MainClass.path + "/whitelists.yml", cn.nukkit.utils.Config.YAML);
            if (worldcfg.exists(level.getName())) {
                return worldcfg.getStringList(level.getName()).contains(p.getName());
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    public static String getLang(String key, String subKey){
        if(MainClass.langConfig.containsKey(key)) {
            Map<String, Object> map = (Map<String, Object>) MainClass.langConfig.getOrDefault(key, null);
            if(map!=null) {
                return (String) map.getOrDefault(subKey, "Translation Not Found!");
            }else{
                return "Translation Not Found!";
            }
        }else{
            return "Translation Not Found!";
        }
    }

    public static List<String> getLangList(String text){
        if(MainClass.langConfig.containsKey(text)) {
            return (List<String>) MainClass.langConfig.get(text);
        }else{
            return new ArrayList<>();
        }
    }

    public static void setTemplateBooleanInit(String ConfigName, String key, String subKey, Boolean value){
        if(TemplateCache.containsKey(ConfigName)) {
            LinkedHashMap<String, Object> keyMap = TemplateCache.get(ConfigName);
            if(keyMap != null && keyMap.containsKey(key)) {
                Map<String, Object> obj = (Map<String, Object>) keyMap.get(key);
                if (obj != null) {
                    obj.put(subKey, value);
                    keyMap.put(key, obj);
                    TemplateCache.put(ConfigName, keyMap);
                } else {
                    MainClass.plugin.getLogger().warning("保存配置错误，错误原因: TemplateCache hasn't got a key called: " + subKey);
                }
            }
        }
    }

    @NotNull
    public static Boolean getTemplateBooleanInit(String ConfigName, String key, String subKey){
        if(TemplateCache.containsKey(ConfigName)){
            Map<String, Object> keyMap = TemplateCache.get(ConfigName);
            if(keyMap != null && keyMap.containsKey(key)){
                Map<String, Object> subkeyMap = (Map<String, Object>) keyMap.get(key); //键下的所有配置
                if(subkeyMap.containsKey(subKey)) {
                    return (Boolean) subkeyMap.get(subKey);
                }
            }
        }
        return false;
    }

    @NotNull
    public static List<String> getTemplateListInit(String ConfigName, String key, String subKey){
        if(TemplateCache.containsKey(ConfigName)){
            Map<String, Object> keyMap = TemplateCache.get(ConfigName);
            if(keyMap != null && keyMap.containsKey(key)){
                Map<String, Object> subkeyMap = (Map<String, Object>) keyMap.get(key); //键下的所有配置
                if(subkeyMap.containsKey(subKey)) {
                    return (List<String>) subkeyMap.get(subKey);
                }
            }
        }
        return new ArrayList<>();
    }
}
