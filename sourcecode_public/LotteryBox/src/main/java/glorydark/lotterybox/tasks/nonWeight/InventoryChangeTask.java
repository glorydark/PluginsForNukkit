package glorydark.lotterybox.tasks.nonWeight;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockGlassStained;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionAll;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import glorydark.lotterybox.MainClass;
import glorydark.lotterybox.api.CreateFireworkApi;
import glorydark.lotterybox.forms.CreateGui;
import glorydark.lotterybox.tools.*;

import java.util.*;

public class InventoryChangeTask extends Task implements Runnable  {

    private final Map<Integer, Item> inventory;
    private final List<Integer> maxIndex = new ArrayList<>();

    private Integer index = 0;

    private final Player player;

    private final LotteryBox lotteryBox;

    private int ticks;

    private final int maxSpin;

    private final int maxCounts;

    public InventoryChangeTask(Player player, LotteryBox box, Integer spins){
        this.inventory = player.getInventory().getContents();
        this.player = player;
        this.lotteryBox = box;
        this.maxSpin = spins;
        this.maxCounts = lotteryBox.getPrizes().size()*2;
        for(int i = 0; i < spins; i++) {
            BasicTool.changeLotteryPlayTimes(player.getName(), lotteryBox.getName(), 1);
            if(lotteryBox.getBonus(BasicTool.getLotteryPlayTimes(player.getName(), lotteryBox.getName())) != null){
                Bonus bonus = lotteryBox.getBonus(BasicTool.getLotteryPlayTimes(player.getName(), lotteryBox.getName()));
                for(String s: bonus.getConsolecommands()){
                    Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), s.replace("%player%", player.getName()));
                }
                player.getInventory().addItem(bonus.getItems());
                Server.getInstance().broadcastMessage(MainClass.lang.getTranslation("Tips", "BonusBroadcast", player.getName(), lotteryBox.getName(), bonus.getNeedTimes(), bonus.getName()));
            }
            int get = getObtained();
            this.maxIndex.add(maxCounts + get);
            //player.sendMessage((get) + (getPrize(get)==null? "无奖": "有奖"));
        }
        player.getInventory().clearAll();
        player.getInventory().setHeldItemIndex(4);
        player.getInventory().setItem(0, getDisplayItem(index-4, false));
        player.getInventory().setItem(1, getDisplayItem(index-3, false));
        player.getInventory().setItem(2, getDisplayItem(index-2, false));
        player.getInventory().setItem(3, getDisplayItem(index-1, false));
        player.getInventory().setItem(4, getDisplayItem(index, true));
        player.getInventory().setItem(5, getDisplayItem(index+1, false));
        player.getInventory().setItem(6, getDisplayItem(index+2, false));
        player.getInventory().setItem(7, getDisplayItem(index+3, false));
        player.getInventory().setItem(8, getDisplayItem(index+4, false));
        MainClass.playingPlayers.add(player);
    }

    public Integer getObtained(){
        Random random = new Random();
        for(Prize prize: lotteryBox.getPrizes()){
            List<Integer> integers = new ArrayList<>();
            for(int i=0; i<prize.getPossibility(); i++){
                integers.add(Math.abs(random.nextInt())%10000);
            }
            if(integers.contains(Math.abs(random.nextInt())%10000)){
                return lotteryBox.getPrizes().indexOf(prize)*2;
            }
        }
        return random.nextInt(lotteryBox.getPrizes().size())*2 + 1;
    }

    public Prize getPrize(int index){
        if(index%2 == 0){
            return lotteryBox.getPrizes().get(index%maxCounts/2);
        }else{
            return null;
        }
    }

    public Item getDisplayItem(Integer index, boolean isEnchanted){
        index+=maxCounts;
        if(index == (index / 2) * 2){
            Prize prize = lotteryBox.getPrizes().get((index % maxCounts)/2);
            Item item = prize.getDisplayitem().clone();
            if(prize.getShowOriginName()){
                item.setCustomName(prize.getName());
            }
            if(isEnchanted) {
                item.addEnchantment(new EnchantmentProtectionAll());
            }
            return item;
        }else{
            Item item = new BlockGlassStained().toItem();
            item.setCustomName(MainClass.lang.getTranslation("PlayLotteryWindow","BlockAir"));
            if(isEnchanted) {
                item.addEnchantment(new EnchantmentProtectionAll());
            }
            return item;
        }
    }

    @Override
    public void onRun(int i) {
        if(player.isOnline() && !MainClass.banWorlds.contains(player.getLevel().getName()) && MainClass.isWorldAvailable(player.getLevel().getName()) && MainClass.playingPlayers.contains(player)){
            Integer thisMaxIndex = maxIndex.get(0);
            ticks+=1;
            if(maxCounts > 10){
                if(index < 4){
                    if(ticks % 4 != 0) {
                        return;
                    }
                }
                if(index + 4 < thisMaxIndex){
                    if(ticks % 2 != 0) {
                        return;
                    }
                }
            }
            if(index <= thisMaxIndex) {
                player.getInventory().clearAll();
                player.getInventory().setItem(0, getDisplayItem(index-4, false));
                player.getInventory().setItem(1, getDisplayItem(index-3, false));
                player.getInventory().setItem(2, getDisplayItem(index-2, false));
                player.getInventory().setItem(3, getDisplayItem(index-1, false));
                player.getInventory().setItem(4, getDisplayItem(index, true));
                player.getInventory().setItem(5, getDisplayItem(index+1, false));
                player.getInventory().setItem(6, getDisplayItem(index+2, false));
                player.getInventory().setItem(7, getDisplayItem(index+3, false));
                player.getInventory().setItem(8, getDisplayItem(index+4, false));
                player.getInventory().setHeldItemIndex(4);
                Prize prize = getPrize(index);
                if(prize != null) {
                    lotteryBox.addBlockParticle(player, getPrize(index));
                    switch (MainClass.showType) {
                        case "actionbar":
                            player.sendActionBar(MainClass.lang.getTranslation("Tips", "PrizeShow", prize.getName()));
                            break;
                        case "tip":
                            player.sendTip(MainClass.lang.getTranslation("Tips", "PrizeShow", prize.getName()));
                            break;
                        case "popup":
                            player.sendPopup(MainClass.lang.getTranslation("Tips", "PrizeShow", prize.getName()));
                            break;
                    }
                }else{
                    switch (MainClass.showType) {
                        case "actionbar":
                            player.sendActionBar(MainClass.lang.getTranslation("Tips", "PrizeShow", MainClass.lang.getTranslation("PlayLotteryWindow","BlockAir")));
                            break;
                        case "tip":
                            player.sendTip(MainClass.lang.getTranslation("Tips", "PrizeShow", MainClass.lang.getTranslation("PlayLotteryWindow","BlockAir")));
                            break;
                        case "popup":
                            player.sendPopup(MainClass.lang.getTranslation("Tips", "PrizeShow", MainClass.lang.getTranslation("PlayLotteryWindow","BlockAir")));
                            break;
                    }
                }
                player.getLevel().addSound(player.getPosition(), lotteryBox.getSound());
                index++;
            }else{
                player.getInventory().clearAll();
                player.getInventory().setContents(inventory);
                int count = 0;
                for(int index : maxIndex){
                    if(getPrize(index) != null){
                        count+=1;
                    }
                }
                StringBuilder content = new StringBuilder(MainClass.lang.getTranslation("RewardWindow", "Title") + "\n" + MainClass.lang.getTranslation("RewardWindow", "StartText", count));
                for(int index : maxIndex){
                    Prize prize = getPrize(index);
                    lotteryBox.showEndParticle(player);
                    if(lotteryBox.getSpawnFirework()) {
                        CreateFireworkApi.spawnFirework(player.getPosition(), DyeColor.YELLOW, ItemFirework.FireworkExplosion.ExplosionType.BURST);
                    }
                    if(prize != null) {
                        content.append("\n").append(MainClass.lang.getTranslation("RewardWindow", "PrizeText", prize.getRarity(), prize.getName()));
                        player.getInventory().addItem(prize.getItems());
                        player.sendMessage(MainClass.lang.getTranslation("Tips","DrawEndWithPrize", prize.getName()));
                        for(String s: prize.getConsolecommands()){
                            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), s.replace("%player%", player.getName()));
                        }
                        if(prize.getBroadcast()){
                            Server.getInstance().broadcastMessage(MainClass.lang.getTranslation("Tips", "PrizeBroadcast", player.getName(), prize.getName()));
                        }
                    }else{
                        if(maxSpin == 1) {
                            player.sendMessage(MainClass.lang.getTranslation("Tips", "DrawEndWithoutPrize"));
                        }
                    }
                }
                if(content.toString().equals(MainClass.lang.getTranslation("RewardWindow", "Title") + "\n" + MainClass.lang.getTranslation("RewardWindow", "StartText"))) {
                    content.append("\n").append(MainClass.lang.getTranslation("RewardWindow", "PrizeNone"));
                    CreateGui.showRewardWindow(player, content.toString());
                }else{
                    CreateGui.showRewardWindow(player, content.toString());
                }
                MainClass.playingPlayers.remove(player);
                MainClass.playerLotteryBoxes.remove(player);
                this.cancel();
            }
        }else{
            for(Integer index: maxIndex) {
                Prize prize = getPrize(index);
                if(prize != null){
                    if (player.isOnline() && MainClass.playingPlayers.contains(player)) {
                        player.sendMessage(MainClass.lang.getTranslation("Tips", "DrawEndWithPrize", prize.getName()));
                        player.getInventory().addItem(prize.getItems());
                    } else {
                        saveMessage(MainClass.lang.getTranslation("Tips", "DrawEndWithPrize", prize.getName()));
                        saveItem(prize.getItems());
                    }
                    for (String s : prize.getConsolecommands()) {
                        saveCommand(s);
                    }
                    if (prize.getBroadcast()) {
                        Server.getInstance().broadcastMessage(MainClass.lang.getTranslation("Tips", "PrizeBroadcast", player.getName(), prize.getName()));
                    }
                }
                saveItem(inventory.values().toArray(new Item[0]));
            }
            MainClass.playerLotteryBoxes.remove(player);
            MainClass.playingPlayers.remove(player);
            MainClass.instance.getLogger().warning("Detect ["+player.getName()+"] exit the server, server will retry to give it in his or her next join");
            this.cancel();
        }
    }

    private void saveItem(Item[] items){
        if(!MainClass.save_bag_enabled) {
            return;
        }
        Config config = new Config(MainClass.path+"/cache.yml", Config.YAML);

        List<String> stringList = new ArrayList<>(config.getStringList(player.getName()+".items"));
        for(Item item: items) {
            stringList.add(Inventory.saveItemToString(item));
        }
        config.set(player.getName()+".items", stringList);
        config.save();
    }

    private void saveCommand(String command){
        Config config = new Config(MainClass.path+"/cache.yml", Config.YAML);

        List<String> stringList = new ArrayList<>(config.getStringList(player.getName()+".commands"));
        stringList.add(command);
        config.set(player.getName()+".commands", stringList);
        config.save();
    }

    private void saveMessage(String message){
        Config config = new Config(MainClass.path+"/cache.yml", Config.YAML);

        List<String> stringList = new ArrayList<>(config.getStringList(player.getName()+".messages"));
        stringList.add(message);
        config.set(player.getName()+".messages", stringList);
        config.save();
    }
}
