package glorydark.lootbattle;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import gameapi.room.Room;
import gameapi.room.RoomRule;
import gameapi.room.RoomStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//简单pvp写法
public class MainClass extends PluginBase {

    public static List<Room> roomList = new ArrayList<>();

    @Override
    public void onLoad() {
        this.getLogger().info("LootBattle onLoad");
    }

    @Override
    public void onEnable() {
        this.saveResource("rooms.yml", false);
        Config config = new Config(this.getDataFolder().getPath()+"/rooms.yml", Config.YAML);
        RoomRule roomRule = new RoomRule(0);
        roomRule.allowDamagePlayer = true;
        roomRule.allowEntityExplosionDamage = true;
        roomRule.allowProjectTileDamage = true;
        roomRule.noStartWalk = false;
        roomRule.allowFoodLevelChange = false;
        roomRule.allowRespawn = true;
        roomRule.respawnCoolDownTick = 20;
        roomRule.allowSpectatorMode = true;
        //设置房间规则
        for(String key: config.getKeys(false)){
            Room room = new Room("LootBattle", roomRule, "",1);
            room.setRoomName(config.getString(key+".name"));
            room.setResetMap(false);
            String level = config.getString(key+".world");
            if(Server.getInstance().isLevelLoaded(level)){
                if(!Server.getInstance().loadLevel(level)){
                    return;
                }
            }
            room.setWaitSpawn(config.getString(key+".spawn_wait")+":"+level);
            ConfigSection start =config.getSection(key+".spawn_start");
            for(String team: start.getKeys(false)){
                room.addStartSpawn(start.getString(team+".spawn")+":"+level);
                room.registerTeam(team, start.getString(team+".prefix"), start.getInt(team+".max"), start.getInt(team+".spawn_index"));
                this.getLogger().info("§a加载队伍成功，队伍名:"+team);
            }
            this.getLogger().info("§a加载队伍成功，共加载队伍:"+room.getTeams().size());
            this.getLogger().info("§a队伍数据:");
            room.getTeams().forEach(team -> this.getLogger().info(team.toString()));
            room.setGameTime(config.getInt(key+".game_time", 60));
            room.setWaitTime(config.getInt(key+".wait_time", 10));
            room.setGameWaitTime(config.getInt(key+".ready_time", 10));
            room.setCeremonyTime(config.getInt(key+".ceremony_time", 10));

            room.setWinConsoleCommands(new ArrayList<>(config.getStringList("winner_commands")));
            room.setLoseConsoleCommands(new ArrayList<>(config.getStringList("loser_commands")));
            AtomicInteger max = new AtomicInteger(0);
            room.getTeams().forEach(team -> max.addAndGet(team.getMaxPlayer()));
            room.setMaxPlayer(max.get());
            room.setMinPlayer(room.getTeams().size()); //两边各一人至少
            ConfigSection common_tributes = config.getSection(key+".common_tribute");
            ConfigSection golden_tribute = config.getSection(key+".golden_tribute");
            TributeData data = new TributeData(common_tributes.getInt("interval"), golden_tribute.getInt("interval"), common_tributes.getInt("score"), golden_tribute.getInt("score"), new ArrayList<>(common_tributes.getStringList("spawns")), new ArrayList<>(golden_tribute.getStringList("spawns")), level);
            room.setRoomProperties("extend_data", data);
            Room.loadRoom(room);
            roomList.add(room);
            this.getLogger().info("§a加载房间成功，房间名:"+room.getRoomName()+", 最小人数"+room.getMinPlayer()+", 最大人数"+ room.getMaxPlayer());
        }
        //房间设置完成
        this.getServer().getPluginManager().registerEvents(new GameListener(), this);
        this.getServer().getCommandMap().register("", new Commands("lootbattle"));
        this.getLogger().info("LootBattle onEnable");
    }

    public static class Commands extends Command{

        public Commands(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            if(strings.length == 1) {
                switch (strings[0]) {
                    case "join":
                        if(commandSender.isPlayer()) {
                            Window.showPlayerRoomListWindow((Player) commandSender);
                        }else{
                            commandSender.sendMessage("请在游戏内执行此指令！");
                        }
                        break;
                    case "history":
                        if(commandSender.isPlayer()) {
                            Window.showPlayerHistoryWindow((Player) commandSender);
                        }else{
                            commandSender.sendMessage("请在游戏内执行此指令！");
                        }
                        break;
                    case "quit":
                        if(commandSender.isPlayer()) {
                            Room room = Room.getRoom("LootBattle", (Player) commandSender);
                            if(room != null){
                                if((room.getRoomStatus() == RoomStatus.ROOM_STATUS_WAIT || room.getRoomStatus() == RoomStatus.ROOM_STATUS_PreStart)) {
                                    room.removePlayer((Player) commandSender, true);
                                }else{
                                    commandSender.sendMessage("游戏已经开始，您不能随意退出！");
                                }
                            }else{
                                if(((Player) commandSender).getGamemode() == 3){
                                    Player p = (Player) commandSender;
                                    p.teleportImmediate(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation());
                                    p.setGamemode(0);
                                    p.sendMessage("已退出观战！");
                                }
                            }
                        }else{
                            commandSender.sendMessage("请在游戏内执行此指令！");
                        }
                        break;
                }
            }
            return true;
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("LootBattle onDisable");
    }
}