package glorydark.lotterybox.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.inventory.CraftingGrid;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.inventory.PlayerUIInventory;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.utils.Config;
import glorydark.lotterybox.MainClass;
import glorydark.lotterybox.event.LotteryForceCloseEvent;
import glorydark.lotterybox.tasks.nonWeight.LotteryBoxChangeTask;
import glorydark.lotterybox.tools.Inventory;
import glorydark.lotterybox.tools.LotteryBox;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class EventListeners implements Listener {
    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof EntityMinecartChest && event.getEntity().namedTag.contains("IsLotteryBox")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
        if(event.getEntity() instanceof EntityMinecartChest && event.getEntity().namedTag.contains("IsLotteryBox")){
            event.setCancelled(true);
        }
    }



    public static void resetBasisWindow(Player player){
        PlayerUIInventory inventory = player.getUIInventory();
        player.addWindow(inventory);
        CraftingGrid grid = player.getCraftingGrid();
        player.addWindow(grid);
        PlayerOffhandInventory inventory1 = player.getOffhandInventory();
        player.addWindow(inventory1);
    }

    @EventHandler
    public void slotChange(InventoryMoveItemEvent event){
        if(event.getInventory() == null){
            return;
        }
        Player[] players = event.getViewers();
        for(Player player: players) {
            if (MainClass.chestList.containsKey(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event){
        if(event.getInventory() == null){
            return;
        }
        Player p = event.getPlayer();
        if (MainClass.chestList.containsKey(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        Player p = event.getPlayer();
        if (MainClass.playingPlayers.contains(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        Player p = event.getPlayer();
        if (MainClass.playingPlayers.contains(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void InventoryMoveItemEvent(InventoryMoveItemEvent event){
        if(event.getInventory() == null){
            return;
        }
        Player[] players = event.getViewers();
        for(Player player: players) {
            if (MainClass.chestList.containsKey(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent event){
        if(event.getInventory() == null){
            return;
        }
        if(MainClass.chestList.containsKey(event.getPlayer())){
            EntityMinecartChest chest = MainClass.chestList.get(event.getPlayer());
            chest.getInventory().clearAll();
            chest.close();
            MainClass.chestList.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            if(MainClass.playingPlayers.contains((Player) event.getEntity())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void LotteryForceCloseEvent(LotteryForceCloseEvent event){
        if(MainClass.chestList.containsKey(event.getPlayer())){
            EntityMinecartChest chest = MainClass.chestList.get(event.getPlayer());
            chest.getInventory().clearAll();
            chest.close();
            MainClass.chestList.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void moveItem(InventoryClickEvent event){
        if(event.getInventory() == null){
            return;
        }
        Player player = event.getPlayer();
        if (MainClass.chestList.containsKey(player)) {
            if(event.getSourceItem().getCustomName().equals(MainClass.lang.getTranslation("PlayLotteryWindow","StartLotteryWithOneSpinsItemName")) && !MainClass.playingPlayers.contains(player)){
                startLottery(player, false);
            }
            if(event.getSourceItem().getCustomName().equals(MainClass.lang.getTranslation("PlayLotteryWindow","StartLotteryWithTenSpinsItemName")) && !MainClass.playingPlayers.contains(player)){
                startLottery(player, true);
            }
            event.setCancelled(true);
        }
    }

    public void startLottery(Player player, Boolean isTenSpins){
        LotteryBox lotteryBox = MainClass.playerLotteryBoxes.get(player);
        int spin = 1;
        if(isTenSpins){
            spin = 10;
        }
        if(lotteryBox.checkLimit(player.getName(), spin)) {
            if (lotteryBox.deductNeeds(player, spin)) {
                Server.getInstance().getScheduler().scheduleRepeatingTask(new LotteryBoxChangeTask(MainClass.chestList.get(player), player, lotteryBox, spin), MainClass.chest_speed_ticks);
            } else {
                player.sendMessage(MainClass.lang.getTranslation("Tips", "LackOfItemsOrTickets"));
                Server.getInstance().getPluginManager().callEvent(new LotteryForceCloseEvent(player));
            }
        }else {
            player.sendMessage(MainClass.lang.getTranslation("Tips", "TimesLimit"));
            Server.getInstance().getPluginManager().callEvent(new LotteryForceCloseEvent(player));
        }
    }

    @EventHandler
    public void LevelChangeEvent(EntityLevelChangeEvent event){
        if(event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            if(MainClass.playingPlayers.contains(p)) {
                for (String path : MainClass.inventory_cache_paths) {
                    File file = new File((Server.getInstance().getFilePath() + "/" + path + "/").replace("%player%", p.getName()));
                    if (file.exists()) {
                        Config config = new Config(file);
                        config.set("inventoryContents", new HashMap<>());
                        config.save();
                    }
                }
                MainClass.playingPlayers.remove(p);
                MainClass.playerLotteryBoxes.remove(p);
            }
        }
    }

    @EventHandler
    public void Join(PlayerLocallyInitializedEvent event){
        Player player = event.getPlayer();
        Config config = new Config(MainClass.path+"/cache.yml", Config.YAML);
        if(config.exists(player.getName())){
            if(MainClass.save_bag_enabled) {
                for (String string : new ArrayList<>(config.getStringList(player.getName() + ".items"))) {
                    player.getInventory().addItem(Inventory.getItem(string));
                }
            }
            for(String string: new ArrayList<>(config.getStringList(player.getName()+".commands"))){
                Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), string.replace("%player%", player.getName()));
            }
            for(String string: new ArrayList<>(config.getStringList(player.getName()+".messages"))){
                player.sendMessage(string.replace("%player%", player.getName()));
            }
            config.remove(event.getPlayer().getName());
            event.getPlayer().sendMessage("检测到您上次退出有异常，将未发放的物品发放给您！");
            MainClass.instance.getLogger().alert("Detect [" + player.getName() + "] quit the server unexpectedly, trying to deal with it.");
            config.save();
        }
    }

    @EventHandler
    public void Quit(PlayerQuitEvent event){
        if(MainClass.playingPlayers.contains(event.getPlayer())){
            MainClass.playingPlayers.remove(event.getPlayer());
            event.getPlayer().getInventory().clearAll();
        }
    }

    @EventHandler
    public void DataPacketSendEvent(DataPacketSendEvent event){
        if(!MainClass.chestList.containsKey(event.getPlayer())){return;}
        if(event.getPacket() instanceof SetEntityMotionPacket){
            SetEntityMotionPacket pk = (SetEntityMotionPacket) event.getPacket();
            if(MainClass.chestList.get(event.getPlayer()).getId() == pk.eid){
                MainClass.chestList.get(event.getPlayer()).teleport(event.getPlayer().getPosition(), null);
                event.setCancelled(true);
            }
        }
    }
}
