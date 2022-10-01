package glorydark.ashman;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainClass extends PluginBase {

    public Map<String, Object> lang;

    public String path;

    public Integer time = 0;

    public List<String> countDown_showTimes;

    public Integer clean_intervals;

    public Integer countDownStartTime;

    @Override
    public void onLoad() {
        this.getLogger().info("AshMan onLoad!");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        path = this.getDataFolder().getPath();
        new File(path+"/languages/").mkdirs();
        saveResource("languages/zh-cn.yml", false);
        Config config = new Config(path+"/config.yml", Config.YAML);
        if(config.exists("clean-animals-with-tags")){
            config.set("clean-animals-with-customName", false);
            config.remove("clean-animals-with-tags");
            config.save();
        }
        if(!config.exists("no-clean-type")){
            config.set("no-clean-type", new ArrayList<>());
            config.set("countdown-sendMessageSecond", "10|5|4|3|2|1");
            config.save();
        }
        String language = config.getString("language", "zh-cn");
        lang = new Config(path+"/languages/"+language+".yml", Config.YAML).getAll();
        clean_intervals = config.getInt("clean-intervals", 300);
        countDown_showTimes = new ArrayList<>(Arrays.asList(config.getString("countdown-sendMessageSecond", "5").split("\\|")));
        countDownStartTime = config.getInt("countdown-start", 20);
        Server.getInstance().getScheduler().scheduleRepeatingTask(this, ()->{
            if(Server.getInstance().getOnlinePlayers().values().size() == 0){
                return;
            }
            time++;
            if(countDown_showTimes.contains(String.valueOf(clean_intervals - time))) {
                this.getServer().broadcastMessage(this.getTranslation("specific-second-last").replace("%d", String.valueOf(clean_intervals - time)));
            }
            if(time.equals(clean_intervals)){
                cleanAllEntities();
                time = 0;
            }
        }, 20);
        this.getLogger().info("AshMan onEnabled! Author: Glorydark");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("AshMan onDisabled!");
    }

    public String getTranslation(String key){
        return (String) lang.getOrDefault(key, "null");
    }

    public void cleanAllEntities(){
        int mobs = 0;
        int drops = 0;
        Config config = new Config(path+"/config.yml", Config.YAML);
        List<String> levels = config.getStringList("no-clean-levels");
        for(Level level: Server.getInstance().getLevels().values()) {
            if (!levels.contains(level.getName())) {
                for (Entity entity : level.getEntities()) {
                    if (!(entity instanceof EntityHuman)) {
                        if(!config.getBoolean("clean-animals-with-customName")){
                            if(entity.hasCustomName()){
                                continue;
                            }
                        }
                        if(config.getStringList("no-clean-type").contains(getType(entity))){
                            continue;
                        }
                        if(config.getStringList("no-clean-type").contains(getCategory(entity))){
                            continue;
                        }
                        if (entity instanceof EntityItem) {
                            drops += 1;
                        } else {
                            mobs += 1;
                        }
                        level.removeEntity(entity);
                    }
                }
            }
        }
        this.getServer().broadcastMessage(this.getTranslation("clean-message").replace("%d1", String.valueOf(mobs)).replace("%d2", String.valueOf(drops)));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if((!(sender instanceof Player)) || sender.isOp()){
            cleanAllEntities();
            time = 0;
        }else{
            this.getLogger().info(this.getTranslation("no-permission"));
        }
        return true;
    }

    public String getCategory(Entity entity){
        if(entity instanceof EntityMinecartAbstract){
            return "EntityMinecartAbstract";
        }
        if(entity instanceof EntityItem){
            return "EntityItem";
        }
        if(entity instanceof EntityProjectile){
            return "EntityProjectile";
        }
        return "default";
    }

    public String getType(Entity entity){
        if(entity instanceof EntityBoat){
            return "EntityBoat";
        }
        if(entity instanceof EntityEndCrystal){
            return "EntityEndCrystal";
        }
        if(entity instanceof EntityExpBottle){
            return "EntityExpBottle";
        }
        if(entity instanceof EntityFallingBlock){
            return "EntityFallingBlock";
        }
        if(entity instanceof EntityFirework){
            return "EntityFirework";
        }
        if(entity instanceof EntityFishingHook){
            return "EntityFishingHook";
        }
        if(entity instanceof EntityMinecartChest){
            return "EntityMinecartChest";
        }
        if(entity instanceof EntityMinecartEmpty){
            return "EntityMinecartEmpty";
        }
        if(entity instanceof EntityMinecartHopper){
            return "EntityMinecartHopper";
        }
        if(entity instanceof EntityMinecartTNT){
            return "EntityMinecartTNT";
        }
        if(entity instanceof EntityPainting){
            return "EntityPainting";
        }
        if(entity instanceof EntityPotion){
            return "EntityPotion";
        }
        if(entity instanceof EntityPrimedTNT){
            return "EntityPrimedTNT";
        }
        if(entity instanceof EntityXPOrb){
            return "EntityXPOrb";
        }

        //Projectile
        if(entity instanceof EntityArrow){
            return "EntityArrow";
        }
        if(entity instanceof EntityEgg){
            return "EntityEgg";
        }
        if(entity instanceof EntityEnderPearl){
            return "EntityEndCrystal";
        }
        if(entity instanceof EntitySnowball){
            return "EntitySnowball";
        }
        if(entity instanceof EntityThrownTrident){
            return "EntityThrownTrident";
        }

        if(entity instanceof EntityLightning){
            return "EntityLightning";
        }
        return entity.getName();
    }
}
