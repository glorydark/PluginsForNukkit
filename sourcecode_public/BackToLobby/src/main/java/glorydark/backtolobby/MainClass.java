package glorydark.backtolobby;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainClass extends PluginBase implements Listener {

    public static Map<String, Object> configs;

    public static List<Player> playerList = new ArrayList<>();

    public static Location spawn;

    public static int waitTicks;

    @Override
    public void onLoad() {
        this.getLogger().info("BackToLobby onLoad");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config config = new Config(this.getDataFolder().getPath()+"/config.yml", Config.YAML);
        configs = config.getAll();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getCommandMap().register("", new BackToLobbyCommand(config.getString("command", "backtolobby"), new ArrayList<>(config.getStringList("aliases")).toArray(new String[0])));
        String posString = (String) configs.getOrDefault("spawn", "null");
        if(posString.equals("null")){
            spawn = this.getServer().getDefaultLevel().getSpawnLocation().getLocation();
        }else{
            String[] posStrings = posString.split(":");
            if(!this.getServer().isLevelLoaded(posStrings[3])){
                if(!this.getServer().loadLevel(posStrings[3])){
                    this.setEnabled(false);
                    return;
                }
            }
            if(posStrings.length == 4){
                spawn = new Location(Double.parseDouble(posStrings[0]), Double.parseDouble(posStrings[1]), Double.parseDouble(posStrings[2]), this.getServer().getLevelByName(posStrings[3]));
            }
            if(posStrings.length == 6){
                spawn = new Location(Double.parseDouble(posStrings[0]), Double.parseDouble(posStrings[1]), Double.parseDouble(posStrings[2]), Double.parseDouble(posStrings[4]), Double.parseDouble(posStrings[5]), 0, this.getServer().getLevelByName(posStrings[3]));
            }
        }
        if(spawn == null){
            this.getLogger().warning("您的坐标配置错误！格式：x:y:z:yaw:pitch");
            this.setEnabled(false);
            return;
        }
        waitTicks = (int) configs.getOrDefault("wait-ticks", 5);
        this.getLogger().info("BackToLobby onEnable");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("BackToLobby onDisable");
    }

    public static void backToLobby(Player player){
        if(!playerList.contains(player)) {
            if(waitTicks > 0) {
                Effect effect = Effect.getEffect(Effect.LEVITATION);
                effect.setDuration(waitTicks+50);
                player.addEffect(effect);
                player.sendTitle((String) configs.getOrDefault("pre-back-title", "正在回城中！"), (String) configs.getOrDefault("pre-back-subtitle","请不要移动"));
                playerList.add(player);
                Server.getInstance().getScheduler().scheduleRepeatingTask(new ParticleSchedule(player, waitTicks, spawn), 1);
            }else{
                player.teleport(spawn, null);
                player.sendTitle((String) configs.getOrDefault("back-title", "回城成功！"), (String) configs.getOrDefault("back-subtitle", ""));
            }
        }
    }

    @EventHandler
    public void Move(PlayerMoveEvent event){
        if(checkPosition(event.getFrom(), event.getTo())){
            return;
        }
        if(playerList.contains(event.getPlayer())){
            playerList.remove(event.getPlayer());
            event.getPlayer().removeEffect(Effect.LEVITATION);
            event.getPlayer().sendTitle("您打断了回城！");
        }
    }

    public boolean checkPosition(Location pos1, Location pos2){
        return (pos1.getFloorX() == pos2.getFloorX()) && (pos1.getFloorZ() == pos2.getFloorZ());
    }
}
