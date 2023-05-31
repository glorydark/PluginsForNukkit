package glorydark.simplenightvision;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.List;

public class MainClass extends PluginBase {

    public List<String> allowLevels;
    
    public boolean onlyNight;

    @Override
    public void onLoad() {
        this.getLogger().info("SimpleNightVision onLoad!");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config config = new Config(this.getDataFolder()+"/config.yml", Config.YAML);
        allowLevels = new ArrayList<>(config.getStringList("AllowLevels"));
        if(!config.exists("OnlyNight")){
            config.set("OnlyNight", true);
            config.save();
        }
        onlyNight = config.getBoolean("OnlyNight", true);
        this.getLogger().info("SimpleNightVision onEnabled!");
        Server.getInstance().getScheduler().scheduleRepeatingTask(this, ()->{
            for(Player player: Server.getInstance().getOnlinePlayers().values()){
                if(allowLevels.contains(player.getLevel().getName())){
                    if(isDay(player.getLevel()) && onlyNight){
                        if(player.hasEffect(Effect.NIGHT_VISION)){
                            player.removeEffect(Effect.NIGHT_VISION);
                        }
                        continue;
                    }
                    Effect effect = Effect.getEffect(Effect.NIGHT_VISION);
                    effect.setAmplifier(255);
                    effect.setAmbient(true);
                    effect.setVisible(false);
                    effect.setDuration(1200);
                    player.addEffect(effect);
                }
            }
        }, 20);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("SimpleNightVision onDisable!");
    }

    public boolean isDay(Level level){
        int timeOfDay = level.getTime()%24000;
        return timeOfDay < 12000;
    }
}
