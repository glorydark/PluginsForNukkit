package glorydark.CloudInventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.List;

public class CloudBag {

    public static List<String> getPlayerBag(Player player){
        Config config = new Config(MainClass.plugin.getDataFolder() + "/players/" + player.getName() + ".yml");
        return new ArrayList<>(config.getStringList("Inventory"));
    }

    public static void getPlayerBagItem(Integer index,Player player){
        Config config = new Config(MainClass.plugin.getDataFolder()+"/players/"+player.getName()+".yml");
        List<String> arrayList = new ArrayList<>(config.getStringList("Inventory"));
        if (index <= 0 || index > arrayList.size()) {
            player.sendMessage(Lang.getTranslation("Message.PickItemFailedForIndexInputWrong"));
            return;
        }
        if (index <= arrayList.size()) {
            Item add = parseString(arrayList.get(index - 1));
            if (player.getInventory().canAddItem(add)) {
                player.getInventory().addItem(add);
                arrayList.remove(arrayList.get(index - 1));
                config.set("Inventory", arrayList);
                config.save();
                player.sendMessage(Lang.getTranslation("Message.PickItemSuccessfully").replace("%ItemName%",add.getName()+"*"+add.getCount()));
            } else {
                player.sendMessage(Lang.getTranslation("Message.InventoryFull"));
            }
        }
    }

    public static Boolean addItemToCloud(Player player,Item item){
        if(item.getId() == 0){ player.sendMessage(Lang.getTranslation("Message.ForbidAirBeingUpload")); return false; }
        Config config = new Config(MainClass.plugin.getDataFolder()+"/players/"+player.getName()+".yml");
        if(!config.exists("Inventory")){
            config.set("Inventory",new ArrayList<>());
            config.save();
        }
        ArrayList<String> arrayList = new ArrayList<>(config.getStringList("Inventory"));
        if (arrayList.size() >= getMaxCloudSlot(player)) {
            return false;
        }
        if(item.hasCompoundTag()) {
            arrayList.add(item.getId() + ":" + item.getDamage() + ":" + item.getCount() + ":" + Inventory.bytesToHexString(item.getCompoundTag()));
        }else{
            arrayList.add(item.getId() + ":" + item.getDamage() + ":" + item.getCount());
        }
        config.set("Inventory", arrayList);
        config.save();
        return true;
    }

    public static void uploadHeldItemToCloud(Player player){
        if(player.getInventory().getItemInHand().getId() == 0){ player.sendMessage(Lang.getTranslation("Message.ForbidAirBeingUpload")); return; }
        Config config = new Config(MainClass.plugin.getDataFolder()+"/players/"+player.getName()+".yml");
        Item item = player.getInventory().getItemInHand();
        if(!config.exists("Inventory")){
            config.set("Inventory",new ArrayList<>());
            config.save();
        }
        ArrayList<String> arrayList = new ArrayList<>(config.getStringList("Inventory"));
        if (arrayList.size() >= getMaxCloudSlot(player)) {
            player.sendMessage(Lang.getTranslation("Message.CloudInventoryFull"));
            return;
        }
        if(item.hasCompoundTag()) {
            arrayList.add(item.getId() + ":" + item.getDamage() + ":" + item.getCount() + ":" + Inventory.bytesToHexString(item.getCompoundTag()));
        }else{
            arrayList.add(item.getId() + ":" + item.getDamage() + ":" + item.getCount());
        }
        config.set("Inventory", arrayList);
        config.save();
        player.sendMessage(Lang.getTranslation("Message.UploadItemSuccessfully").replace("%ItemName%",item.getName()+"*"+item.getCount()));
        player.getInventory().setItemInHand(new ItemBlock(new BlockAir(), null, 0));
    }

    public static Item parseString(String string){
        if(string.split(":").length > 2){
            String[] strings = string.split(":");
            Item item = Item.get(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),Integer.parseInt(strings[2]));
            if(strings.length>3) {
                item.setCompoundTag(Inventory.hexStringToBytes(strings[3]));
            }
            return item;
        }
        return null;
    }

    public static Integer getMaxCloudSlot(Player p){
        Config config = new Config(MainClass.plugin.getDataFolder()+"/players/"+p.getName()+".yml");
        if(config.exists("InventorySlots")){
            return config.getInt("InventorySlots");
        }else{
            config.set("InventorySlots", MainClass.defaultMaxSlot);
            config.save();
            return MainClass.defaultMaxSlot;
        }
    }
}
