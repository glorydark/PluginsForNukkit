package glorydark.CloudInventory;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;

import java.util.ArrayList;

public class Event implements Listener {

    @EventHandler
    public void Join(PlayerJoinEvent event){
        Config config = new Config(MainClass.plugin.getDataFolder()+"/players/"+event.getPlayer().getName()+".yml");
        if(!config.exists("Inventory")){
            config.set("Inventory",new ArrayList<String>());
            config.save();
        }
    }
}
