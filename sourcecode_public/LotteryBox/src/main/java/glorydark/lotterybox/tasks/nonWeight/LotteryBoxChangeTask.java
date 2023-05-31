package glorydark.lotterybox.tasks.nonWeight;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionAll;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import glorydark.lotterybox.MainClass;
import glorydark.lotterybox.api.CreateFireworkApi;
import glorydark.lotterybox.event.LotteryForceCloseEvent;
import glorydark.lotterybox.tools.*;

import java.util.*;

public class LotteryBoxChangeTask extends Task implements Runnable {
    private Map<Integer, Item> inventory;
    private final EntityMinecartChest chest;
    private final List<Integer> maxIndex = new ArrayList<>();

    private Integer index = 0;

    private final Player player;

    private final LotteryBox lotteryBox;

    private final List<Integer> allowIndex;

    private int ticks;
    private int spin = 1;

    private final int maxSpin;

    public LotteryBoxChangeTask(EntityMinecartChest chest, Player player, LotteryBox box, Integer spins){
        this.chest = chest;
        this.inventory = this.chest.getInventory().getContents();
        this.player = player;
        this.lotteryBox = box;
        Integer[] arr = new Integer[]{0,1,2,3,5,6,7,8,9,10,11,15,16,17,18,19,20,21,23,24,25,26};
        allowIndex = Arrays.asList(arr);
        for(int i = 0; i < spins; i++) {
            if(spins == 1) {
                this.maxIndex.add(getMaxIndex());
            }else{
                this.maxIndex.add(22 + getMaxIndex());
            }
            BasicTool.changeLotteryPlayTimes(player.getName(), lotteryBox.getName(), 1);
            if(lotteryBox.getBonus(BasicTool.getLotteryPlayTimes(player.getName(), lotteryBox.getName())) != null){
                Bonus bonus = lotteryBox.getBonus(BasicTool.getLotteryPlayTimes(player.getName(), lotteryBox.getName()));
                for(String s: bonus.getConsolecommands()){
                    Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), s.replace("%player%", player.getName()));
                }
                player.getInventory().addItem(bonus.getItems());
                Server.getInstance().broadcastMessage(MainClass.lang.getTranslation("Tips", "BonusBroadcast", player.getName(), lotteryBox.getName(), bonus.getNeedTimes(), bonus.getName()));
            }
        }
        this.maxSpin = spins;
    }

    public Integer getMaxIndex(){
        Random random = new Random();
        for(Prize prize: lotteryBox.getPrizes()){
            List<Integer> integers = new ArrayList<>();
            for(int i=0; i<prize.getPossibility(); i++){
                integers.add(Math.abs(random.nextInt())%10000);
            }
            if(integers.contains(Math.abs(random.nextInt())%10000)){
                return lotteryBox.getPrizes().indexOf(prize);
            }
        }
        List<Integer> absent = new ArrayList<>();
        for(int i = lotteryBox.getPrizes().size(); i<22; i++){
            absent.add(allowIndex.get(i));
        }
        if(absent.size() > 0) {
            return absent.get(random.nextInt(absent.size()));
        }else{
            return random.nextInt(22);
        }
    }

    @Override
    public void onRun(int i) {
        ticks+=1;
        if(player.isOnline() && !chest.closed && chest.getInventory().getViewers().contains(player) && !MainClass.banWorlds.contains(player.getLevel().getName()) && MainClass.isWorldAvailable(player.getLevel().getName())){
            Integer thisMaxIndex = maxIndex.get(spin-1);
            if(index <= thisMaxIndex) {
                if(thisMaxIndex > 10) {
                    if (index < 2 || index + 2 >= thisMaxIndex) {
                        if (ticks % 4 != 0) {
                            return;
                        }
                    } else {
                        if (index < 5 || index + 5 >= thisMaxIndex) {
                            if (ticks % 2 != 0) {
                                return;
                            }
                        }
                    }
                }
                Integer realIndex = allowIndex.get(index % 22);
                chest.getInventory().setContents(inventory);
                Item item = chest.getInventory().getItem(realIndex);
                item.addEnchantment(new EnchantmentProtectionAll());
                chest.getInventory().setItem(realIndex, item);
                player.getLevel().addSound(player.getPosition(), lotteryBox.getSound());
                if(index % 22 < lotteryBox.getPrizes().size()) {
                    lotteryBox.addBlockParticle(player, lotteryBox.getPrizes().get(index % 22));
                }
                index++;
            }else{
                Item item = inventory.get(allowIndex.get(thisMaxIndex % 22));
                Item[] give;
                List<Prize> prizes = lotteryBox.getPrizes();
                Integer realIndex = allowIndex.get(thisMaxIndex % 22);
                Prize prize = null;
                if(realIndex < prizes.size()){
                    give = prizes.get(realIndex).getItems();
                    prize = prizes.get(realIndex);
                }else{
                    give = new Item[]{new BlockAir().toItem()};
                }
                item.addEnchantment(new EnchantmentProtectionAll());
                chest.getInventory().setItem(realIndex, item);
                lotteryBox.showEndParticle(player);
                if(lotteryBox.getSpawnFirework()) {
                    CreateFireworkApi.spawnFirework(player.getPosition(), DyeColor.YELLOW, ItemFirework.FireworkExplosion.ExplosionType.BURST);
                }
                if(!item.getCustomName().equals(MainClass.lang.getTranslation("PlayLotteryWindow","BlockAir")) && prize != null) {
                    player.getInventory().addItem(give);
                    player.sendMessage(MainClass.lang.getTranslation("Tips","DrawEndWithPrize").replace("%s", Objects.requireNonNull(prize).getName()));
                    for(String s: prize.getConsolecommands()){
                        Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), s.replace("%player%", player.getName()));
                    }
                    if(prize.getBroadcast()){
                        Server.getInstance().broadcastMessage(MainClass.lang.getTranslation("Tips", "PrizeBroadcast").replaceFirst("%s", player.getName()).replaceFirst("%s1", prize.getName()));
                    }
                }else{
                    player.sendMessage(MainClass.lang.getTranslation("Tips","DrawEndWithoutPrize"));
                }
                if(spin < maxSpin - 1){
                    chest.getInventory().setContents(inventory);
                    Item enchant = chest.getInventory().getItem(realIndex);
                    enchant.addEnchantment(new EnchantmentProtectionAll());
                    chest.getInventory().setItem(realIndex, enchant);
                    inventory = chest.getInventory().getContents();
                    index = 0;
                    spin++;
                }else{
                    MainClass.playerLotteryBoxes.remove(player);
                    MainClass.playingPlayers.remove(player);
                    player.removeWindow(chest.getInventory());
                    Server.getInstance().getPluginManager().callEvent(new LotteryForceCloseEvent(player));
                    this.cancel();
                }
            }
        }else{
            for(int t = 1; t<spin; t++){
                maxIndex.remove(0);
            }
            for(Integer thisMaxIndex: maxIndex) {
                List<Prize> prizes = lotteryBox.getPrizes();
                Integer realIndex = allowIndex.get(thisMaxIndex % 22);
                if (realIndex < prizes.size()) {
                    Prize prize = prizes.get(realIndex);
                    if (player.isOnline()) {
                        player.sendMessage(MainClass.lang.getTranslation("Tips", "DrawEndWithPrize").replace("%s", prize.getName()));
                        player.getInventory().addItem(prize.getItems());
                    } else {
                        saveItem(prize.getItems());
                        saveMessage(MainClass.lang.getTranslation("Tips", "DrawEndWithPrize").replace("%s", prize.getName()));
                    }
                    for (String s : prize.getConsolecommands()) {
                        saveCommand(s);
                    }
                    if (prize.getBroadcast()) {
                        Server.getInstance().broadcastMessage(MainClass.lang.getTranslation("Tips", "PrizeBroadcast").replaceFirst("%s", player.getName()).replaceFirst("%s1", prize.getName()));
                    }
                } else {
                    Item[] give = new Item[]{new BlockAir().toItem()};
                    if (player.isOnline()) {
                        player.sendMessage(MainClass.lang.getTranslation("Tips", "DrawEndWithoutPrize"));
                        player.getInventory().addItem(give);
                    }
                }
            }
            MainClass.instance.getLogger().warning("Detect ["+player.getName()+"] exit the server, server will retry to give it in his or her next join");
            player.removeWindow(chest.getInventory());
            MainClass.playerLotteryBoxes.remove(player);
            MainClass.playingPlayers.remove(player);
            Server.getInstance().getPluginManager().callEvent(new LotteryForceCloseEvent(player));
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
