package glorydark.fireworkshop.utils;

import cn.nukkit.Player;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import glorydark.fireworkshop.MainClass;
import glorydark.fireworkshop.task.ScheduleTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class config {

    public static Config getConfig(String filename){
        return new Config(MainClass.path+"/"+filename,Config.YAML);
    }

    public static ItemFirework.FireworkExplosion.ExplosionType getExplosionTypeByString(String s){
        if(s.equals("BURST")){
            return ItemFirework.FireworkExplosion.ExplosionType.BURST;
        }
        if(s.equals("LARGE_BALL")){
            return ItemFirework.FireworkExplosion.ExplosionType.LARGE_BALL;
        }
        if(s.equals("SMALL_BALL")){
            return ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL;
        }
        if(s.equals("STAR_SHAPED")){
            return ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED;
        }
        if(s.equals("CREEPER_SHAPED")){
            return ItemFirework.FireworkExplosion.ExplosionType.CREEPER_SHAPED;
        }
        return ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED;
    }

    public static DyeColor getColorByString(String s){
        if(s.equals("RED")){
            return DyeColor.RED;
        }
        if(s.equals("BLACK")){
            return DyeColor.BLACK;
        }
        if(s.equals("BLUE")){
            return DyeColor.BLUE;
        }
        if(s.equals("BROWN")){
            return DyeColor.BROWN;
        }
        if(s.equals("CYAN")){
            return DyeColor.CYAN;
        }
        if(s.equals("GRAY")){
            return DyeColor.GRAY;
        }
        if(s.equals("GREEN")){
            return DyeColor.GREEN;
        }
        if(s.equals("LIGHT_BLUE")){
            return DyeColor.LIGHT_BLUE;
        }
        if(s.equals("LIGHT_GRAY")){
            return DyeColor.LIGHT_GRAY;
        }
        if(s.equals("LIME")){
            return DyeColor.LIME;
        }
        if(s.equals("MAGENTA")){
            return DyeColor.MAGENTA;
        }
        if(s.equals("ORANGE")){
            return DyeColor.ORANGE;
        }
        if(s.equals("PINK")){
            return DyeColor.PINK;
        }
        if(s.equals("PURPLE")){
            return DyeColor.PURPLE;
        }
        if(s.equals("WHITE")){
            return DyeColor.WHITE;
        }
        if(s.equals("YELLOW")){
            return DyeColor.YELLOW;
        }
        return DyeColor.WHITE;
    }

    public static void parseTemplate(String TemplateName,Player p, Position pos){
        Config config = new Config(MainClass.path + "/Template/"+TemplateName+".yml");
        if(!config.exists("MaxTicks")){ return; }
        MainClass.playerfireworkcache.put(p,new HashMap<>());
        for (Object s:config.getKeys()){
            if(!s.equals("MaxTicks")) {
                int tick = Integer.parseInt(s.toString());
                List<FireworkData> data = new ArrayList<>();
                for(String s1: config.getStringList(String.valueOf(s))) {
                    String[] TemplateData = s1.split(":");
                    if (TemplateData.length != 5) {
                        return;
                    }
                    ItemFirework.FireworkExplosion.ExplosionType explosionType = getExplosionTypeByString(TemplateData[0]);
                    DyeColor color = getColorByString(TemplateData[1]);
                    Position position = new Position(pos.x + Double.parseDouble(TemplateData[2]), pos.y + Double.parseDouble(TemplateData[3]), pos.z + Double.parseDouble(TemplateData[4]), p.getLevel());
                    data.add(new FireworkData(explosionType, color, position));
                }
                MainClass.playerfireworkcache.get(p).put(tick, data);
            }
        }
        MainClass.MaxTickRecord.put(p,config.getInt("MaxTicks"));
        ScheduleTask.tickcache.put(p,0);
        return;
    }
}
