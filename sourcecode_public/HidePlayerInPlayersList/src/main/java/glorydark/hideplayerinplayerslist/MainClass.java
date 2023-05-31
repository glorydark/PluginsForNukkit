package glorydark.hideplayerinplayerslist;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.entity.EntityPortalEnterEvent;
import cn.nukkit.event.entity.EntityTeleportEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLoader;
import cn.nukkit.utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MainClass extends PluginBase implements Listener {

    public Boolean isHideAllPlayers;
    public Boolean isShowOneWorldPlayers;

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        Config config = new Config(this.getDataFolder()+"/config.yml", Config.YAML);
        this.isShowOneWorldPlayers = config.getBoolean("显示同世界玩家", false);
        this.isHideAllPlayers = config.getBoolean("仅显示自己", false);
    }

    public void removeFullPlayerListData(Player player) {
        Collection<Player> online = Server.getInstance().getOnlinePlayers().values();
        PlayerListPacket pk = new PlayerListPacket();
        List<Player> players;
        if(online.size() > 0) {
            players = new ArrayList<>(Arrays.asList(online.toArray(new Player[online.size() - 1])));
        }else{
            players = new ArrayList<>(Arrays.asList(online.toArray(new Player[0])));
        }
        players.remove(player);
        pk.type = 1;
        pk.entries = online.stream().map((p) -> {
            return new PlayerListPacket.Entry(p.getUniqueId(), p.getId(), p.getDisplayName(), p.getSkin(), p.getLoginChainData().getXUID());
        }).toArray((x$0) -> {
            return new PlayerListPacket.Entry[x$0];
        });
        player.dataPacket(pk);
    }

    public void sendFullLevelPlayerListData(Player player, Level level) {
        Collection<Player> online = level.getPlayers().values();
        PlayerListPacket pk = new PlayerListPacket();
        List<Player> players;
        if(online.size() > 0) {
            players = new ArrayList<>(Arrays.asList(online.toArray(new Player[online.size() - 1])));
        }else{
            players = new ArrayList<>(Arrays.asList(online.toArray(new Player[0])));
        }
        players.add(player);
        pk.type = 0;
        pk.entries = players.stream().map((p) -> {
            return new PlayerListPacket.Entry(p.getUniqueId(), p.getId(), p.getDisplayName(), p.getSkin(), p.getLoginChainData().getXUID());
        }).toArray((x$0) -> {
            return new PlayerListPacket.Entry[x$0];
        });
        player.dataPacket(pk);
    }

    @EventHandler
    public void join(PlayerLocallyInitializedEvent event) throws IOException {
        if(isHideAllPlayers) {
            Collection<Player> playerList = Server.getInstance().getOnlinePlayers().values();
            for (Player player : playerList) {
                List<Player> removePlayers = new ArrayList<>(Arrays.asList(playerList.toArray(new Player[playerList.size() - 1])));
                removePlayers.remove(player);
                sendRemoveData(player, removePlayers);
            }
        }else{
            if(isShowOneWorldPlayers){
                Player player = event.getPlayer();
                removeFullPlayerListData(player);
                sendFullLevelPlayerListData(player, event.getPlayer().getLevel());
                for(Player nowPlayer: event.getPlayer().getLevel().getPlayers().values()){
                    sendUpdateData(nowPlayer, player);
                }
            }
        }
        Skin skin = event.getPlayer().getSkin();
        File jsonFile = new File(this.getDataFolder().getPath()+"/"+skin.getFullSkinId()+".json");
        FileWriter jsonWriter = new FileWriter(jsonFile);
        BufferedWriter out = new BufferedWriter(jsonWriter);
        out.write(skin.getGeometryData());
        out.close();
        jsonWriter.close();
    }

    @EventHandler
    public void quit(PlayerQuitEvent event){
        if(isShowOneWorldPlayers){
            for(Player player: event.getPlayer().getLevel().getPlayers().values()){
                sendRemoveData(player, event.getPlayer());
            }
        }
    }

    public void sendRemoveData(Player player, List<Player> hiders){
        for(Player hider: hiders) {
            PlayerListPacket pk = new PlayerListPacket();
            pk.type = 1;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(hider.getUniqueId())};
            if (player.getUniqueId() != hider.getUniqueId()) {
                player.dataPacket(pk);
            }
        }
    }

    public void sendRemoveData(Player player, Player hider){
        if (player.getUniqueId() != hider.getUniqueId()) {
            PlayerListPacket pk = new PlayerListPacket();
            pk.type = 1;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(hider.getUniqueId())};
            player.dataPacket(pk);
        }
    }

    public void sendUpdateData(Player player, Player add){
        if (player.getUniqueId() != add.getUniqueId()) {
            PlayerListPacket pk = new PlayerListPacket();
            pk.type = 0;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(add.getUniqueId(), add.getId(), add.getDisplayName(), add.getSkin(), add.getName())};
            player.dataPacket(pk);
        }
    }

    @EventHandler
    public void LevelChangeEvent(EntityLevelChangeEvent event){
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player) event.getEntity();
        removeFullPlayerListData(player);
        sendFullLevelPlayerListData(player, event.getTarget());

        for(Player originPlayer: event.getOrigin().getPlayers().values()){
            sendRemoveData(originPlayer, player);
        }

        for(Player nowPlayer: event.getTarget().getPlayers().values()){
            sendUpdateData(nowPlayer, player);
        }
    }

    @EventHandler
    public void EntityTeleportEvent(EntityTeleportEvent event){
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        if(!event.getFrom().getLevel().equals(event.getTo().getLevel())) {
            Player player = (Player) event.getEntity();
            removeFullPlayerListData(player);
            sendFullLevelPlayerListData(player, event.getTo().getLevel());

            for (Player originPlayer : event.getFrom().getLevel().getPlayers().values()) {
                sendRemoveData(originPlayer, player);
            }

            for (Player nowPlayer : event.getTo().getLevel().getPlayers().values()) {
                sendUpdateData(nowPlayer, player);
            }
        }
    }

    @EventHandler
    public void Teleport(EntityPortalEnterEvent event){
        if(!Server.getInstance().isNetherAllowed()){ return; }
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player) event.getEntity();
        removeFullPlayerListData(player);
        Level target = null;
        switch (event.getPortalType()) {
            case END:
                if(Server.getInstance().isLevelLoaded("end")){
                    sendFullLevelPlayerListData(player, Server.getInstance().getLevelByName("end"));
                    target = Server.getInstance().getLevelByName("end");
                }
                break;
            case NETHER:
                sendFullLevelPlayerListData(player, Server.getInstance().getLevelByName("nether"));
                target = Server.getInstance().getLevelByName("nether");
                break;
        }
        if(target == null){ return; }
        for(Player originPlayer: player.getLevel().getPlayers().values()){
            sendRemoveData(originPlayer, player);
        }

        for(Player nowPlayer: target.getPlayers().values()){
            sendUpdateData(nowPlayer, player);
        }
    }
}
