package glorydark.DTipSystem;

import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import glorydark.DTipSystem.command.command;
import glorydark.DTipSystem.event.event;

public class MainClass extends PluginBase implements Listener {
    private static MainClass plugin; // 插件实例
    public static String path = null;
    @Override
    public void onLoad(){
        this.getLogger().info("DTitle加载中");
    }
    @Override
    public void onEnable(){
        plugin = this;
        this.getLogger().info("DTitle加载成功");
        this.getServer().getPluginManager().registerEvents(new event(),this);
        this.getServer().getCommandMap().register("DTitle",new command("dts"));
        this.saveResource("config.yml",false);
        this.saveResource("world.yml",false);
        path = this.getDataFolder().getPath();
    }

    public static Plugin getPlugin(){
        return plugin;
    }
}
