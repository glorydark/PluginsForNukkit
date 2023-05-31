package glorydark.SimpleDialogues.dialogue;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.Dialogue;
import glorydark.SimpleDialogues.MainClass;
import glorydark.SimpleDialogues.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DialogueMain {
    public static void loadAll(){
        File path = new File(MainClass.path+"/dialogues/");
        if(path.exists()) {
            File[] files = path.listFiles();
            if (files != null && files.length > 0) {
                MainClass.dialoguesMap = new HashMap<>();
                HashMap<String, Dialogue> dialoguesMap = new HashMap<>();
                for (File file : files) {
                    Config config = new Config(file, Config.YAML);
                    Dialogue dialogue = Dialogue.parseConfig(config);
                    if (dialogue != null) {
                        String name = file.getName().split("\\.")[0];
                        dialoguesMap.put(name, dialogue);
                        MainClass.plugin.getLogger().info(TextFormat.GREEN + "Dialogue: " + name + " loaded");
                    } else {
                        MainClass.plugin.getLogger().alert("Dialogue File: " + file.getName() + " can not be loaded");
                    }
                }
                MainClass.dialoguesMap = dialoguesMap;
                MainClass.plugin.getLogger().info(TextFormat.GREEN + "All the dialogue files have been loaded!");
            } else {
                MainClass.plugin.getLogger().info(TextFormat.GREEN + "No dialogue file has been loaded!");
            }
        }
    }

    public static void saveAll(){
        for(String s: MainClass.dialoguesMap.keySet()){
            Config config = new Config(MainClass.path+"/dialogues/"+s+".yml",Config.YAML);
            List<String> stringList = config.getStringList("完成玩家");
            if(stringList != null){
                config.set("完成玩家", MainClass.dialoguesMap.get(s).getFinishPlayers());
                config.save();
            }
        }
        MainClass.plugin.getLogger().info(TextFormat.GREEN + "Dialogue Records have been saved!");
    }

    public static Dialogue getDialogue(String name){
        return MainClass.dialoguesMap.getOrDefault(name, null);
    }

    public static void showPlayerDialogue(Player player, String dialogueName) {
        Dialogue dialogue = MainClass.dialoguesMap.get(dialogueName);
        if (dialogue != null) {
                if (!MainClass.inDialogues.containsKey(player)) {
                    if(dialogue.getFinishPlayers().contains(player.getName())){
                        if(dialogue.isOnce()) {
                            List<String> strings = dialogue.getPlayerMustExecuteCommands();
                            if(strings.size() > 0) {
                                Tools.playerExecuteCommands(player, strings);
                            }
                            player.sendMessage(TextFormat.RED + "对话仅限播放一次！");
                            return;
                        }
                    }
                    if(checkRequirement(player, dialogue.getRequirements())) {
                        if(checkItemIsPossess(player, dialogue.getNeedItem(), false) && checkItemIsPossess(player, dialogue.getCostItem(), true)) {
                            MainClass.inDialogues.put(player, dialogue);
                            MainClass.plugin.getServer().getScheduler().scheduleRepeatingTask(new DialoguePlayTask(player, dialogue), 1);
                        }else{
                            player.sendMessage(TextFormat.RED + "您尚未拥有所有所需物品！");
                        }
                    }else{
                        player.sendMessage(TextFormat.RED + "您目前不满足此对话开启条件！");
                    }
                } else {
                    player.sendMessage(TextFormat.RED + "您正处于对话中，无法加入其他对话！");
                }
        }else{
            player.sendMessage(TextFormat.RED+"对话不存在！");
        }
    }

    public static Boolean checkRequirement(Player player, List<String> requirements){
        if(requirements != null){
            for(String requirement : requirements){
                if(MainClass.dialoguesMap.containsKey(requirement)){
                    Dialogue dialogue = MainClass.dialoguesMap.get(requirement);
                    if(!dialogue.getFinishPlayers().contains(player.getName())){
                        player.sendMessage(TextFormat.RED + "您需要先完成对话 [ "+dialogue.getDialogueName()+" ]！");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static Boolean checkItemIsPossess(Player player, List<String> needItem, Boolean isReduce){
        if (needItem.size() >= 1) {
            List<Item> items = new ArrayList<>();
            List<Item> costItems = new ArrayList<>();
            for (String s : needItem) {
                String[] s1 = s.split(":");
                Item item = new Item(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]), Integer.parseInt(s1[2]));
                if(!s1[3].equals("null")){
                    byte[] tag = hexStringToBytes(s1[3]);
                    item.setCompoundTag(tag);
                }
                if(!player.getInventory().contains(item)){
                    for(Item costItem: costItems){
                        player.getInventory().addItem(costItem);
                    }
                    return false;
                }else{
                    Integer count = 0; //玩家拥有的
                    for(Item item1 : player.getInventory().getContents().values()){
                        if(item.getId() == item1.getId() && item.getDamage() == item1.getDamage() && Arrays.equals(item.getCompoundTag(), item1.getCompoundTag())){
                            count+=item1.getCount();
                        }
                    }
                    if(count >= item.getCount()){
                        if(isReduce) {
                            Item save = item;
                            save.setCount(count);
                            items.add(save);
                            //Record
                            costItems.add(item); //保存花费物品
                            player.getInventory().remove(item);
                        }
                    }else{
                        for(Item costItem: costItems){
                            player.getInventory().addItem(costItem);
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

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
}
