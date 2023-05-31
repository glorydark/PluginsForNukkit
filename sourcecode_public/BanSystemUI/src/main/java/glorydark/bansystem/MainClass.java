package glorydark.bansystem;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.bansystem.gui.GuiListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainClass extends PluginBase {
    public static String path;
    public static MainClass plugin;

    @Override
    public void onLoad() {
        this.getLogger().info("BanSystemUI onLoaded");
    }

    @Override
    public void onEnable() {
        path = this.getDataFolder().getPath();
        plugin = this;
        this.saveResource("config.yml",false);
        this.saveResource("dealMethod.yml",false);
        /*
        this.saveResource("managerLevel.yml",false);
        this.saveResource("playerLevel.yml",false);
         */
        Config config = new Config(path+"/config.yml",Config.YAML);
        if(config.exists("数据库")) {
            Map<String, Object> map = (Map<String, Object>) config.get("数据库");
            if (map.get("是否启用").equals(true)) {
                if (Server.getInstance().getPluginManager().getPlugin("EasyMySQL") != null) {
                    this.getLogger().error("[前置已安装] 您已安装Dblib插件！");
                } else {
                    this.getLogger().error("[前置缺失] 您尚未安装EasyMySQL(作者:ruo_shui)，请下载前置！");
                }
            }
        }else{
            Map<String, Object> map = new HashMap<>();
            map.put("ip","192.168.1.1");
            map.put("是否启用",false);
            map.put("端口",19132);
            map.put("名称","test");
            map.put("用户名","test");
            map.put("密码","test");
            config.set("数据库", map);
            config.save();
        }
        ScheduledThreadPoolExecutor ste = new ScheduledThreadPoolExecutor(4);
        ste.scheduleAtFixedRate(new BindTask(),1,50, TimeUnit.MILLISECONDS);
        this.getServer().getPluginManager().registerEvents(new GuiListener(),this);
        this.getServer().getPluginManager().registerEvents(new EventListener(),this);
        this.getServer().getCommandMap().register("",new showGuiCommand());
        this.getServer().getCommandMap().register("",new quitFollowCommand());
        this.getLogger().info("BanSystemUI onEnabled");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("BanSystem onDisabled");
    }
}