package glorydark.flysystem;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.List;

public class MainClass extends PluginBase implements Listener {

    public List<String> banWorlds;
    public static String path;

    @Override
    public void onLoad() {
        this.getLogger().info("FlySystem onLoad");
    }

    @Override
    public void onEnable() {
        path = this.getDataFolder().getPath();
        saveDefaultConfig();
        banWorlds = new ArrayList<>(new Config(path+"/config.yml", Config.YAML).getStringList("banWorlds")); //get banned worlds
        this.getServer().getScheduler().scheduleRepeatingTask(this, ()->{
            for(Player player: Server.getInstance().getOnlinePlayers().values()){
                if(player.isOp()){ return; }
                Config config = new Config(path+"/config.yml", Config.YAML);
                double duration = config.getDouble("playerRecords." + player.getName(), -1d);
                if(duration == -1d){
                    if(isAllowFlight(player)) {
                        setFlying(player, false);
                        player.sendMessage("您的飞行状态错误，系统已自动更改！");
                    }
                    return;
                }
                if(duration < System.currentTimeMillis()){
                    if(isAllowFlight(player)) {
                        setFlying(player, false);
                    }
                    config.remove("playerRecords." + player.getName());
                    config.save();
                    player.sendMessage("您的飞行已过期！");
                    return;
                }
                if(!banWorlds.contains(player.getLevel().getName())){
                    setFlying(player, true);
                }else{
                    if(isAllowFlight(player)) {
                        player.sendMessage("该世界不允许飞行，系统关闭了你的飞行！");
                        setFlying(player, false);
                    }
                }
            }
        },20);
        this.getServer().getCommandMap().register("", new FlyCommand("fly"));
        this.getLogger().info("FlySystem onEnable");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("FlySystem onDisable");
    }

    public void setFlying(Player player, boolean bool){
        AdventureSettings settings = player.getAdventureSettings();
        settings.set(AdventureSettings.Type.FLYING, bool);
        settings.set(AdventureSettings.Type.ALLOW_FLIGHT, bool);
        player.setAdventureSettings(settings);
        player.getAdventureSettings().update();
    }

    public boolean isAllowFlight(Player player){
        return player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT);
    }
}
