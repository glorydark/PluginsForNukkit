package glorydark.CloudInventory;

import cn.nukkit.utils.Config;

public class Lang {
    public static String getTranslation(String s){
        Config config = new Config(MainClass.plugin.getDataFolder()+"/lang.yml");
        return config.getString(s, "Can not find the translation!");
    }
}
