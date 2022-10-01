package gameapi.scripts;

import cn.nukkit.block.BlockAir;
import cn.nukkit.item.Item;
import gameapi.GameAPI;

import java.util.HashMap;

public class GameItemBase {
    public static HashMap<String, Item> roomItemHashMap = new HashMap<>();

    public static Item getRoomItem(String identifier){
        return roomItemHashMap.getOrDefault(identifier, new BlockAir().toItem());
    }

    public static boolean registerRoomItem(String identifier, Item item){
        if(roomItemHashMap.containsKey(identifier)){
            GameAPI.plugin.getLogger().warning("注册房间物品已存在，identifier："+identifier+"，物品data: "+item.toString());
            return false;
        }else{
            roomItemHashMap.put(identifier, item);
            GameAPI.plugin.getLogger().warning("成功注册房间物品，identifier："+identifier+"，物品data: "+item.toString());
            return true;
        }
    }
}
