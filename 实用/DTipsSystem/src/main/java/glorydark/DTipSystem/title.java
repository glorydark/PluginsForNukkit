package glorydark.DTipSystem;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;

public class title {
    public static String ModifyText(String s,Player player){
        return s.replace("%p%",player.getName()).replace("%level%", player.getLevel().getName()).replace("%position%", player.getPosition().toString()).replace("%displayname%", player.getDisplayName());
    }

    public static String ModifyTextByLevel(String s, Player player, Level level){
        return s.replace("%p%",player.getName()).replace("%level%", level.getName()).replace("%position%", player.getPosition().toString()).replace("%displayname%", player.getDisplayName());
    }

    public static Config getConfig(String filename){
        return new Config(MainClass.path+"/"+filename,Config.YAML);
    }

    public static void addTitle(Player p,String title,String subtitle,int fadeIn, int fadeOut, int duration){
        p.sendTitle(title,subtitle,fadeIn,duration,fadeOut);
    }

    public static void addActionBar(Player p,String content,int fadeIn, int fadeOut, int duration){
        p.sendActionBar(content,fadeIn,duration,fadeOut);
    }

    public static void addPopup(Player p,String title,String subtitle){
        p.sendPopup(title,subtitle);
    }

    public static void sendMessage(Player p,String Message){
        p.sendMessage(Message);
    }
}
