package glorydark.SimpleDialogues.charmsystem;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.Achievement;
import glorydark.SimpleDialogues.Dialogue;
import glorydark.SimpleDialogues.MainClass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AchievementMain {
    public static HashMap<String, Achievement> achievements = new HashMap<>();

    public static void loadAll(){
        File path = new File(MainClass.path+"/achievements/");
        if(path.exists()) {
            File[] files = path.listFiles();
            if (files != null && files.length > 0) {
                HashMap<String, Achievement> cacheHashMap = new HashMap<>();
                for (File file : files) {
                    Config config = new Config(file, Config.YAML);
                    Achievement achievement = Achievement.parseConfig(config);
                    if (achievement != null) {
                        String name = file.getName().split("\\.")[0];
                        cacheHashMap.put(name, achievement);
                        MainClass.plugin.getLogger().info(TextFormat.GREEN + "Achievement: " + name + " loaded");
                    } else {
                        MainClass.plugin.getLogger().alert("Achievement File: " + file.getName() + " can not be loaded");
                    }
                }
                achievements = cacheHashMap;
                MainClass.plugin.getLogger().info(TextFormat.GREEN + "All the achievements files have been loaded!");
            } else {
                MainClass.plugin.getLogger().info(TextFormat.GREEN + "No achievement file has been loaded!");
            }
        }
    }

    public static void saveAll(){
        for(String s: AchievementMain.achievements.keySet()){
            Config config = new Config(MainClass.path+"/achievements/"+s+".yml",Config.YAML);
            List<String> stringList = config.getStringList("领取记录");
            if(stringList != null){
                config.set("领取记录", AchievementMain.achievements.get(s).getRecords());
                config.save();
            }
        }
        MainClass.plugin.getLogger().info(TextFormat.GREEN + "Achievement Records have been saved!");
    }
}
