package glorydark.fireworkshop;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import glorydark.fireworkshop.command.acceptcommand;
import glorydark.fireworkshop.command.command;
import glorydark.fireworkshop.event.EventListener;
import glorydark.fireworkshop.task.ScheduleTask;
import glorydark.fireworkshop.utils.FireworkData;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MainClass extends PluginBase implements Listener {
    public static boolean bool = false;
    public static String path = null;
    public static HashMap<Player,HashMap<Integer, List<FireworkData>>> playerfireworkcache = new HashMap<>();
    public static HashMap<Player,Integer> MaxTickRecord = new HashMap<>();
    public static Position arrivepos = null;

    @Override
    public void onLoad() {
        this.getLogger().info("加载 FireworkShop 中");
        File file = new File(this.getDataFolder().getPath()+"/Template/");
        file.mkdirs();
        this.saveResource("shops.yml",false);
        this.saveResource("Template/test.yml",false);
    }

    @Override
    public void onEnable() {
        this.getLogger().info("欢迎使用 FireworkShop 插件");
        this.getServer().getPluginManager().registerEvents(this, this); // 注册Event
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getCommandMap().register("FireworkShop", new command("fs"));
        this.getServer().getCommandMap().register("FireworkShop", new acceptcommand("fsaccept"));
        Server server = this.getServer();
        this.getServer().getScheduler().scheduleRepeatingTask(new ScheduleTask(),2);
        path = this.getDataFolder().getPath();
        if(this.getServer().getPluginManager().getPlugins().containsKey("net.player.PlayerPoint")){
            bool = true;
        }else{
            bool = false;
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("关闭 FireworkShop 中");
    }
}
