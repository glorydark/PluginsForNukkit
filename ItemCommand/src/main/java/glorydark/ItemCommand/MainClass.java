package glorydark.ItemCommand;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.List;

public class MainClass extends PluginBase implements Listener {
    public static Plugin plugin;

    public void onLoad() {
        this.getLogger().info("ItemCommand 正在加载中");
    }

    public void onEnable(){
        this.getLogger().info("ItemCommand 成功加载");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.saveResource("item.yml");
        plugin = this;
    }

    public void onDisable(){
        this.getLogger().info("ItemCommand 已卸载");
    }

    @EventHandler
    public void onTouch(PlayerInteractEvent event){
        if(event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
            if(!event.getPlayer().isPlayer()){ return; }
            List<String> commands = this.getCommandByItem(event.getItem());
            if(commands == null){return;}
            for(String s: commands){
                s = s.replace("%player%",event.getPlayer().getName());
                Server.getInstance().dispatchCommand(event.getPlayer(),s);
            }
        }
    }

    public List<String> getCommandByItem(Item item){
        String name = item.getId()+":"+item.getDamage();
        Config config = new Config(plugin.getDataFolder()+ "/item.yml");
        if(config.exists(name)){
            List<String> stringList = new ArrayList<>(config.getStringList(name));
            return stringList;
        }
        return null;
    }
}
