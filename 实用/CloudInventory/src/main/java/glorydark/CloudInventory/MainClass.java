package glorydark.CloudInventory;

import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.CloudInventory.gui.GuiListener;

import java.io.File;

public class MainClass extends PluginBase implements Listener {
    public static Plugin plugin;
    public static Integer defaultMaxSlot;

    public void onLoad() {
        this.getLogger().info("CloudInventory 正在加载中");
    }

    public void onEnable(){
        this.getLogger().info("CloudInventory 成功加载");
        this.getLogger().info("作者: Glorydark");
        this.getLogger().info("本插件引用了若水的NBT物品保存代码");
        this.saveDefaultConfig();
        defaultMaxSlot = new Config(this.getDataFolder()+"/config.yml", Config.YAML).getInt("默认云背包大小", 20);
        this.checkPlayerSlot();
        this.saveResource("lang.yml",false);
        this.getServer().getPluginManager().registerEvents(new GuiListener(), this);
        this.getServer().getPluginManager().registerEvents(new Event(),this);
        this.getServer().getCommandMap().register("",new InventoryCommand("yun"));
        plugin = this;
    }

    public void onDisable(){
        this.getLogger().info("CloudInventory 已卸载");
    }

    public void checkPlayerSlot(){
        File file = new File(this.getDataFolder().getPath() + "/players/");
        if(file.isDirectory()){
            for(File file1 : file.listFiles()){
                Config config = new Config(file1.getPath(), Config.YAML);
                if(config.exists("InventorySlots")){
                    Integer slot = config.getInt("InventorySlots");
                    if(slot < defaultMaxSlot){
                        config.set("InventorySlots", defaultMaxSlot);
                        config.save();
                    }
                }else{
                    config.set("InventorySlots", defaultMaxSlot);
                    config.save();
                }
            }
        }
    }
}
