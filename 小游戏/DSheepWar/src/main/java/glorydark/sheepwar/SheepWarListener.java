package glorydark.sheepwar;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlazeRod;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import gameapi.event.room.RoomGameEndEvent;
import gameapi.event.room.RoomGameStartEvent;
import gameapi.listener.RoomGameProcessingListener;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import glorydark.sheepwar.RoomEvent.PlayerObtainSheepEventByCollision;
import glorydark.sheepwar.RoomEvent.PlayerObtainSheepEventByDamage;
import glorydark.sheepwar.RoomEvent.PlayerSlainEvent;
import glorydark.sheepwar.utils.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SheepWarListener implements Listener {

    @EventHandler
    public void damageSheep(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();
        if(damager instanceof Player && victim instanceof EntitySheep){
            Player player = ((Player) damager).getPlayer();
            if(player.getGamemode() != 0){ return; }
            Room room = Room.getRoom("SheepWar", player);
            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                event.setCancelled(true);
                return;
            }
            victim.kill();
            victim.close();
            player.getLevel().addSound(player.getPosition(), Sound.NOTE_HARP);
            PlayerProperties.addPlayerSheep(player, (EntitySheep) victim);
            Server.getInstance().getPluginManager().callEvent(new PlayerObtainSheepEventByDamage(room, player));
            return;
        }
        if (damager instanceof Player && victim instanceof Player) {
            if(victim.getHealth() - event.getFinalDamage() < 0.0) {
                Room room = Room.getRoom("SheepWar", (Player) damager);
                Room room1 = Room.getRoom("SheepWar", (Player) victim);
                if (room != null && room1 != null) {
                    if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                        return;
                    }
                    if (Objects.equals(room.getRoomName(), room1.getRoomName())) {
                        for (Player player : room.getPlayers()) {
                            player.sendMessage(TextFormat.RED + " * " + victim.getName() + "was slained by" + damager.getName());
                        }
                        PlayerProperties.addPlayerSheep((Player) victim, (Player) victim);
                    }
                }
                Server.getInstance().getPluginManager().callEvent(new PlayerSlainEvent(room, ((Player) damager).getPlayer(), ((Player) victim).getPlayer()));
                spawnPlayer(room1, ((Player) victim).getPlayer(), true);
            }else{
                if(((Player) damager).getInventory().getItemInHand() instanceof ItemBlazeRod){
                    event.setKnockBack(3.0f);
                    victim.setOnFire(2);
                    ((Player) victim).sendMessage(damager+"使用Lucky Rod将你击飞并附加2秒着火效果");
                }
            }
        }
    }

    @EventHandler
    public void EntityDamage(EntityDamageEvent event){
        Entity victim = event.getEntity();
        if (victim instanceof Player) {
            Room room = Room.getRoom("SheepWar", (Player) victim);
            if (room != null) {
                if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                    return;
                }
                if (victim.getHealth() - event.getFinalDamage() < 0.0) {
                    spawnPlayer(room, ((Player) victim).getPlayer(), true);
                    switch (event.getCause()){
                        case SUFFOCATION:
                        case DROWNING:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "感受到窒息的感觉！");
                            break;
                        case PROJECTILE:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "被射死了！");
                            break;
                        case FALL:
                        case VOID:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "体验了飞翔的感觉！");
                            break;
                        case BLOCK_EXPLOSION:
                        case ENTITY_EXPLOSION:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "芜湖起飞了！");
                            break;
                        case LIGHTNING:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "渡劫失败！");
                            break;
                        case FIRE_TICK:
                        case FIRE:
                        case LAVA:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "与火亲密接触一命呜呼了！");
                            break;
                        case HUNGER:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "还是困难户，吃不饱去了天堂！");
                            break;
                        case MAGIC:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "感受到魔幻的力量！ \nIt‘s my dream, it's magic, 照亮我的心~~~");
                            break;
                        case ENTITY_ATTACK:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "在战斗中英勇牺牲了！");
                            break;
                        case CUSTOM:
                        case CONTACT:
                        case SUICIDE:
                            broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "一命呜呼了！");
                            break;
                    }
                    broadcastMessageToAll(room, "玩家" + ((Player) victim).getPlayer().getName() + "一命呜呼了！");
                    event.setDamage(0f);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event){
        Room room = Room.getRoom("SheepWar", event.getPlayer());
        if(room != null) {
            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){
                return;
            }
            Player player = event.getPlayer();
            if(player.getGamemode() != 0){ return; }
            for (Entity e : player.getLevel().getEntities()) {
                if (e instanceof EntitySheep) {
                    Position pos = event.getPlayer().getPosition();
                    if (e.getPosition().distance(pos) < 1.5) {
                        e.kill();
                        e.close();
                        player.getLevel().addSound(player.getPosition(), Sound.NOTE_HARP);
                        PlayerProperties.addPlayerSheep(event.getPlayer(), (EntitySheep) e);
                        Server.getInstance().getPluginManager().callEvent(new PlayerObtainSheepEventByCollision(room, player));
                    }
                }
            }
        }
    }

    @EventHandler
    public void GameStartEvent(RoomGameStartEvent event){
        if(event.getRoom().getGameName().equals("SheepWar")){
            for(Player player: event.getRoom().getPlayers()){
                player.getInventory().clearAll();
                spawnPlayer(event.getRoom(), player, false);
            }
        }
    }

    @EventHandler
    public void GameStartListener(RoomGameProcessingListener event){
        Room room = event.getRoom();
        if(!room.getGameName().equals("SheepWar")){ return; }
        //Refresh sheeps spawn
        int time = event.getRoom().getTime();
        ExtendRoomData erd = SheepWarMain.rooms.get(room);
        Level level = erd.getStartLevel();
        if (time == 5 || time == Math.floor(room.getGameTime() * 0.1) || time == Math.floor(room.getGameTime() * 0.3) || time == Math.floor(room.getGameTime() * 0.5) || time == Math.floor(room.getGameTime() * 0.7) || time == Math.floor(room.getGameTime() * 0.9)) {
            broadcastMessageToAll(room, "[!] 白羊羊已经刷新");
            for (Entity entity : level.getEntities()){
                if(entity instanceof EntitySheep && DyeColor.getByWoolData(((EntitySheep)entity).getColor()) == DyeColor.WHITE){
                    entity.kill();
                    entity.close();
                }
            }
            for (Position position : erd.getSheepSpawns(SheepType.NormalSheep)) {
                SheepWarMain.spawnSheep(room, position, SheepType.NormalSheep);
            }
            broadcastMessageToAll(room, "[!] 补给箱已送达！");
            for (Location location: erd.getChestPos()){
                refreshSupplyBox(location);
            }
        }
        if (time == Math.floor(room.getGameTime() * 0.1)) {
            broadcastMessageToAll(room, "[!] 黑羊羊已经刷新");
            for (Position position : erd.getSheepSpawns(SheepType.Black_Sheep)) {
                SheepWarMain.spawnSheep(room, position, SheepType.Black_Sheep);
            }
        }
        if (time == Math.floor(room.getGameTime() * 0.3)) {
            broadcastMessageToAll(room, "[!] 蓝羊羊已经刷新");
            for (Position position : erd.getSheepSpawns(SheepType.Blue_Sheep)) {
                SheepWarMain.spawnSheep(room, position, SheepType.Blue_Sheep);
            }
        }
        if (time == Math.floor(room.getGameTime() * 0.4)) {
            broadcastMessageToAll(room, "[!] 绿羊羊已经刷新");
            for (Position position : erd.getSheepSpawns(SheepType.Green_Sheep)) {
                SheepWarMain.spawnSheep(room, position, SheepType.Green_Sheep);
            }
        }
        if (time == Math.floor(room.getGameTime() * 0.5)) {
            broadcastMessageToAll(room, "[!] 红羊羊已经刷新");
            for (Position position : erd.getSheepSpawns(SheepType.Red_Sheep)) {
                SheepWarMain.spawnSheep(room, position, SheepType.Red_Sheep);
            }
        }
        if (time == Math.floor(room.getGameTime() * 0.6)) {
            broadcastMessageToAll(room, "[!] 紫羊羊已经刷新");
            broadcastMessageToAll(room, TextFormat.YELLOW+"决胜时刻开始！");
            for (Position position : erd.getSheepSpawns(SheepType.Purple_Sheep)) {
                SheepWarMain.spawnSheep(room, position, SheepType.Purple_Sheep);
            }
        }
        if (time == Math.floor(room.getGameTime() * 0.7)) {
            broadcastMessageToAll(room, "[!] 金羊羊已经刷新");
            for (Position position : erd.getSheepSpawns(SheepType.Gold_Sheep)) {
                SheepWarMain.spawnSheep(room, position, SheepType.Gold_Sheep);
            }
        }
        // refresh
        for(Player player: room.getPlayers()){
            HashMap<SheepType, Integer> cache = PlayerProperties.getPlayerSheepData(player).getPlayerSheeps();
            PlayerProperties.showPlayerScoreBoard(player);
            Integer gold = cache.get(SheepType.Gold_Sheep);
            if(gold > 0){
                player.addEffect(Effect.getEffect(Effect.HEALTH_BOOST).setAmplifier(1).setDuration(30));
                player.addEffect(Effect.getEffect(Effect.JUMP_BOOST).setAmplifier(1).setDuration(30));
                player.getLevel().addParticle(new DustParticle(player.getPosition(), BlockColor.GOLD_BLOCK_COLOR));
                continue;
            }
            Integer green = cache.get(SheepType.Green_Sheep);
            if(green > 0){
                player.addEffect(Effect.getEffect(Effect.HEALTH_BOOST).setAmplifier(1).setDuration(30));
                player.getLevel().addParticle(new DustParticle(player.getPosition(), BlockColor.GREEN_BLOCK_COLOR));
                continue;
            }
            Integer blue = cache.get(SheepType.Blue_Sheep);
            if(blue > 0){
                player.addEffect(Effect.getEffect(Effect.SPEED).setAmplifier(1).setDuration(30));
                player.getLevel().addParticle(new DustParticle(player.getPosition(), BlockColor.BLUE_BLOCK_COLOR));
                continue;
            }
            Integer purple = cache.get(SheepType.Purple_Sheep);
            if(purple > 0){
                player.addEffect(Effect.getEffect(Effect.JUMP_BOOST).setAmplifier(1).setDuration(30));
                player.getLevel().addParticle(new DustParticle(player.getPosition(), BlockColor.PURPLE_BLOCK_COLOR));
                continue;
            }
            Integer black = cache.get(SheepType.Black_Sheep);
            if(black > 0){
                player.addEffect(Effect.getEffect(Effect.INSTANT_HEALTH).setAmplifier(1).setDuration(30));
                player.getLevel().addParticle(new DustParticle(player.getPosition(), BlockColor.BLACK_BLOCK_COLOR));
                continue;
            }
            Integer red = cache.get(SheepType.Red_Sheep);
            if(red > 0){
                player.addEffect(Effect.getEffect(Effect.SWIFTNESS).setAmplifier(1).setDuration(30));
                player.getLevel().addParticle(new DustParticle(player.getPosition(), BlockColor.RED_BLOCK_COLOR));
            }
        }
    }

    public void broadcastMessageToAll(Room room, String string){
        for(Player player : room.getPlayers()){
            player.sendMessage(string);
        }
    }

    public void refreshSupplyBox(Location location){
        Block block = location.getLevelBlock();
        if(!(block instanceof BlockChest)){ return; }
        BlockEntityChest chest = (BlockEntityChest) block.getLevel().getBlockEntity(block.getLocation());
        chest.getInventory().clearAll();
        for(RefreshItem item: SheepWarMain.items){
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int i = random.nextInt(100);
            if(i < item.getOdds()){
                chest.getInventory().addItem(item.getItem());
                chest.setName("SupplyBox");
            }
        }
    }

    public void spawnPlayer(Room room, Player player, Boolean isRespawn){
        if(room != null){
            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){ return; }
            List<Location> stringList = SheepWarMain.rooms.get(room).getRandomSpawn();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int i = random.nextInt(stringList.size());
            Location location = stringList.get(i).getLocation();
            ExtendRoomData erd = SheepWarMain.rooms.get(room);
            location.setLevel(erd.getStartLevel());
            PlayerProperties.resetPlayerSheep(player);
            if(isRespawn){
                player.getInventory().clearAll();
                player.setGamemode(3);
                player.sendTitle(TextFormat.RED+"您死了", "还有3秒复活！");
                Server.getInstance().getScheduler().scheduleDelayedTask(new RespawnTask(room, player, location), 60);
            }else{
                player.teleportImmediate(location);
                player.setGamemode(0);
                player.setHealth(10);
                player.setMaxHealth(10);
                player.getFoodData().reset();
                player.getInventory().clearAll();
                player.getInventory().addItem(Item.get(Item.WOODEN_SWORD, 0, 1));
                player.getInventory().addItem(Item.get(Item.SNOWBALL, 0, 16));
                player.getInventory().addItem(Item.get(Item.ENDER_PEARL, 0, 3));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void touch(PlayerInteractEvent event) {
        Room room = Room.getRoom("SheepWar", event.getPlayer());
        if(room == null){ return; }
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (item instanceof ItemBookEnchanted && item.getCustomName().equals("§l§c退出房间")) {
            room.removePlayer(player, true);
            player.sendMessage("§l§c您已退出房间！");
        }
    }

    @EventHandler
    public void end(RoomGameEndEvent event){
        if(!event.getRoom().getGameName().equals("SheepWar")){ return; }
        HashMap<Player, Integer> map = new HashMap<>();
        Room room = event.getRoom();
        List<Player> players = room.getPlayers();
        for(Player player: room.getPlayers()){
            map.put(player, PlayerProperties.getScore(player));
        }
        List<Map.Entry<Player, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        List<Player> rank = new ArrayList<>();
        for (Map.Entry<Player, Integer> playerIntegerEntry : list) {
            rank.add(playerIntegerEntry.getKey());
        }

        List<Player> winner = new ArrayList<>();
        int middlePoint = players.size()/2;
        if(middlePoint > 0) {
            for (int i = 0; i < middlePoint; i++) {
                winner.add(rank.get(i));
            }
            Integer lastScore = list.get(middlePoint - 1).getValue();
            for (int i = middlePoint; i < players.size(); i++) {
                if (Objects.equals(list.get(i).getValue(), lastScore)) {
                    winner.add(list.get(i).getKey());
                } else {
                    break;
                }
            }
        }else{
            winner = players;
        }
        Config config = new Config(SheepWarMain.path+"/rooms.yml", Config.YAML);
        LinkedHashMap<String, Object> configs = config.get(room.getRoomName(), new LinkedHashMap<>());
        List<String> winnerCmd = (List<String>) configs.getOrDefault("胜利控制台指令", new ArrayList<>());
        List<String> loserCmd = (List<String>) configs.getOrDefault("失败控制台指令", new ArrayList<>());
        for(Player player: players){
            if(winner.contains(player)){
                player.sendMessage("恭喜您获得胜利，获得第"+(rank.indexOf(player)+1)+"名的好成绩！");
                for(String cmd: winnerCmd){
                    String out = cmd.replace("%p", player.getName());
                    Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), out);
                }
            }else{
                player.sendMessage("您非常遗憾失败了，本次获得第"+(rank.indexOf(player)+1)+"名的成绩。不要灰心！");
                for(String cmd: loserCmd){
                    String out = cmd.replace("%p", player.getName());
                    Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), out);
                }
            }
        }

        for(Entity e: SheepWarMain.rooms.get(room).getStartLevel().getEntities()){
            if(e instanceof EntitySheep){
                e.kill();
                e.close();
            }
        }
    }
}
