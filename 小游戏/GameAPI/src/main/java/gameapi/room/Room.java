package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.arena.Arena;
import gameapi.event.base.RoomPlayerJoinEvent;
import gameapi.event.base.RoomPlayerPreJoinEvent;
import gameapi.event.base.RoomPlayerLeaveEvent;
import gameapi.event.base.RoomPlayerRespawnEvent;
import gameapi.event.room.*;
import gameapi.inventory.Inventory;
import gameapi.utils.AdvancedLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Glorydark
 */
@Getter
@Setter
public class Room {

    private Boolean temporary = false;
    private Boolean resetMap = true; // Resetting map is defaultly set to false.
    private String roomName = "null";
    private RoomRule roomRule;
    private RoomStatus roomStatus = RoomStatus.ROOM_STATUS_WAIT;
    private List<Player> players = new ArrayList<>();
    private int maxPlayer = 2; //最大人数
    private int minPlayer = 16; //最少人数
    private int waitTime = 10; //等待时间
    private int gameWaitTime = 10; //开始/结束缓冲时间
    private int gameTime = 10; //游戏开始时间
    private int ceremonyTime = 10; //颁奖典礼时间

    private int gameEndTime = 10;
    private int MaxRound; //回合数
    private int round = 0;
    private int time = 0; //时间记录

    private List<Player> spectators = new ArrayList<>();

    private Level playLevel;
    private AdvancedLocation waitSpawn = new AdvancedLocation();
    private List<AdvancedLocation> startSpawn = new ArrayList<>();
    private AdvancedLocation endSpawn;
    private List<AdvancedLocation> spectatorSpawn = new ArrayList<>();
    private ConcurrentHashMap<String, Team> teamCache = new ConcurrentHashMap<>();
    private HashMap<Player, Float> playersHealth = new HashMap<>();
    private String roomLevelBackup;
    private String gameName;
    private List<String> winConsoleCommands = new ArrayList<>();
    private List<String> loseConsoleCommands = new ArrayList<>();
    private LinkedHashMap<String, LinkedHashMap<String, Object>> playerProperties = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> roomProperties = new LinkedHashMap<>(); //储存房间数据

    public Room(String gameName, RoomRule roomRule, String roomLevelBackup, int round) {
        this.MaxRound = round;
        this.roomRule = roomRule;
        this.roomLevelBackup = roomLevelBackup;
        this.gameName = gameName;
        AdvancedLocation endSpawn = new AdvancedLocation();
        endSpawn.setLocation(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation());
        endSpawn.setVersion(0);
        this.endSpawn = endSpawn;
    }

    public Object getPlayerProperties(String player, String key) {
        return  playerProperties.containsKey(player)? playerProperties.get(player).getOrDefault(key, null): null;
    }

    public Object getPlayerProperties(String player, String key, Object defaultValue) {
        return playerProperties.containsKey(player)? playerProperties.get(player).getOrDefault(key, defaultValue): defaultValue;
    }

    public void setPlayerProperties(String player, String key, Object value) {
        if(playerProperties.containsKey(player)){
            playerProperties.get(player).put(key, value);
        }else{
            playerProperties.put(player, new LinkedHashMap<>());
            playerProperties.get(player).put(key, value);
        }
    }

    public Object getRoomProperties(String key) {
        return roomProperties.getOrDefault(key, null);
    }

    public Object getRoomProperties(String key, Object defaultValue) {
        return roomProperties.getOrDefault(key, defaultValue);
    }

    public void setRoomProperties(String key, Object value) {
        this.roomProperties.put(key, value);
    }

    public void executeLoseCommands(Player player) {
        for(String string: loseConsoleCommands){
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", player.getName()).replace("%level%",player.getLevel().getName()).replace("%gamename%", gameName).replace("%roomname", roomName));
        }
    }

    public void executeWinCommands(Player player) {
        for(String string: winConsoleCommands){
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", player.getName()).replace("%level%",player.getLevel().getName()).replace("%gamename%", gameName).replace("%roomname", roomName));
        }
    }

    public void allocatePlayerToTeams(){
        if(teamCache.keySet().size() == 0){ return; }
        for(Player player: players) {
            ConcurrentHashMap<String, Team> map = new ConcurrentHashMap<>(teamCache);
            List<Map.Entry<String, Team>> list = map.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(t -> t.getValue().getSize()))
                    .collect(Collectors.toList());
            teamCache.get(list.get(0).getKey()).addPlayer(player); //从最低人数来尝试加入
            Team team = list.get(0).getValue();
            player.sendMessage("您加入了："+team.getPrefix()+team.getRegistryName());
        }
    }

    public boolean addTeamPlayer(String registry, Player player){
        if(getPlayerTeam(player) != null){
            return false;
        }
        if(teamCache.containsKey(registry) && teamCache.get(registry).isAvailable()){ //禁止加入满人队伍
            teamCache.get(registry).addPlayer(player);
            player.sendMessage("您加入了："+teamCache.get(registry).getPrefix()+registry);
        }else{
            return false;
        }
        return true;
    }

    public void removeTeamPlayer(String registry, Player player){
        if(teamCache.containsKey(registry)){
            teamCache.get(registry).removePlayer(player);
            player.sendMessage("您退出了："+teamCache.get(registry).getPrefix()+registry);
        }
    }

    public void removeTeamPlayer(Player player){
        for(Map.Entry<String, Team> entrySet: teamCache.entrySet()){
            if(entrySet.getValue().hasPlayer(player)){
                teamCache.get(entrySet.getKey()).removePlayer(player);
            }
        }
    }

    public Team getPlayerTeam(Player player){
        for(Map.Entry<String, Team> entrySet: teamCache.entrySet()){
            if(entrySet.getValue().hasPlayer(player)){
                return entrySet.getValue();
            }
        }
        return null;
    }

    public Collection<Team> getTeams(){
        return teamCache.values();
    }

    public Team getTeam(String registry){
        return teamCache.getOrDefault(registry, null);
    }

    public void registerTeam(String registryName, String prefix, int MaxPlayers, int spawnIndex){
        Team team = new Team(this, registryName, prefix, MaxPlayers, spawnIndex);
        teamCache.put(team.getRegistryName(), team);
    }

    public static void loadRoom(Room room){
        List<Room> rooms = new ArrayList<>(GameAPI.RoomHashMap.getOrDefault(room.getGameName(), new ArrayList<>()));
        rooms.add(room);
        GameAPI.RoomHashMap.put(room.getGameName(), rooms);
    }

    public Boolean addPlayer(Player player){
        RoomStatus roomStatus = this.getRoomStatus();
        if(roomStatus == RoomStatus.ROOM_MapLoadFailed){
            player.sendMessage("地图加载失败，请联系腐竹！");
            return false;
        }
        if(roomStatus != RoomStatus.ROOM_STATUS_WAIT && roomStatus != RoomStatus.ROOM_STATUS_PreStart){
            player.sendMessage("房间游戏已经开始！");
            return false;
        }
        if(GameAPI.playerRoomHashMap.get(player) != null){
            player.sendMessage("您已经在房间中了！");
            return false;
        }
        if(this.players.size() < this.maxPlayer){
            if(this.players.contains(player)){
                player.sendMessage("您已经在此房间中了！");
                return false;
            }else{
                RoomPlayerPreJoinEvent ev = new RoomPlayerPreJoinEvent(this,player);
                Server.getInstance().getPluginManager().callEvent(ev);
                if(!ev.isCancelled()) {
                    GameAPI.playerRoomHashMap.put(player, this);
                    Inventory.saveBag(player);
                    playerProperties.put(player.getName(), new LinkedHashMap<>());
                    this.players.add(player);
                    waitSpawn.teleport(player);
                    player.setGamemode(2);
                    player.getFoodData().reset();
                    for (Player p : this.players) {
                        p.sendMessage(player.getName() + " §l§a加入房间 【" + this.players.size() + "/" + this.maxPlayer + "】");
                    }
                    Server.getInstance().getPluginManager().callEvent(new RoomPlayerJoinEvent(this, player));
                }
                return true;
            }
        }
        return false;
    }

    public void removePlayer(Player player, Boolean saveBag){
        RoomPlayerLeaveEvent ev = new RoomPlayerLeaveEvent(this,player);
        Server.getInstance().getPluginManager().callEvent(ev);
        //GameAPI.registry.callEvent(this.getGameName(), ev);
        if(!ev.isCancelled()) {
            player.getInventory().clearAll();
            if (saveBag) {
                Inventory.loadBag(player);
            }
            player.teleportImmediate(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation());
            player.setGamemode(0);
            if(this.getPlayerTeam(player) != null){
                this.getPlayerTeam(player).removePlayer(player);
            }
            this.playerProperties.remove(player.getName());
            this.players.remove(player);
            GameAPI.playerRoomHashMap.remove(player);
        }
    }

    public static boolean isRoomCurrentPlayLevel(Level level){
        List<Room> collect = GameAPI.playerRoomHashMap.values().stream().filter(room -> level.getName().equals(room.getPlayLevel().getName())).collect(Collectors.toList());
        return collect.size() > 0;
    }

    public static Room getRoom(String gameName, Player p){
        if(getRoom(p) == null){ return null; }
        return getRoom(p).getGameName().equals(gameName)? getRoom(p) : null;
    }

    public static Room getRoom(Player p){
        return GameAPI.playerRoomHashMap.getOrDefault(p, null);
    }

    public static Room getRoom(String gameName, String roomName){
        for(Room room: GameAPI.RoomHashMap.getOrDefault(gameName, new ArrayList<>())){
            if(room.roomName.equals(roomName) && room.gameName.equals(gameName)){
                return room;
            }
        }
        return null;
    }

    public void detectToReset(){
        this.players = new ArrayList<>();
        this.round = 0;
        this.playerProperties = new LinkedHashMap<>();
        this.playersHealth = new HashMap<>();
        this.time = 0;
        this.getTeamCache().forEach((key, value) -> value.resetAll());
        if(this.getRoomStatus() != RoomStatus.ROOM_MapInitializing && this.getRoomStatus() != RoomStatus.ROOM_STATUS_WAIT) {
            if (playLevel != null) {
                for (Entity entity : playLevel.getEntities()) {
                    if (!(entity instanceof Player)) {
                        entity.kill();
                        entity.close();
                    }
                }
            }
            if(this.temporary){
                this.setRoomStatus(RoomStatus.ROOM_MapInitializing, false);
                GameAPI.plugin.getLogger().alert("检测到房间内无玩家，正在删除房间:" + this.getRoomName());
                if (startSpawn.size() > 0) {
                    String levelName = this.getPlayLevel().getName();
                    if(this.getPlayLevel().unload(true)) {
                        Arena.delWorldByName(levelName);
                    }
                }
                List<Room> rooms = GameAPI.RoomHashMap.getOrDefault(this.getGameName(), new ArrayList<>());
                GameAPI.RoomHashMap.put(this.getGameName(), rooms);
            }else {
                if (this.resetMap) {
                    GameAPI.plugin.getLogger().alert("检测到房间内无玩家，正在重置地图，房间:" + this.getRoomName());
                    Arena.reloadLevel(this);
                }
                this.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
            }
        }

    }

    public void setRoomStatus(RoomStatus status){
        this.setRoomStatus(status, true);
    }

    public void setRoomStatus(RoomStatus status, boolean callEvent){
        if(callEvent){
            switch (status){
                case ROOM_STATUS_GameReadyStart:
                    Server.getInstance().getPluginManager().callEvent(new RoomReadyStartEvent(this));
                    break;
                case ROOM_STATUS_PreStart:
                    Server.getInstance().getPluginManager().callEvent(new RoomPreStartEvent(this));
                    break;
                case ROOM_STATUS_GameStart:
                    Server.getInstance().getPluginManager().callEvent(new RoomGameStartEvent(this));
                    break;
                case ROOM_STATUS_GameEnd:
                    Server.getInstance().getPluginManager().callEvent(new RoomGameEndEvent(this));
                    break;
                case ROOM_STATUS_Ceremony:
                    Server.getInstance().getPluginManager().callEvent(new RoomCeremonyEvent(this));
                    break;
            }
        }
        this.roomStatus = status;
    }

    public void resetAll(){
        this.setRoomStatus(RoomStatus.ROOM_MapInitializing, false);
        //Server.getInstance().getScheduler().scheduleAsyncTask(MainClass.plugin,new AsyncBlockCleanTask(this));
        for(Player player: players){
            RoomPlayerLeaveEvent ev = new RoomPlayerLeaveEvent(this,player);
            Server.getInstance().getPluginManager().callEvent(ev);
            player.getInventory().clearAll();
            //GameAPI.registry.callEvent(this.getGameName(), ev);
            Inventory.loadBag(player);
            AdvancedLocation location = getEndSpawn();
            location.teleport(player);
        }
        for(Player player: players){
            GameAPI.playerRoomHashMap.remove(player);
        }
        this.players = new ArrayList<>();
        this.round = 0;
        this.time = 0;
        this.playerProperties = new LinkedHashMap<>();
        this.teamCache.forEach((s, team) -> team.resetAll());
        if(!this.temporary) {
            for(Entity entity: this.getStartSpawn().get(0).getLevel().getEntities()){
                if(entity instanceof Player){
                    Player p = ((Player) entity);
                    p.teleportImmediate(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation());
                    if(p.getGamemode() == 3){
                        p.setGamemode(0);
                    }
                }else{
                    entity.kill();
                    entity.close();
                }
            }
            if (this.resetMap) {
                GameAPI.plugin.getLogger().alert("检测到房间游戏结束，正在重置地图，房间:" + this.getRoomName());
                Arena.reloadLevel(this);
            } else {
                GameAPI.plugin.getLogger().alert("检测到房间内无玩家，正在重置房间:" + this.getRoomName());
                this.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT, false);
            }
        }else{
            this.setRoomStatus(RoomStatus.ROOM_MapInitializing, false);
            GameAPI.plugin.getLogger().alert("检测到房间内无玩家，正在删除房间:" + this.getRoomName());
            if (startSpawn.size() > 0) {
                Level level = startSpawn.get(0).getLevel();
                String levelName = level.getName();
                if(level.unload(true)) {
                    Arena.delWorldByName(levelName);
                }
            }
            List<Room> rooms = GameAPI.RoomHashMap.getOrDefault(this.getGameName(), new ArrayList<>());
            GameAPI.RoomHashMap.put(this.getGameName(), rooms);
        }
    }

    public void initRoom(){
        this.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
        GameAPI.plugin.getLogger().error(roomName+" 成功还原地图！");
    }

    public AdvancedLocation getLocationByString(String string){
        String[] positions = string.split(":");
        if(positions.length < 4){ GameAPI.plugin.getLogger().warning("检测到坐标格式错误，请修改！"); return null; }
        if(!Server.getInstance().isLevelLoaded(positions[3])){
            GameAPI.plugin.getLogger().warning("房间世界不存在，尝试加载中！");
            if(Server.getInstance().loadLevel(positions[3])){
                Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
                AdvancedLocation advancedLocation = new AdvancedLocation();
                advancedLocation.setLocation(location);
                advancedLocation.setVersion(0);
                if(positions.length >= 6){
                    advancedLocation.setYaw(Double.parseDouble(positions[4]));
                    advancedLocation.setPitch(Double.parseDouble(positions[5]));
                    advancedLocation.setVersion(1);
                    if(positions.length == 7){
                        advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                        advancedLocation.setVersion(2);
                    }
                }
                return advancedLocation;
            }else{
                GameAPI.plugin.getLogger().warning("世界加载失败！世界名:"+positions[3]);
                return null;
            }
        }else{
            Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
            AdvancedLocation advancedLocation = new AdvancedLocation();
            advancedLocation.setLocation(location);
            advancedLocation.setVersion(0);
            if(positions.length >= 6){
                advancedLocation.setYaw(Double.parseDouble(positions[4]));
                advancedLocation.setPitch(Double.parseDouble(positions[5]));
                advancedLocation.setVersion(1);
                if(positions.length == 7){
                    advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                    advancedLocation.setVersion(2);
                }
            }
            return advancedLocation;
        }
    }

    public void setWaitSpawn(String position) {
        AdvancedLocation location = this.getLocationByString(position);
        if(location != null){
            this.waitSpawn = location;
        }else{
            this.setRoomStatus(RoomStatus.ROOM_MapLoadFailed, false);
        }
    }

    public void addStartSpawn(String position) {
        AdvancedLocation location = this.getLocationByString(position);
        if(location != null){
            this.startSpawn.add(location);
        }else{
            this.setRoomStatus(RoomStatus.ROOM_MapLoadFailed, false);
        }
    }

    public void addSpectatorSpawn(String position) {
        AdvancedLocation location = this.getLocationByString(position);
        if(location != null){
            this.spectatorSpawn.add(location);
        }else{
            this.setRoomStatus(RoomStatus.ROOM_MapLoadFailed, false);
        }
    }

    public void setEndSpawn(String position) {
        AdvancedLocation location = this.getLocationByString(position);
        if(location != null){
            this.endSpawn = location;
        }else{
            this.setRoomStatus(RoomStatus.ROOM_MapLoadFailed, false);
        }
    }

    public void setWaitSpawn(Location location) {
        AdvancedLocation advancedLocation = new AdvancedLocation();
        advancedLocation.setLocation(location);
        this.waitSpawn = advancedLocation;
    }

    public void addStartSpawn(Location location) {
        AdvancedLocation advancedLocation = new AdvancedLocation();
        advancedLocation.setLocation(location);
        this.startSpawn.add(advancedLocation);
    }

    public void addSpectatorSpawn(Location location) {
        AdvancedLocation advancedLocation = new AdvancedLocation();
        advancedLocation.setLocation(location);
        this.spectatorSpawn.add(advancedLocation);
    }

    public void setEndSpawn(Location location) {
        AdvancedLocation advancedLocation = new AdvancedLocation();
        advancedLocation.setLocation(location);
        this.endSpawn = advancedLocation;
    }

    public void setWaitSpawn(AdvancedLocation location) {
        this.waitSpawn = location;
    }

    public void addStartSpawn(AdvancedLocation location) {
        this.startSpawn.add(location);
    }

    public void addSpectatorSpawn(AdvancedLocation location) {
        this.spectatorSpawn.add(location);
    }

    public void setEndSpawn(AdvancedLocation location) {
        this.endSpawn = location;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return "{" +
                "\"temporary\":" + temporary +
                ", \"resetMap\":" + resetMap +
                ", \"roomName\":" + "\"" + roomName + "\"" +
                ", \"roomStatus\":" + "\"" + roomStatus + "\"" +
                ", \"maxPlayer\":" + maxPlayer +
                ", \"minPlayer\":" + minPlayer +
                ", \"waitTime\":" + waitTime +
                ", \"gameWaitTime\":" + gameWaitTime +
                ", \"gameTime\":" + gameTime +
                ", \"ceremonyTime\":" + ceremonyTime +
                ", \"MaxRound\":" + MaxRound +
                ", \"round\":" + round +
                ", \"time\":" + time +
                ", \"playersHealth\":" + playersHealth +
                ", \"roomLevelBackup\":" + "\"" + roomLevelBackup + "\"" +
                ", \"gameName\":" + "\"" + gameName  + "\"" +
                ", \"winConsoleCommands\":" + winConsoleCommands +
                ", \"loseConsoleCommands\":" + loseConsoleCommands +
                '}';
    }

    public float getPlayerHealth(Player player) {
        if(playersHealth.containsKey(player)){
            return playersHealth.get(player);
        }else{
            return this.getRoomRule().defaultHealth;
        }
    }

    public void setPlayerHealth(Player player, float health) {
        this.playersHealth.put(player,health);
    }

    public void resetAllPlayersHealth(float health) {
        this.playersHealth.replaceAll((p, v) -> health);
    }

    public void addPlayerHealth(Player player,float add){
        if(this.playersHealth.containsKey(player)){
            this.playersHealth.put(player, this.playersHealth.get(player)+add);
        }else{
            this.playersHealth.put(player, this.getRoomRule().defaultHealth+add);
        }
    }

    public void reducePlayerHealth(Player player,float reduce){
        if(this.playersHealth.containsKey(player)){
            this.playersHealth.put(player, this.playersHealth.get(player)-reduce);
        }else{
            this.playersHealth.put(player, this.getRoomRule().defaultHealth-reduce);
        }
    }

    public void setSpectator(Player player, boolean setMode, boolean dead){
        player.getInventory().clearAll();
        if(setMode) {
            player.setGamemode(3, false);
        }else{
            player.setGamemode(0, false);
        }
        player.setHealth(player.getMaxHealth());
        spectators.add(player);
        if(this.getSpectatorSpawn().size() != 0){
            Random random = new Random(this.getSpectatorSpawn().size());
            AdvancedLocation location = this.getSpectatorSpawn().get(random.nextInt(this.getSpectatorSpawn().size()));
            location.teleport(player);
        }
        if(dead) {
            player.sendTitle(TextFormat.RED + "您已死亡！", "已进入观察状态!", 5, 10, 5);
            if(spectatorSpawn.size() != 0){
                Random random = new Random();
                spectatorSpawn.get(random.nextInt(spectatorSpawn.size())).teleport(player);
                teleportToSpawn(player);
            }
            if(roomRule.allowRespawn){
                RoomPlayerRespawnEvent ev = new RoomPlayerRespawnEvent(this, player);
                Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.plugin, ()->{
                    Server.getInstance().getPluginManager().callEvent(ev);
                    //GameAPI.registry.callEvent(this.getGameName(), ev);
                    if(!ev.isCancelled() && this.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
                        player.sendTitle("您已复活");
                        player.setGamemode(0);
                        teleportToSpawn(player);
                        spectators.remove(player);
                    }
                }, roomRule.respawnCoolDownTick);
            }
        }
    }

    public boolean isSpectator(Player player){
        return spectators.contains(player);
    }

    public void teleportToSpawn(Player p){
        if(this.getPlayerTeam(p) != null){
            this.getPlayerTeam(p).teleportToSpawn();
            return;
        }
        if (this.startSpawn.size() > 1) {
            if (this.getPlayerProperties(p.getName(), "spawnIndex") == null) {
                Random random = new Random(System.currentTimeMillis());
                AdvancedLocation location = this.startSpawn.get(random.nextInt(this.startSpawn.size()));
                location.teleport(p);
            } else {
                AdvancedLocation location = this.startSpawn.get((Integer) this.getPlayerProperties(p.getName(), "spawnIndex"));
                location.teleport(p);
            }
        } else if (this.getStartSpawn().size() == 1) {
            AdvancedLocation location = this.startSpawn.get(0);
            location.teleport(p);
        }
    }
}
