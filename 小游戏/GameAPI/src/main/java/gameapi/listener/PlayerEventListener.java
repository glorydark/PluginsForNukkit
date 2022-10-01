package gameapi.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.entity.EntityUtils;
import gameapi.entity.TextEntity;
import gameapi.event.base.RoomPlayerDeathEvent;
import gameapi.inventory.Inventory;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.utils.AdvancedLocation;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Glorydark
 */
public class PlayerEventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void Join(PlayerLocallyInitializedEvent event){
        GameAPI.playerRoomHashMap.put(event.getPlayer(), null);
        if(event.getPlayer() != null) {
            if (Inventory.getPlayerBagConfig(event.getPlayer()) != null) {
                Inventory.loadBag(event.getPlayer());
                event.getPlayer().getFoodData().setLevel(20, 20.0F);
                Server.getInstance().getLogger().info("检测到玩家"+event.getPlayer().getName()+"背包上次未能返还，已经返还！");
            }

            for (TextEntity entity : EntityUtils.entityList) {
                entity.spawnTo(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void Exit(PlayerQuitEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            for (Player p : room.getPlayers()) {
                p.sendMessage("玩家" + event.getPlayer().getName() + "退出了本房间");
            }
            room.removePlayer(event.getPlayer(),true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockBreakEvent(BlockBreakEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                event.setCancelled(true);
            }
            if(!room.getRoomRule().allowBreakBlock) {
                if (!room.getRoomRule().canBreakBlocks.contains(event.getBlock().getId()+":"+event.getBlock().getDamage())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockPlaceEvent(BlockPlaceEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                event.setCancelled(true);
            }
            if(!room.getRoomRule().allowPlaceBlock) {
                if (!room.getRoomRule().canPlaceBlocks.contains(event.getBlock().getId()+":"+event.getBlock().getDamage())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null){
            if (room.getRoomRule().noDropItem) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ExplodeEvent(EntityExplodeEvent event){
        List<Room> roomList = new ArrayList<>();
        GameAPI.RoomHashMap.forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room: roomList) {
            if(room != null){
                for(AdvancedLocation location: room.getStartSpawn()){
                    if(location.getLevel().getName().equals(event.getPosition().level.getName())){
                        return;
                    }
                }
                if(room.getRoomRule().antiExplosion){
                    event.getEntity().kill();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ExplodePrimeEvent(ExplosionPrimeEvent event){
        List<Room> roomList = new ArrayList<>();
        GameAPI.RoomHashMap.forEach((s, rooms) -> roomList.addAll(rooms));
        for (Room room: roomList) {
            if(room != null){
                for(AdvancedLocation location: room.getStartSpawn()){
                    if(location.getLevel().getName().equals(event.getEntity().level.getName())){
                        return;
                    }
                }
                if(room.getRoomRule().antiExplosion){
                    event.getEntity().kill();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerMoveEvent(PlayerMoveEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room != null) {
            if(room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameReadyStart && room.getRoomRule().noStartWalk){
                Location from = event.getFrom();
                Location to = event.getTo();
                if(from.getFloorX() != to.getFloorX() || from.getFloorZ() != to.getFloorZ()){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerDamageEvent(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Player) {
            Room room1 = Room.getRoom((Player) entity);
            if(room1 == null){ return; }
            if(entity.getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                if(room1.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
                    RoomPlayerDeathEvent ev = new RoomPlayerDeathEvent(room1, (Player) entity, event.getCause());
                    //Server.getInstance().getPluginManager().callEvent(ev);
                    GameAPI.registry.callEvent(room1.getGameName(), ev);
                    if (!ev.isCancelled()) {
                        entity.setHealth(entity.getMaxHealth());
                        room1.setSpectator((Player) entity, room1.getRoomRule().allowSpectatorMode, true);
                        room1.setPlayerProperties(entity.getName(), "last_damage_source", new DamageSource("", 0L));
                    }
                }else{
                    entity.setHealth(entity.getMaxHealth());
                    room1.setSpectator((Player) entity, true, false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if(entity instanceof Player && damager instanceof Player) {
            Room room1 = Room.getRoom((Player) entity);
            Room room2 = Room.getRoom((Player) damager);
            if(room1 != room2){ return; }
            if(room1 == null){ return; }
            if(!room1.getRoomRule().allowDamagePlayer || room1.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                event.setCancelled(true);
                return;
            }
            if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
                Player p1 = (Player) event.getEntity();
                Player p2 = (Player) event.getDamager();
                Room r1 = Room.getRoom(p1);
                Room r2 = Room.getRoom(p2);
                if(r1 != null && r2 != null){
                    if(r1.getTeams().size() > 0){
                        if(r1.getPlayerTeam(p1) != null && r1.getPlayerTeam(p1) == r1.getPlayerTeam(p2)){
                            p1.sendMessage("§c你不能攻击你的队友");
                            event.setCancelled(true);
                        }else{
                            r1.setPlayerProperties(p1.getName(), "last_damage_source", new DamageSource(p2.getName(), System.currentTimeMillis()));
                        }
                    }else{
                        r1.setPlayerProperties(p1.getName(), "last_damage_source", new DamageSource(p2.getName(), System.currentTimeMillis()));
                    }
                }
            }
        }
    }

    @Data
    public static class DamageSource{
        private String damager;
        private long milliseconds;

        public DamageSource(String damager, long milliseconds){
            this.damager = damager;
            this.milliseconds = milliseconds;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if(player != null){
            if(player.isOp()){ return; }
            Room room = Room.getRoom(player);
            if(room != null){
                player.sendMessage(TextFormat.RED + "游戏内玩家不可执行指令！");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTp(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Level fromLevel = event.getFrom().getLevel();
        Level toLevel = event.getTo().getLevel();
        if (player == null || fromLevel == null || toLevel == null) {
            return;
        }
        Room room = Room.getRoom(player);
        if(room != null) {
            if (!fromLevel.equals(toLevel)) {
                List<Level> arenas = new LinkedList<>();
                arenas.add(room.getWaitSpawn().getLevel());
                room.getStartSpawn().forEach(spawn -> arenas.add(spawn.getLevel()));
                if (!arenas.contains(fromLevel) && !arenas.contains(toLevel)) {
                    event.setCancelled(true);
                    player.sendMessage("§c请使用命令加入/退出游戏房间！");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) //暂时合成设置不可使用
    public void onCraft(CraftItemEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && Room.getRoom(event.getPlayer()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && Room.getRoom(event.getPlayer()) != null) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void FlyEvent(PlayerToggleFlightEvent event){
        if(Room.getRoom(event.getPlayer()) == null){ return; }
        if(!event.getPlayer().isOp()){
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void PlayerInvalidMoveEvent(PlayerInvalidMoveEvent event) {
        if (Room.getRoom(event.getPlayer()) == null) {
            return;
        }
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void EntityLevelChangeEvent(EntityLevelChangeEvent event){
        if(event.getEntity() instanceof Player){
            for (TextEntity entity : EntityUtils.entityList) {
                entity.spawnTo((Player) event.getEntity());
            }
        }
    }
}
