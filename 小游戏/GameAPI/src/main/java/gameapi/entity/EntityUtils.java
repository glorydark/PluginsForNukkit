package gameapi.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityUtils {

    public static Set<TextEntity> entityList = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void closeAll() {
        entityList.forEach(textEntity -> {
            if(!textEntity.closed) {
                textEntity.kill();
                textEntity.close();
            }
        });
    }

    public static void spawnTextEntity(Location location, String gameName, String comparedKey){
        TextEntity entity = new RankingListEntity(location.getChunk(), location, gameName, comparedKey, Entity.getDefaultNBT(new Vector3(location.x, location.y, location.z)));
        GameAPI.plugin.getLogger().info(entity.getNameTag());
        entity.setImmobile(true);
        entity.spawnToAll();
        entityList.add(entity);
    }

    public static void spawnTextEntity(Position position, String text){
        TextEntity entity = new TextEntity(position.getChunk(), position, text, Entity.getDefaultNBT(new Vector3(position.x, position.y, position.z)));
        entity.setImmobile(true);
        entity.spawnToAll();
        entityList.add(entity);
    }

    public static void addRankingList(Player player, String gameName, String comparedType){
        Config config = new Config(GameAPI.path+"/rankings.yml");
        List<Map<String, Object>> maps = (List<Map<String, Object>>) config.get("list");
        Map<String, Object> add = new LinkedHashMap<>();
        add.put("game_name", gameName);
        add.put("compared_type", comparedType);
        add.put("x", player.getX());
        add.put("y", player.getY());
        add.put("z", player.getZ());
        add.put("level", player.getLevel().getName());
        add.put("title", "测试排行榜");
        add.put("format", "[%rank%] %player%: %score%");
        maps.add(add);
        config.set("list", maps);
        config.save();
        spawnTextEntity(player.getLocation(), gameName, comparedType);
    }
}
