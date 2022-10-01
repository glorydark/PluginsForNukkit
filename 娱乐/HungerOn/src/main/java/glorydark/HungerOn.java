package glorydark;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.commands.Commands;
import glorydark.events.EntityEvents;
import glorydark.events.PlayerEvents;

import java.io.File;

public class HungerOn extends PluginBase{
    public static int runtime = 0;
    public static String filepath = null;

    @Override
    public void onLoad(){
        filepath = this.getDataFolder().getPath();
        saveDefaultConfig();
        saveResource("languages/English.yml",false);
        saveResource("languages/简体中文.yml",false);
        this.getLogger().info(getlang().getString("PluginLoadMessage"));
    }

    @Override
    public void onEnable() {
        this.getLogger().info(getlang().getString("PluginEnableMessage"));
        this.getServer().getPluginManager().registerEvents(new EntityEvents(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        this.getServer().getCommandMap().register("", new Commands("HungerOn"));
        File itemcfg = new File(this.getDataFolder()+"/item.yml");
        if(!itemcfg.exists()){
            saveResource("config.yml");
        }
        Config cfg = new Config(this.getDataFolder()+"/records.yml",Config.YAML);
        runtime = this.getServer().getTick();
    }

    @Override
    public void onDisable() {
        this.getLogger().info(getlang().getString("PluginDisableMessage"));
    }

    public static int getHungerValue(String Name){
        Config cfg = new Config(filepath+"/records.yml",Config.YAML);
        if(!cfg.exists(Name)){
            cfg.set(Name,0);
            cfg.save();
        }
        return cfg.getInt(Name);
    }

    public static String getConfigValue(int type, String Name){
        Config cfg = new Config(filepath+"/config.yml",Config.YAML);
        if(cfg.exists(Name)){
            switch (type) {
                case 0:
                    return cfg.getString(Name,"1");
                case 1:
                    return String.valueOf(cfg.getInt(Name,0));
                case 2:
                    return String.valueOf(cfg.getBoolean(Name,true));
            }
        }
        return "0";
    }

    public static void setHungerValue(String Name, double amount){
        Config cfg = new Config(filepath+"/records.yml",Config.YAML);
        cfg.set(Name, amount);
        cfg.save();
    }

    public static Config getlang(){
        Config cfg = new Config(filepath+"/config.yml",Config.YAML);
        if(cfg.exists("language")){
            String lang = cfg.getString("language","简体中文");
            return new Config(filepath+"/languages/"+ lang +".yml",Config.YAML);
        }
        return new Config(filepath+"/languages/简体中文.yml",Config.YAML);
    }

    public static double GetItemHoldDecreaseRate(Player p){
        int id = p.getInventory().getItemInHand().getId();
        Config cfg = new Config(HungerOn.filepath+"/item.yml",Config.YAML);
        if(cfg.exists(String.valueOf(id))) {
            return cfg.getDouble(String.valueOf(id), 1.000000);
        }else{
            return 1.000000;
        }
    }
}
