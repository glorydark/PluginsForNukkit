package glorydark.playercountsync;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.List;

// @original author: SmallAsWater
// Glorydark added small changes
public class MainClass extends PluginBase implements Listener {
    public static List<String> servers = new ArrayList<>();

    public static int all = 0;

    public static int fakebase = 0;

    @EventHandler
    public void QueryRegenerateEvent(QueryRegenerateEvent event){
        UpdateServerInfoRunnable.run();
        event.setPlayerCount(fakebase+all);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Config config = new Config(this.getDataFolder().getPath()+"/config.yml", Config.YAML);
        servers = new ArrayList<>(config.getStringList("list"));
        fakebase = config.getInt("fake_base", 0);
        this.getServer().getPluginManager().registerEvents(this, this);
    }
}
