package glorydark.dodgebolt.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import gameapi.event.room.RoomGameStartEvent;
import gameapi.event.room.RoomReadyStartEvent;
import gameapi.listener.RoomReadyStartListener;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.scoreboard.ScoreboardAPI;
import glorydark.dodgebolt.MainClass;
import glorydark.dodgebolt.tasks.ProjectileParticleTask;
import glorydark.dodgebolt.utils.ExtendRoomData;

import java.util.*;

public class GameListener implements Listener {

    @EventHandler
    public void touch(PlayerInteractEvent event){
        Room room = Room.getRoom("DodgeBolt", event.getPlayer());
        Player player = event.getPlayer();
        Item item = event.getItem();
        if(room == null){ return; }
        if(item instanceof ItemBookEnchanted){
            if(item.getCustomName().equals("§l§c退出房间")) {
                room.removePlayer(player, true);
                player.sendMessage("§l§c您已退出房间！");
            }
        }
    }

    @EventHandler
    public void RoomReadyStartEvent(RoomReadyStartEvent event){
        if(event.getRoom().getGameName().equals("DodgeBolt")) {
            LinkedList<Player> team1 = new LinkedList<>();
            LinkedList<Player> team2 = new LinkedList<>();
            List<Player> players = event.getRoom().getPlayers();
            players.forEach(player -> {
                if (team1.size() >= team2.size()) {
                    team2.add(player);
                    event.getRoom().setPlayerProperties(player.getName(), "spawnIndex", 1);
                    player.sendMessage("您加入了§e黄队");
                } else {
                    team1.add(player);
                    event.getRoom().setPlayerProperties(player.getName(), "spawnIndex", 0);
                    player.sendMessage("您加入了§b蓝队");
                }
                event.getRoom().setPlayerProperties(player.getName(), "isAlive", true);
            });
            ExtendRoomData data = MainClass.roomList.get(event.getRoom());
            data.setTeam1(team1);
            data.setTeam2(team2);
            data.setTeam1_score(0);
            data.setTeam2_score(0);
            MainClass.roomList.put(event.getRoom(), data);
        }
    }

    @EventHandler
    public void RoomReadyListener(RoomReadyStartListener event){
        if(event.getRoom().getGameName().equals("DodgeBolt")) {
            ExtendRoomData data = MainClass.roomList.get(event.getRoom());
            updateScoreBoard(event.getRoom(), data.getTeam1_score(), data.getTeam2_score(), data.getMax_score());
        }
    }

    @EventHandler
    public void RoomGameStartEvent(RoomGameStartEvent event){
        if(event.getRoom().getGameName().equals("DodgeBolt")) {
            Item crossbow = Item.get(Item.BOW, 0, 1);
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("Unbreakable", true);
            tag.putByte("minecraft:item_lock", 1);
            crossbow.setCompoundTag(tag);
            ExtendRoomData data = MainClass.roomList.get(event.getRoom());
            for (Player player : event.getRoom().getPlayers()) {
                player.getInventory().clearAll();
                player.getInventory().addItem(crossbow);
                data.teleportToSpawn(player);
            }
            data.spawnArrow(1);
            data.spawnArrow(2);
            updateScoreBoard(event.getRoom(), data.getTeam1_score(), data.getTeam2_score(), data.getMax_score());
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) { return; }
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        if (damager == victim) {
            return;
        }
        Room room = Room.getRoom("DodgeBolt", damager);
        Room room1 = Room.getRoom("DodgeBolt", victim);
        if (room != room1) {
            return;
        }
        if (room == null) {
            return;
        }
        event.setDamage(0);
        event.setKnockBack(0f);
        ExtendRoomData data = MainClass.roomList.get(room);
        if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                if ((Boolean) room.getPlayerProperties(victim.getName(), "isAlive")) {
                    Integer damagerTeam = data.getPlayerTeamIndex(damager);
                    Integer victimTeam = data.getPlayerTeamIndex(victim);
                    if (!Objects.equals(damagerTeam, victimTeam)) {
                        this.spawnFirework(victim.getPosition(), DyeColor.YELLOW, ItemFirework.FireworkExplosion.ExplosionType.BURST);
                        room.setSpectator(victim, false, true);
                        List<String> killMsg = new ArrayList<>(MainClass.lang.getStringList("kill_messages"));
                        Random random = new Random(System.currentTimeMillis());
                        int index = random.nextInt(killMsg.size());
                        room.getPlayers().forEach(player -> player.sendMessage(killMsg.get(index).replace("<damager>", data.getPlayerTeamColorPrefix(damager) +"[ "+ damager.getName()+" ]§f").replace("<victim>", data.getPlayerTeamColorPrefix(victim) +"[ "+ victim.getName()+" ]§f")));
                        victim.getInventory().clearAll();
                        room.setPlayerProperties(victim.getName(), "isAlive", false);
                        data.spawnArrow(damagerTeam);
                        for(Player player: MainClass.roomList.get(room).getTeamMembers(victimTeam)){
                            if((Boolean) room.getPlayerProperties(player.getName(), "isAlive")){
                                return;
                            }
                        }
                        for(Entity entity: data.getTeam1_spawn().getLevel().getEntities()){
                            if(entity instanceof EntityArrow){
                                entity.kill();
                            }
                        }
                        for(Player player: MainClass.roomList.get(room).getTeamMembers(damagerTeam)){
                            room.setSpectator(player, false, false);
                            player.sendMessage(MainClass.lang.getString("win_round_messages").replace("<team>", data.getPlayerTeamName(damagerTeam)));
                        }
                        for(Player player: MainClass.roomList.get(room).getTeamMembers(victimTeam)){
                            player.sendMessage(MainClass.lang.getString("lose_round_messages").replace("<team>", data.getPlayerTeamName(victimTeam)));
                        }
                        data.addTeamScore(damagerTeam);
                        MainClass.roomList.put(room, data);
                        updateScoreBoard(room, data.getTeam1_score(), data.getTeam2_score(), data.getMax_score());
                        for(Player player: room.getPlayers()){
                            room.setPlayerProperties(player.getName(), "isAlive", true);
                        }
                        if(Objects.equals(data.getTeamScore(damagerTeam), data.getMax_score())){
                            for(Player player: MainClass.roomList.get(room).getTeamMembers(damagerTeam)){
                                player.sendMessage(MainClass.lang.getString("win_match_messages").replace("<team>", data.getPlayerTeamName(damagerTeam)));
                                player.sendTitle("§eWin!", "§eYour team is the winner!");
                                room.executeWinCommands(player);
                            }
                            for(Player player: MainClass.roomList.get(room).getTeamMembers(victimTeam)){
                                player.sendMessage(MainClass.lang.getString("lose_match_messages").replace("<team>", data.getPlayerTeamName(victimTeam)));
                                player.sendTitle("§7Lose!", "§7Your team failed!");
                                room.executeLoseCommands(player);
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd);
                            room.setTime(0);
                            data.reset();
                            MainClass.roomList.put(room, data);
                            return;
                        }
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameReadyStart);
                        room.setTime(0);
                    } else {
                        damager.sendMessage("您不能攻击您的队友！");
                    }
                }
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void Move(PlayerMoveEvent event){
        Room room = Room.getRoom("DodgeBolt", event.getPlayer());
        if(room != null && (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart || room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameReadyStart)){
            Player player = event.getPlayer();
            ExtendRoomData data = MainClass.roomList.get(room);
            if(data.getWallPos().checkMovement(player.getLocation())){
                event.setCancelled();
                event.getPlayer().teleport(event.getFrom(), null);
            }
        }
    }

    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event){
        if(event.getEntity() instanceof EntityArrow) {
            Entity shooter = event.getEntity().shootingEntity;
            if(shooter instanceof Player) {
                Room room = Room.getRoom("DodgeBolt", (Player) shooter);
                if(room != null) {
                    Server.getInstance().getScheduler().scheduleRepeatingTask(new ProjectileParticleTask((EntityArrow) event.getEntity()), 1);
                }
            }
        }
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event){
        if(event.getEntity() instanceof EntityArrow) {
            Entity shooter = ((EntityArrow) event.getEntity()).shootingEntity;
            if(shooter instanceof Player) {
                Room room = Room.getRoom("DodgeBolt", (Player) shooter);
                if(room != null) {
                    event.getEntity().kill();
                    ExtendRoomData data = MainClass.roomList.get(room);
                    data.spawnArrow(data.getPlayerTeamIndex((Player) shooter));
                }
            }
        }
    }

    public void spawnFirework(Position position, DyeColor color, ItemFirework.FireworkExplosion.ExplosionType type) {
        Level level = position.getLevel();
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        Random random = new Random();
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByteArray("FireworkFade", new byte[0]);
        compoundTag.putBoolean("FireworkFlicker", random.nextBoolean());
        compoundTag.putBoolean("FireworkTrail", random.nextBoolean());
        tag.putCompound("Fireworks", (new CompoundTag("Fireworks")).putList((new ListTag("Explosions")).add(compoundTag)).putByte("Flight", 1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList((new ListTag("Pos")).add(new DoubleTag("", position.x + 1.0)).add(new DoubleTag("", position.y + 1.0)).add(new DoubleTag("", position.z + 1.0)));
        nbt.putList((new ListTag("Motion")).add(new DoubleTag("", 0.0)).add(new DoubleTag("", 0.0)).add(new DoubleTag("", 0.0)));
        nbt.putList((new ListTag("Rotation")).add(new FloatTag("", 0.0F)).add(new FloatTag("", 0.0F)));
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        compoundTag.putByteArray("FireworkColor", new byte[]{(byte)color.getDyeData()});
        compoundTag.putByte("FireworkType", type.ordinal());
        EntityFirework entity = new EntityFirework(level.getChunk((int)position.x >> 4, (int)position.z >> 4), nbt);
        entity.spawnToAll();
        EntityEventPacket pk = new EntityEventPacket();
        pk.data = 0;
        pk.event = 25;
        pk.eid = entity.getId();
        entity.level.addLevelSoundEvent(entity.getLocation(), 58, -1, 72);
        Server.broadcastPacket(entity.getViewers().values(), pk);
        entity.kill();
    }

    public void updateScoreBoard(Room room, Integer blueScore, Integer yellowScore, Integer maxRound){
        for(Player player: room.getPlayers()){
            Scoreboard scoreboard = new Scoreboard();
            ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay( DisplaySlot.SIDEBAR, "scoreboard.dodgebolt_scoreboard_entry_final", "DodgeBolt" );
            if(room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameReadyStart){
                if(blueScore == 0 && yellowScore == 0){
                    scoreboardDisplay.addLine("§c§lGame begins: §f"+ScoreboardAPI.secToTime(room.getGameWaitTime() - room.getTime() + 1), 0);
                }else{
                    scoreboardDisplay.addLine("§c§lNext round: §f"+ScoreboardAPI.secToTime(room.getGameWaitTime() - room.getTime() + 1), 0);
                }
            }
            if(room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart){
                scoreboardDisplay.addLine("§c§lIn Game", 0);
            }
            StringBuilder blue = new StringBuilder("§b§lBlue: §f");
            StringBuilder yellow = new StringBuilder("§e§lYellow: §f");
            for(int i = 0; i<maxRound; i++){
                if(i<blueScore){
                    blue.append("§6●");
                }else{
                    blue.append("§f○");
                }
            }
            for(int i = 0; i<maxRound; i++){
                if(i<yellowScore){
                    yellow.append("§6●");
                }else{
                    yellow.append("§f○");
                }
            }
            scoreboardDisplay.addLine(blue.toString(), 1);
            scoreboardDisplay.addLine(yellow.toString(), 2);
            ScoreboardAPI.drawScoreBoardEntry(player, scoreboard);
        }
    }
}
