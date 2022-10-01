package glorydark.treasurehunt;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBed;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ResourcePacksInfoPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import com.sun.istack.internal.NotNull;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainClass extends PluginBase implements Listener {
    public static HashMap<String, Skin> treasureSkin = new HashMap<>();
    public Integer maxCollectCount = 30;
    private List<String> rewardCommands;

    public static boolean isKnockback; //

    public static Config langConfig;

    public static String path;

    public static HashMap<String, Treasure> treasureEntities = new HashMap<>();
    private List<String> messages;

    @Override
    public void onLoad() {
        this.getLogger().info(TextFormat.GREEN+"TreasureHunt onLoad");
    }

    @Override
    public void onEnable() {
        path = this.getDataFolder().getPath();
        this.saveResource("config.yml", false);
        this.saveResource("treasures.yml", false);
        this.saveResource("skins/default/skin.png", false);
        this.saveResource("skins/default/skin.json", false);
        this.saveResource("lang.properties", false);
        Config config = new Config(path+"/config.yml", Config.YAML);
        this.maxCollectCount = config.getInt("maxCollectCount",30);
        this.rewardCommands = new ArrayList<>(config.getStringList("commands"));
        this.messages = new ArrayList<>(config.getStringList("messages"));
        isKnockback = config.getBoolean("isKnockback", true);
        langConfig = new Config(path+"/lang.properties",Config.PROPERTIES);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getCommandMap().register("", new BaseCommand("treasurehunt"));
        this.loadSkins();
        this.spawnAllTreasures();
        this.getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            for (String pos : treasureEntities.keySet()) {
                Treasure treasure = treasureEntities.get(pos);
                for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                    if(treasure.getEntity() == null){
                        if(this.getServer().getLevelByName(treasure.getPosition().split(":")[3]).getPlayers().values().size()>0) {
                            treasure.spawnByStringPos();
                        }
                        continue;
                    }
                    if(!treasure.getEntity().hasSpawned().contains(player)){
                        treasure.getEntity().spawnTo(player);
                    }
                    if(treasure.isParticleMarked()) {
                        treasure.showParticle(player);
                    }
                }
            }
        }, 40);
        this.getLogger().info(TextFormat.GREEN+"TreasureHunt enabled");
    }

    @Override
    public void onDisable() {
        for(Treasure treasure: treasureEntities.values()){
            Entity e = treasure.getEntity();
            if(e != null && !e.isClosed()) {
                e.kill();
                e.close();
            }
        }
        treasureEntities = new HashMap<>();
        this.getLogger().info(TextFormat.GREEN+"TreasureHunt onDisable");
    }

    public void loadSkins(){
        File skinDir = new File(path + "/skins/");
        for(File file: Objects.requireNonNull(skinDir.listFiles())){
            this.getLogger().warning(skinDir.getPath()+"/"+file.getName()+"/skin.png");
            loadSkin(file.getName(), new File(skinDir.getPath()+"/"+file.getName()+"/skin.png"), new File(skinDir+"/"+file.getName()+"/skin.json"));
        }
    }

    //from RsNPCX and updating the skin loader
    public void loadSkin(String skinName, File skinDataFile, File skinJsonFile){
        if (skinDataFile.exists()) {
            Skin skin = new Skin();
            skin.setSkinId(skinName);
            try {
                skin.setSkinData(ImageIO.read(skinDataFile));
            } catch (Exception e) {
                this.getLogger().error("皮肤 " + skinName + " 读取错误，请检查图片格式或图片尺寸！", e);
            }

            //如果是4D皮肤
            if (skinJsonFile.exists()) {
                Map<String, Object> skinJson = (new Config(skinJsonFile, Config.JSON)).getAll();
                String geometryName = null;

                String formatVersion = (String) skinJson.getOrDefault("format_version", "1.10.0");
                skin.setGeometryDataEngineVersion(formatVersion); //设置皮肤版本，主流格式有1.16.0,1.12.0(Blockbench新模型),1.10.0(Blockbench Legacy模型),1.8.0
                switch (formatVersion){
                    case "1.16.0":
                    case "1.12.0":
                        geometryName = getGeometryName(skinJsonFile);
                        if(geometryName.equals("nullvalue")){
                            this.getLogger().error("暂不支持1.12.0版本格式的皮肤！请等待更新！");
                        }else{
                            skin.generateSkinId(skinName);
                            skin.setSkinResourcePatch("{\"geometry\":{\"default\":\"" + geometryName + "\"}}");
                            skin.setGeometryName(geometryName);
                            skin.setGeometryData(readFile(skinJsonFile));
                            this.getLogger().info("皮肤 " + skinName + " 读取中");
                        }
                        break;
                    default:
                        this.getLogger().warning("["+skinName+"] 的版本格式为："+formatVersion + "，正在尝试加载！");
                    case "1.10.0":
                    case "1.8.0":
                        for (Map.Entry<String, Object> entry : skinJson.entrySet()) {
                            if (geometryName == null) {
                                if (entry.getKey().startsWith("geometry")) {
                                    geometryName = entry.getKey();
                                }
                            }else {
                                break;
                            }
                        }
                        skin.generateSkinId(skinName);
                        skin.setSkinResourcePatch("{\"geometry\":{\"default\":\"" + geometryName + "\"}}");
                        skin.setGeometryName(geometryName);
                        skin.setGeometryData(readFile(skinJsonFile));
                        break;
                }
            }

            skin.setTrusted(true);

            if (skin.isValid()) {
                treasureSkin.put(skinName, skin);
                this.getLogger().info("皮肤 " + skinName + " 读取完成");
            }else {
                this.getLogger().error("皮肤 " + skinName + " 验证失败，请检查皮肤文件完整性！");
            }
        } else {
            this.getLogger().error("皮肤 " + skinName + " 错误的名称格式，请将皮肤文件命名为 skin.png 模型文件命名为 skin.json");
        }
    }

    public String getGeometryName(File file) {
        Config originGeometry = new Config(file, Config.JSON);
        if (!originGeometry.getString("format_version").equals("1.12.0") && !originGeometry.getString("format_version").equals("1.16.0")) {
            return "nullvalue";
        }
        //先读取minecraft:geometry下面的项目
        List<Map<String, Object>> geometryList = (List<Map<String, Object>>) originGeometry.get("minecraft:geometry");
        //不知道为何这里改成了数组，所以按照示例文件读取第一项
        Map<String, Object> geometryMain = geometryList.get(0);
        //获取description内的所有
        Map<String, Object> descriptions = (Map<String, Object>) geometryMain.get("description");
        return (String) descriptions.getOrDefault("identifier", "geometry.unknown"); //获取identifier
    }

    public static void spawnTreasure(String identifier){
        Config config = new Config(getTreasuresConfigPath(), Config.YAML);
        if(config.getString(identifier+".position", "null").equals("null")){
            return;
        }
        Treasure treasure = new Treasure(identifier
                ,config.getString(identifier+".position", "null"),
                treasureSkin.getOrDefault(config.getString(identifier+".skin", "default"), new Skin()),
                config.getBoolean(identifier+".spin",false)?config.getDouble(identifier+".spinSpeed", 4.0):0d,
                config.getDouble(identifier+".scale", 4.0),
                isKnockback,
                config.getBoolean(identifier+".isParticleMarked",false),
                new ArrayList<>(config.getStringList(identifier+".messages")),
                new ArrayList<>(config.getStringList(identifier+".commands"))
        );
        treasure.spawnByStringPos();
        String[] strings = config.getString(identifier+".position", "null").split(":");
        if(!Server.getInstance().isLevelLoaded(strings[3])){
            if(!Server.getInstance().loadLevel(strings[3])){
                Server.getInstance().getLogger().warning("找不到地图："+strings[3]);
                return;
            }
        }
        Location pos = new Location(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Server.getInstance().getLevelByName(strings[3]));
        treasureEntities.put(pos.getFloorX()+":"+pos.getFloorY()+":"+pos.getFloorZ()+":"+pos.getLevel().getName(), treasure);
    }

    public void spawnAllTreasures(){
        File file = new File(getTreasuresConfigPath());
        if(file.exists()) {
            Config config = new Config(file, Config.YAML);
            for(String key: config.getKeys(false)){
                spawnTreasure(key);
            }
        }
    }

    public String readFile(@NotNull File file) {
        String content = "";
        try {
            content = cn.nukkit.utils.Utils.readFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void createTreasure(Player player, Position position, String identifier){
        String pos = position.getFloorX()+":"+position.getFloorY()+":"+position.getFloorZ()+":"+position.getLevel().getName();
        if(!treasureEntities.containsKey(pos)) {
            spawnTreasure(pos);
            Config config = new Config(path + "/treasures.yml", Config.YAML);
            config.set(identifier+".position", pos);
            config.set(identifier+".commands", new ArrayList<>());
            config.set(identifier+".messages", new ArrayList<>());
            config.set(identifier+".scale", 1.0);
            config.set(identifier+".spin", false);
            config.set(identifier+".spinSpeed", 4.0);
            config.set(identifier+".isParticleMarked", false);
            config.set(identifier+".skin", "default");
            config.save();
            player.sendMessage(translateString("addTreasure_success", pos));
        }else{
            player.sendMessage(translateString("addTreasure_alreadyExist", pos));
        }
    }

    public static void deleteTreasure(Player player, Position position){
        String pos = position.getFloorX()+":"+position.getFloorY()+":"+position.getFloorZ()+":"+position.getLevel().getName();
        if(treasureEntities.containsKey(pos)) {
            treasureEntities.remove(pos);
            Config config = new Config(path + "/treasures.yml", Config.YAML);
            config.remove(treasureEntities.get(pos).getName());
            config.save();
            player.sendMessage(translateString("removeTreasure_success", pos));
        }else{
            player.sendMessage(translateString("removeTreasure_notFound", pos));
        }
    }

    public List<String> getPlayerCollect(String player){
        Config config = new Config(this.getDataFolder().getPath()+"/players/"+player+".yml", Config.YAML);
        return new ArrayList<>(config.getStringList("list"));
    }

    public void setPlayerCollect(String player, List<String> strings){
        Config config = new Config(this.getDataFolder().getPath()+"/players/"+player+".yml", Config.YAML);
        config.set("list", strings);
        config.save();
    }

    public Boolean addPlayerCollect(Player player, String string){
        List<String> strings = getPlayerCollect(player.getName());
        if(!strings.contains(string)) {
            strings.add(string);
            setPlayerCollect(player.getName(), strings);
            if(strings.size() >= this.maxCollectCount){
                if(strings.size() == this.maxCollectCount) {
                    Treasure treasure = treasureEntities.getOrDefault(string, null);
                    if(treasure != null){
                        player.sendMessage(translateString("found_treasure", treasure.getName(), maxCollectCount - strings.size()));
                        for(String cmd : treasure.getCommands()){
                            this.getServer().dispatchCommand(this.getServer().getConsoleSender(), cmd.replace("%player%", player.getName()));
                        }
                        for(String msg : treasure.getMessages()){
                            player.sendMessage(msg.replace("%player%", player.getName()));
                        }
                    }
                    player.sendMessage(translateString("found_all_treasures"));
                    for(String cmd : this.rewardCommands){
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), cmd.replace("%player%", player.getName()));
                    }
                    for(String msg : this.messages){
                        player.sendMessage(msg.replace("%player%", player.getName()));
                    }
                    CreateFireworkApi.spawnFirework(player.getPosition(), DyeColor.YELLOW, ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED);
                }else{
                    player.sendMessage(translateString("already_found_all_treasures"));
                }
            }else {
                Treasure treasure = treasureEntities.getOrDefault(string, null);
                if(treasure != null){
                    player.sendMessage(translateString("found_treasure", treasure.getName(), maxCollectCount - strings.size()));
                    for(String cmd : treasure.getCommands()){
                        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), cmd.replace("%player%", player.getName()));
                    }
                    for(String msg : treasure.getMessages()){
                        player.sendMessage(msg.replace("%player%", player.getName()));
                    }
                }
                player.getLevel().addSound(player.getPosition() ,Sound.NOTE_FLUTE);
            }
            return true;
        }else{
            player.sendMessage(translateString("already_found_treasure"));
            player.getLevel().addSound(player.getPosition() ,Sound.NOTE_BASS);
        }
        return false;
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof TreasureEntity){
            Position position = event.getEntity().getPosition();
            if(!addPlayerCollect((Player) event.getDamager(), position.getFloorX()+":"+position.getFloorY()+":"+position.getFloorZ()+":"+position.getLevel().getName())){
                if(isKnockback) {
                    double deltaX = event.getDamager().x - event.getEntity().x;
                    double deltaZ = event.getDamager().z - event.getEntity().z;
                    knockBack(event.getDamager(), deltaX, deltaZ, 1.0);
                }
            }
            event.setCancelled(true);
        }
    }

    public void knockBack(Entity entity, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (!(f <= 0.0)) {
            f = 1.0 / f;
            Vector3 motion = new Vector3();
            motion.x /= 2.0;
            motion.y /= 2.0;
            motion.z /= 2.0;
            motion.x += x * f * base;
            motion.y += base;
            motion.z += z * f * base;
            if (motion.y > base) {
                motion.y = base;
            }
            entity.setMotion(motion);
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event){
        if(event.getEntity() instanceof TreasureEntity && !(event instanceof EntityDamageByEntityEvent)){
            event.setCancelled(true);
        }
    }

    //by:lt-name CrystalWars
    public static String translateString(String key, Object... params) {
        String string = langConfig.getString(key, "§c Unknown key:" + key);
        if (params != null && params.length > 0) {
            for(int i = 1; i < params.length + 1; ++i) {
                string = string.replace("%" + i + "%", Objects.toString(params[i - 1]));
            }
        }
        return string;
    }

    public static String translateString(String key) {
        return langConfig.getString(key, "§c Unknown key:" + key);
    }

    public static String getTreasuresConfigPath(){
        return path+"/treasures.yml";
    }
}
