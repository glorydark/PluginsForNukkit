package glorydark.SimpleDialogues.charmsystem;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.MainClass;

import java.io.File;
import java.util.*;

public class CharmPoint {
    public static HashMap<String, Double> charmPointCache = new HashMap<>();
    public static HashMap<String, Double> originalCache = new HashMap<>();

    public static void loadAll(){
        File file = new File(MainClass.path+"/players.yml");
        if(file.exists()){
            Config config = new Config(file, Config.YAML);
            for(String string: config.getKeys(false)){
                charmPointCache.put(string, config.getDouble(string));
            }
            originalCache = charmPointCache;
            MainClass.plugin.getLogger().info(TextFormat.GREEN + "All players' Charming Point records have been loaded!");
        }else{
            Config config = new Config(file, Config.YAML);
            config.save();
            MainClass.plugin.getLogger().info(TextFormat.GREEN + "Created players.yml!");
        }
    }

    public static void saveAll(){
        MainClass.plugin.getLogger().info(TextFormat.GREEN + "saving players' Charming Point records......");
        Config config = new Config(MainClass.path+"/players.yml", Config.YAML);
        for(String key: charmPointCache.keySet()){
            /*
            if(originalCache.size() > 0 && originalCache.containsKey(key)){
                if(!Objects.equals(originalCache.get(key), charmPointCache.get(key))){
                    config.set(key, charmPointCache.get(key));
                }
            }else{
                config.set(key, charmPointCache.get(key));
            }
             */
            config.set(key, charmPointCache.get(key));
        }
        config.save();
        MainClass.plugin.getLogger().info(TextFormat.GREEN + "players' Charming Point records saved!");
    }

    public static Double getCharmPointCache(Player player) {
        if(player == null) { return 0d;}
        String p = player.getName();
        return charmPointCache.getOrDefault(p, 0d);
    }

    public static Double getCharmPointCache(String player) {
        return charmPointCache.getOrDefault(player, 0d);
    }

    public static void addCharmPoint(Player player, Double add){
        if(player == null) { return;}
        String p = player.getName();
        if(charmPointCache.containsKey(p)){
            charmPointCache.put(p, charmPointCache.get(p) + add);
        }else{
            charmPointCache.put(p, add);
        }
        if(player.isOnline()){
            player.sendMessage("恭喜您获得"+TextFormat.LIGHT_PURPLE+"魅力值*"+add);
        }
    }

    public static void setCharmPoint(Player player, Double amount){
        if(player == null) { return;}
        String p = player.getName();
        charmPointCache.put(p, amount);
    }

    public static void reduceCharmPoint(Player player, Double reduce){
        if(player == null) { return;}
        String p = player.getName();
        if(charmPointCache.containsKey(p)){
            charmPointCache.put(p, charmPointCache.get(p) - reduce);
        }else{
            charmPointCache.put(p, reduce);
        }
    }

    public static HashMap<String, Double> getRankingList(){
        HashMap<String, Double> cache = charmPointCache;
        List<Map.Entry<String, Double>> list = new ArrayList<>(cache.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        HashMap cache2 = new HashMap();
        for(Map.Entry<String, Double> entry:list){
            cache2.put(entry.getKey(),entry.getValue());
        }
        return cache2;
    }

    public static Integer getRank(Player player){
        if(player != null){
            HashMap hashMap = getRankingList();
            return new ArrayList<>(hashMap.keySet()).indexOf(player.getName()) + 1;
        }else{
            return 0;
        }
    }
}
