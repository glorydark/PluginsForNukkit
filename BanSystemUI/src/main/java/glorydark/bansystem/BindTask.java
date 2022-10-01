package glorydark.bansystem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.MovePlayerPacket;

import java.util.concurrent.ConcurrentHashMap;

public class BindTask implements Runnable {

    public static ConcurrentHashMap<Player, Player> bindPlayers = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for(Player bind: bindPlayers.values()){
            for (Player player : bindPlayers.keySet()) { //bind为被捆绑者
                if (bindPlayers.get(player).equals(bind)) {
                    if (bind.isOnline() && player.isOnline()) {
                        player.teleportImmediate(new Location(bind.getX(),bind.getY(),bind.getZ(),bind.getYaw(),bind.getPitch(),bind.getHeadYaw(), bind.getLevel()));
                        player.sendTip("您正在跟随:" + bind.getName());
                    }else{
                        bindPlayers.remove(player);
                    }
                }
            }
        }
    }
}
