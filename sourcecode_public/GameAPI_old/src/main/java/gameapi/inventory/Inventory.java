package gameapi.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from SmallAsWater's method
 * Glorydark added some changes
 */

public class Inventory {

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static void saveBag(Player gamePlayer) {
        if(GameAPI.saveBag) {
            List<String> bag = new ArrayList<>();
            for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
                Item item = gamePlayer.getInventory().getItem(i);
                bag.add(getItemString(item));
            }
            savePlayerBagConfig(gamePlayer, bag);
        }
        gamePlayer.getInventory().clearAll();
    }

    public static String getItemString(Item item){
        String nbt = "null";
        if (item.hasCompoundTag()) {
            nbt = bytesToHexString(item.getCompoundTag());
        }
        return item.getId() + ":" + item.getDamage() + ":" + item.getCount() + ":" + nbt;
    }

    public static void loadBag(Player gamePlayer) {
        if(GameAPI.saveBag) {
            List<String> bag = getPlayerBagConfig(gamePlayer);
            if (bag != null && bag.size() > 0) {
                gamePlayer.getInventory().clearAll();
                for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
                    String[] a = bag.get(i).split(":");
                    Item item = new Item(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
                    if (a.length > 3 && !a[3].equals("null")) {
                        CompoundTag tag = Item.parseCompoundTag(hexStringToBytes(a[3]));
                        item.setNamedTag(tag);
                    }
                    gamePlayer.getInventory().setItem(i, item);
                }
                removePlayerBagConfig(gamePlayer);
            }
        }
    }

    public static List<String> getPlayerBagConfig(Player player){
        Config config = new Config(GameAPI.path+"/inventory_caches/"+player.getName()+".yml",Config.YAML);
        if(!config.exists("bagCache")){ return null;}
        return new ArrayList<>(config.getStringList("bagCache"));
    }

    public static void savePlayerBagConfig(Player player,List<String> content){
        Config config = new Config(GameAPI.path+"/inventory_caches/"+player.getName()+".yml",Config.YAML);
        config.set("bagCache",content);
        config.save();
    }

    public static void removePlayerBagConfig(Player player){
        Config config = new Config(GameAPI.path+"/inventory_caches/"+player.getName()+".yml",Config.YAML);
        config.remove("bagCache");
        config.save();
    }
}
