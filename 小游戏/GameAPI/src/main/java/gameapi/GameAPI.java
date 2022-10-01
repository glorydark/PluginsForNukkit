package gameapi;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import gameapi.arena.Arena;
import gameapi.entity.EntityUtils;
import gameapi.listener.PlayerEventListener;
import gameapi.listener.base.BaseGameRegistry;
import gameapi.room.Room;
import gameapi.commands.Commands;
import gameapi.task.RoomTask;
import gameapi.utils.GsonAdapter;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Glorydark
 * Some methods using in this class came from others, and you can find the original author in some specific classes!
 */
public class GameAPI extends PluginBase implements Listener {

    public static ConcurrentHashMap<String, List<Room>> RoomHashMap = new ConcurrentHashMap<>(); //房间状态
    public static HashMap<Player, Room> playerRoomHashMap = new LinkedHashMap<>(); //防止过多次反复检索房间
    public static String path = null;
    public static Plugin plugin = null;
    public static HashMap<String, Map<String, Object>> gameRecord = new HashMap<>();

    public static List<Player> debug = new ArrayList<>();

    public static int entityRefreshIntervals = 100;

    public static boolean saveBag;

    public static BaseGameRegistry registry;

    //此处引用lt-name的CrystalWar内的复原地图部分源码
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            5,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            task -> new Thread(task, "GameAPI Restore World Thread")
    );

    @Override
    public void onEnable() {
        path = this.getDataFolder().getPath();
        plugin = this;
        registry = new BaseGameRegistry();
        this.getDataFolder().mkdir();
        this.saveDefaultConfig();
        this.saveResource("rankings.yml");
        File file = new File(path+"/worlds/");
        File file1 = new File(path+"/gameRecords/");
        file.mkdirs();
        file1.mkdir();
        Config config = new Config(path+"/config.yml", Config.YAML);
        saveBag = config.getBoolean("save_bag", false);
        //loadSkills();
        loadAllGameRecord();
        loadAllRankingListEntities();
        this.getServer().getScheduler().scheduleRepeatingTask(plugin, new RoomTask(),20);
        this.getServer().getPluginManager().registerEvents(new PlayerEventListener(),this);
        this.getServer().getCommandMap().register("",new Commands("gametest"));
        Server.getInstance().getScheduler().scheduleRepeatingTask(this, ()->{
            List<Player> players = new ArrayList<>(debug);
            players.forEach(player -> {
                if(player == null || !player.isOnline()){ debug.remove(player); return;}
                DecimalFormat df = new DecimalFormat("#0.00");
                String line1 = "所在位置: [" + df.format(player.getX()) + ":" + df.format(player.getY()) + ":" + df.format(player.getZ()) + "] 世界名: " + player.getLevel().getName();
                String line2 = "yaw: " + df.format(player.getYaw()) + " pitch: " + df.format(player.pitch) + " headYaw: " + df.format(player.headYaw);
                Item item = player.getInventory().getItemInHand();
                String line3 = "手持物品id: [" + item.getId() + ":" + item.getDamage() + "] 数量:" + item.getCount();
                Block block = player.getTargetBlock(32);
                        String line4;
                        String line5;
                        if(block != null) {
                            line4 = "所指方块id: [" + block.getId() + ":" + block.getDamage() + "] 方块名称:" + block.getName();
                            line5 = "所指方块位置: [" + df.format(block.getX()) + ":" + df.format(block.getY()) + ":" + df.format(block.getZ()) + "]";
                        }else{
                            line4 = "所指方块id: [无] 方块名称:无";
                            line5 = "所指方块位置: [无]";
                        }
                        player.sendActionBar(line1 + "\n" + line2 + "\n" + line3 + "\n" + line4 + "\n" + line5);
                    }
            );
        }, 5);
        this.getLogger().info("§aDGameAPI Enabled!");
    }
    @Override
    public void onLoad() {
        this.getLogger().info("§aDGameAPI OnLoad!");
        this.getLogger().info("§a作者:glorydark");
    }

    public void loadAllGameRecord(){
        File[] files = new File(path+"/gameRecords/").listFiles();
        if(files != null && files.length > 0){
            for(File file:files){
                String fileName = file.getName().split("\\.")[0];
                Config config;
                config = new Config(file.getPath(), Config.YAML);
                gameRecord.put(fileName, config.getAll());
            }
        }
    }

    public void loadAllRankingListEntities(){
        Config config = new Config(path+"/rankings.yml");
        entityRefreshIntervals = config.getInt("refresh_interval", 100);
        List<Map<String, Object>> maps = (List<Map<String, Object>>) config.get("list");
        for(Map<String, Object> map: maps){
            String level = (String) map.get("level");
            if(Server.getInstance().getLevelByName(level) == null){
                if(!Server.getInstance().loadLevel(level)){
                    this.getLogger().warning("[RankingListLoader] 无法找到世界:"+level);
                    continue;
                }else{
                    this.getLogger().warning("[RankingListLoader] 加载世界:"+level);
                }
            }else{
                this.getLogger().warning("[RankingListLoader] 检测到已加载世界:"+level);
            }
            Location location = new Location((Double) map.get("x"), (Double) map.get("y"), (Double) map.get("z"), this.getServer().getLevelByName((String) map.get("level")));
            if(location.getChunk() == null){
                if(!location.getLevel().loadChunk(location.getChunkX(), location.getChunkZ())){
                    this.getLogger().warning("[RankingListLoader] 无法加载区块:"+location.getChunkX()+":"+location.getChunkZ());
                    return;
                }else{
                    this.getLogger().warning("[RankingListLoader] 加载区块:"+location.getChunkX()+":"+location.getChunkZ());
                }
            }else{
                this.getLogger().warning("[RankingListLoader] 检测到已加载区块:"+location.getChunkX()+":"+location.getChunkZ());
            }
            EntityUtils.spawnTextEntity(location, (String) map.get("game_name"), (String) map.get("compared_type"));
        }
    }

    @Override
    public void onDisable() {
        RoomHashMap.keySet().forEach(s -> Arena.delWorld(s));
        EntityUtils.closeAll();
        THREAD_POOL_EXECUTOR.shutdown();
        RoomHashMap.clear();
        playerRoomHashMap.clear();
        gameRecord.clear();
        this.getLogger().info("DGameAPI Disabled!");
    }

    public static boolean saveJsonToCore(String savePath, InputStream stream, int type){
        File file = new File(path + "/" + savePath);
        if(file.exists()){ return true; }
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>() {}.getType(), new GsonAdapter()).create();
        Map<String, Object> mainMap;
        JsonReader reader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8)); //一定要以utf-8读取
        mainMap = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
        Config config = new Config(file, type);
        LinkedHashMap<String, Object> save = new LinkedHashMap<>();
        mainMap.keySet().forEach(key -> save.put(key, mainMap.get(key)));
        config.setAll(save);
        config.save();
        return true;
    }
}
