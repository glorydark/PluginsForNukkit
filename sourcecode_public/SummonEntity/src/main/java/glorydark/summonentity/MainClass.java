package glorydark.summonentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainClass extends PluginBase {

    public static MainClass instance;
    public static List<DefaultEntity> entityList = new ArrayList<>();

    public static HashMap<String, Skin> texturesList = new HashMap<>();

    public static String path = "";
    public static HashMap<String, List<Skin>> variedSkinList = new HashMap<>();

    @Override
    public void onLoad() {
        this.getLogger().info(TextFormat.GREEN+"SummonEntity 加载中");
    }

    @Override
    public void onEnable() {
        path = this.getDataFolder().getPath();
        instance = this;
        File file = new File(path+"/skins/");
        File file1 = new File(path+"/entities/");
        if(!file.exists()){
            file.mkdirs();
        }
        if(!file1.exists()){
            file1.mkdirs();
        }
        this.getLogger().info(TextFormat.GREEN+"SummonEntity 加载完成");
        this.getServer().getCommandMap().register("", new SummonEntityCommand("summonentity"));
        this.getServer().getPluginManager().registerEvents(new EventListeners(), this);
        Entity.registerEntity("DefaultEntity", DefaultEntity.class);
        try {
            loadSkins();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getLogger().alert("皮肤完成加载！");
        this.getServer().getScheduler().scheduleRepeatingTask(()->{
            for(DefaultEntity e: MainClass.entityList){
                if(!e.getOwner().isOnline() || e.getOwner() == null){
                    e.kill();
                    e.close();
                    continue;
                }
                if(e.isFollow()) {
                    Player player = e.getOwner();
                    if (e.getLevel() != player.getLevel()) {
                        e.setLevel(player.level);
                    }
                    MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
                    pk.eid = e.getId();
                    pk.x = player.getX();
                    pk.y = player.getY() + e.getHeight();
                    pk.z = player.getZ();
                    pk.yaw = player.getYaw();
                    pk.headYaw = player.getHeadYaw();
                    pk.pitch = player.getPitch();
                    //pk.teleport = true;
                    for (Player player1 : Server.getInstance().getOnlinePlayers().values()) {
                        player1.dataPacket(pk);
                    }
                    e.updateMovement();
                }
            }
        }, 1);
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.RED+"SummonEntity 卸载中");
        for(Entity e: entityList){
            e.kill();
            e.close();
        }
    }

    public void loadSkins() throws IOException {
        File folder = new File(this.getDataFolder()+"/skins");
        if(!folder.exists()){
            if(!folder.mkdirs()){
                this.getLogger().info("Fail to create skins folder!");
            }
            return;
        }
        for(File file: folder.listFiles()){
            if(file.isDirectory()){
                File json = new File(file.getPath()+"/skin.json");
                File texture = new File(file.getPath()+"/skin.png");
                if(json.exists() && texture.exists()){
                    Skin skin = createSkin(file);
                    if(skin.isValid()) {
                        this.getLogger().info(file.getName() + "皮肤加载成功");
                        texturesList.put(file.getName(),skin);
                        loadVariedSkins(file, skin);
                    }else{
                        this.getLogger().info(file.getName() + "皮肤加载失败");
                        return;
                    }
                }
            }
        }
    }

    public Skin createSkin(File dic) throws IOException {
        File json = new File(dic.getPath()+"/skin.json");
        File texture = new File(dic.getPath()+"/skin.png");
        Skin skin = new Skin();
        if(texture.exists()){
            skin.setSkinId("SummonEntity"+dic.getName());
            skin.setSkinData(ImageIO.read(texture));
        }else{
            return new Skin();
        }
        if(json.exists()){
            String geometryName = "default";
            for(String s:new Config(json, Config.JSON).getKeys(false)){
                if(s.startsWith("geometry")){
                    geometryName = s;
                }
            }
            //dealWithPicture(texture);  //test
            skin.setSkinResourcePatch("{\"geometry\":{\"default\":\""+geometryName+"\"}}");
            skin.generateSkinId("SummonEntity"+dic.getName());
            skin.setGeometryName(geometryName);
            skin.setGeometryData(readFile(json));
        }
        skin.setTrusted(true);
        return skin;
    }

    public void loadVariedSkins(File path, Skin skin) throws IOException {
        File variedDic = new File(path.getPath()+"/varied");
        if(variedDic.exists()){
            List<Skin> variedSkin = new ArrayList<>();
            for(File file1: variedDic.listFiles()){
                if(ImageIO.read(file1) != null) {
                    Skin save = createSkin(path);
                    save.setSkinData(ImageIO.read(file1));
                    skin.generateSkinId(file1.getName());
                    variedSkin.add(save);
                    //dealWithPicture(file1);  //test
                    if(save.isValid()) {
                        this.getLogger().info(path.getName() + "的变化皮肤" + file1.getName().split("\\.")[0] + "加载成功");
                        return;
                    }
                }else{
                    this.getLogger().info(path.getName() + "的变化皮肤"+ file1.getName().split("\\.")[0]+"加载失败");
                }
            }
            variedSkinList.put(path.getName(), variedSkin);
        }
        return;
    }

    public String readFile(File file){
        String content = "";
        try{
            content = Utils.readFile(file);
        }catch (IOException e){
            e.printStackTrace();
        }
        return content;
    }

    public static void summonEntity(Player player, String id){
        File file1 = new File(path+"/entities/"+id+".yml");
        if(file1.exists()){
            Config entityCfg = new Config(file1,Config.YAML);
            String name = entityCfg.getString("name","default");
            int health = entityCfg.getInt("health", 20);
            //Double damage = entityCfg.getDouble("damage", 20.0);
            Double damage = 0.0;
            //Double defense = entityCfg.getDouble("defense", 20.0);
            Double defense = 0.0;
            Boolean follow = entityCfg.getBoolean("follow", true);
            //Boolean attack = entityCfg.getBoolean("attack", true);
            Boolean attack = true;
            //Boolean spin = entityCfg.getBoolean("spin", true);
            Boolean spin = false;
            Boolean varied = entityCfg.getBoolean("model.varied", false);
            String skinFile = entityCfg.getString("model.skin", "default");
            int interval = entityCfg.getInt("model.interval", 20);
            double height = entityCfg.getDouble("model.height", 2.0);
            //Double speed = entityCfg.getDouble("speed", 1.0);
            Double speed = 1.0;
            int duration = entityCfg.getInt("duration", 20);
            double scale = entityCfg.getDouble("model.scale", 1.0);
            double x = entityCfg.getDouble("delta.x", 0.0);
            double y = entityCfg.getDouble("delta.y", 0.0);
            double z = entityCfg.getDouble("delta.z", 0.0);
            Position pos = player.getPosition();
            CompoundTag tag = Entity.getDefaultNBT(new Vector3(pos.x,pos.y,pos.z));
            Skin skin = MainClass.texturesList.getOrDefault(skinFile, null);
            if(skin == null){
                return;
            }
            tag.putCompound("Skin",new CompoundTag()
                    .putByteArray("Data", skin.getSkinData().data)
                    .putString("ModelId", skin.getSkinId()));
            DefaultEntity entity = new DefaultEntity(pos.getChunk(), tag, pos);
            entity.setSkin(skin);
            entity.setVaried(varied);
            entity.setChangeInterval(interval);
            //battle
            entity.setAttack(attack);
            entity.setDamage(damage);
            entity.setDefense(defense);
            entity.setHealth(health);
            entity.setMaxHealth(health);
            entity.setSpeed(speed);
            //setting
            entity.setOwner(player);
            entity.setNameTag(name);
            entity.setFollow(follow);
            entity.setSpin(spin);
            pos.x+=x;
            pos.y+=y;
            pos.z+=z;
            //pos.setY(pos.y+=height);
            entity.setScale((float) scale);
            entity.setPosition(pos);
            entity.spawnToAll();
            MainClass.entityList.add(entity);
            if(entity.isVaried()){
                if(variedSkinList.getOrDefault(skinFile, null) != null) {
                    Server.getInstance().getScheduler().scheduleRepeatingTask(new ChangeSkinTask(instance, entity, duration, skinFile), 1);
                }else{
                    Server.getInstance().getScheduler().scheduleDelayedTask(()->{
                        if(entity.isAlive()) {
                            MainClass.entityList.remove(entity);
                            entity.kill();
                            entity.close();
                        }else{
                            MainClass.entityList.remove(entity);
                            entity.close();
                        }
                    }, duration*20);
                }
            }else{
                Server.getInstance().getScheduler().scheduleDelayedTask(()->{
                    if(entity.isAlive()) {
                        MainClass.entityList.remove(entity);
                        entity.kill();
                        entity.close();
                    }else{
                        MainClass.entityList.remove(entity);
                        entity.close();
                    }
                }, duration*20);
            }
        }
    }
}
