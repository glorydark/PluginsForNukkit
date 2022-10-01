package glorydark.bansystem;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.utils.Config;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EventListener implements Listener {

    @EventHandler
    public void Join(PlayerJoinEvent event){
        if(event.getPlayer() == null){return;}
        Config config = new Config(MainClass.path+"/players/"+event.getPlayer().getName()+".json", Config.JSON);
        if(config.exists("pardonDate")) {
            if (API.getCalendar().getTimeInMillis() >= API.parseDateString(config.getString("pardonDate")).getTimeInMillis()) {
                config.remove("pardonDate");
                config.save();
            } else {
                event.getPlayer().kick("\n [BanSystem] 解封时间: " + config.getString("pardonDate"));
            }
        }
        //封禁
        List<String> list;
        if(API.getPlayerConfig(event.getPlayer().getName(),"ip") != null) {
            list = new ArrayList<>((List<String>) API.getPlayerConfig(event.getPlayer().getName(), "ip"));
        }else{
            list = new ArrayList<>();
        }
        if(!list.contains(event.getPlayer().getAddress())) {
            list.add(event.getPlayer().getAddress());
            API.setPlayerConfig(event.getPlayer().getName(), "ip", list);
        }
    }
}
