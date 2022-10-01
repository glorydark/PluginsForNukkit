package glorydark.sheepwar.settings;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import gameapi.inventory.Inventory;
import gameapi.room.Room;
import gameapi.room.RoomRule;
import glorydark.sheepwar.gui.GuiListener;
import glorydark.sheepwar.gui.GuiType;
import glorydark.sheepwar.utils.ExtendRoomData;
import glorydark.sheepwar.utils.SheepType;

import java.util.ArrayList;
import java.util.HashMap;

// 快捷设置地图的各个点及房间信息
public class SetGameInit implements Listener {
    private static Integer progress = 0; //进度保存
    private static Player manager; //玩家设置标识
    private static Level playLevel;
    private static Room room = new Room("SheepWar",new RoomRule(0),"",1);
    private static ExtendRoomData extendRoomData = new ExtendRoomData(new HashMap<>(), new ArrayList<>(), new ArrayList<>(), null);

    public static void addPlayer(Player player, String levelName){
        if(manager == null){
            reset();
            manager = player;
            Level level = Server.getInstance().getLevelByName(levelName);
            if(level != null){
                playLevel = level;
                extendRoomData.setStartLevel(level);
            }else{
                return;
            }
            Inventory.saveBag(player);
            player.getInventory().clearAll();
            player.teleportImmediate(level.getSpawnLocation().getLocation());
            execute(progress);
            // 清空背包开始
            player.sendMessage(progress+"");
        }else{
            player.sendMessage(TextFormat.RED+"已经有人在配置地图了！");
        }
    }

    public static Room getRoom() {
        return room;
    }

    public static ExtendRoomData getExtendRoomData() {
        return extendRoomData;
    }

    public static Player getManager() {
        return manager;
    }

    public static Level getPlayLevel() {
        return playLevel;
    }

    private static void execute(Integer progress){
        Item item;
        switch (progress){
            case 0:
                manager.sendTitle("设置等待地点");
                item = Item.get(Item.BEACON, 0, 1);
                item.setCustomName(TextFormat.GREEN+"设置等待地点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;
            case 1:
                manager.getInventory().clearAll();
                manager.sendTitle("设置结束地点");
                item = Item.get(Item.IRON_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"设置结束地点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;
            case 2:
                manager.getInventory().clearAll();
                manager.sendTitle("设置玩家随机出生点");
                item = Item.get(Item.GOLD_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"设置玩家随机出生点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除玩家随机出生点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;
            case 3:
                manager.getInventory().clearAll();
                manager.sendTitle("设置白羊羊刷新点");
                item = Item.get(Item.WOOL, 0, 1);
                item.setCustomName(TextFormat.GREEN+"设置白羊羊刷新点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除刷新点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;
            case 4:
                manager.getInventory().clearAll();
                manager.sendTitle("设置黑羊羊刷新点");
                item = Item.get(Item.WOOL, 15, 1);
                item.setCustomName(TextFormat.GREEN+"设置黑羊羊刷新点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除刷新点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;

            case 5:
                manager.getInventory().clearAll();
                manager.sendTitle("设置红羊羊刷新点");
                item = Item.get(Item.WOOL, 14, 1);
                item.setCustomName(TextFormat.GREEN+"设置红羊羊刷新点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除刷新点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;

            case 6:
                manager.getInventory().clearAll();
                manager.sendTitle("设置蓝羊羊刷新点");
                item = Item.get(Item.WOOL, 3, 1);
                item.setCustomName(TextFormat.GREEN+"设置蓝羊羊刷新点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除刷新点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;

            case 7:
                manager.getInventory().clearAll();
                manager.sendTitle("设置绿羊羊刷新点");
                item = Item.get(Item.WOOL, 5, 1);
                item.setCustomName(TextFormat.GREEN+"设置绿羊羊刷新点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除刷新点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;

            case 8:
                manager.getInventory().clearAll();
                manager.sendTitle("设置紫羊羊刷新点");
                item = Item.get(Item.WOOL, 10, 1);
                item.setCustomName(TextFormat.GREEN+"设置紫羊羊刷新点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除刷新点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;

            case 9:
                manager.getInventory().clearAll();
                manager.sendTitle("设置金羊羊刷新点");
                item = Item.get(Item.WOOL, 4, 1);
                item.setCustomName(TextFormat.GREEN+"设置金羊羊刷新点");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除刷新点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;
            case 10: // add
                manager.getInventory().clearAll();
                manager.sendTitle("设置资源箱位置");
                item = Item.get(Item.CHEST, 0, 1);
                item.setCustomName(TextFormat.GREEN+"设置资源箱位置");
                manager.getInventory().setItem(0, item);

                item = Item.get(Item.REDSTONE_BLOCK, 0, 1);
                item.setCustomName(TextFormat.GREEN+"删除资源箱刷新点");
                manager.getInventory().setItem(1, item);

                item = Item.get(Block.REDSTONE_TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"返回上一步");
                manager.getInventory().setItem(7, item);

                item = Item.get(Block.TORCH, 0, 1);
                item.setCustomName(TextFormat.GREEN+"下一步");
                manager.getInventory().setItem(8, item);
                break;
            case 11:
                manager.getInventory().clearAll();
                FormWindowCustom window = new FormWindowCustom("其他设置");
                window.addElement(new ElementInput("房间名"));
                window.addElement(new ElementInput("等待时间（秒）", "60"));
                window.addElement(new ElementInput("预备时间（秒）", "10"));
                window.addElement(new ElementInput("游戏时间（秒）", "60"));
                window.addElement(new ElementInput("颁奖时间（秒）", "5"));
                window.addElement(new ElementInput("最大人数","16"));
                window.addElement(new ElementInput("最小人数","1"));
                GuiListener.showFormWindow(manager, window, GuiType.roomSettings);
                break;
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        if(manager == null){ return; }
        Player player = event.getPlayer();
        if(player != null && player.getName().equals(manager.getName())){
            if(progress == 10){
                Location blockLocation = event.getBlock().getLocation();
                switch (event.getBlock().getId()){
                    case Block.REDSTONE_BLOCK:
                        player.sendMessage(TextFormat.GREEN+"移除资源箱刷新点："+ blockLocation);
                        extendRoomData.removeChestPos(blockLocation);
                        break;
                    case Block.CHEST:
                        player.sendMessage(TextFormat.GREEN+"新增资源箱刷新点："+ blockLocation);
                        extendRoomData.addChestPos(blockLocation);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event){
        if(manager == null){ return; }
        Player player = event.getPlayer();
        if(player != null && player.getName().equals(manager.getName())){
            event.setCancelled(true);
            Location blockLocation = event.getBlock().getLocation();
            switch (event.getItem().getId()){
                case Block.BEACON:
                    String location = blockLocation.getFloorX()+":"+blockLocation.getFloorY()+":"+blockLocation.getFloorZ()+":"+blockLocation.getLevel().getName();
                    room.setWaitSpawn(location);
                    progress+=1;
                    execute(progress);
                    break;
                case Block.IRON_BLOCK:
                    location = blockLocation.getFloorX()+":"+blockLocation.getFloorY()+":"+blockLocation.getFloorZ()+":"+blockLocation.getLevel().getName();
                    room.setEndSpawn(location);
                    progress+=1;
                    execute(progress);
                    break;
                case Block.GOLD_BLOCK:
                    extendRoomData.addRandomSpawn(blockLocation);
                    break;
                case Block.REDSTONE_BLOCK:
                    switch (progress){
                        case 2:
                            extendRoomData.removeRandomSpawn(blockLocation);
                            break;
                        case 3:
                            extendRoomData.removeSheepSpawn(SheepType.NormalSheep, blockLocation);
                            break;
                        case 4:
                            extendRoomData.removeSheepSpawn(SheepType.Black_Sheep, blockLocation);
                            break;
                        case 5:
                            extendRoomData.removeSheepSpawn(SheepType.Red_Sheep, blockLocation);
                            break;
                        case 6:
                            extendRoomData.removeSheepSpawn(SheepType.Blue_Sheep, blockLocation);
                            break;
                        case 7:
                            extendRoomData.removeSheepSpawn(SheepType.Green_Sheep, blockLocation);
                            break;
                        case 8:
                            extendRoomData.removeSheepSpawn(SheepType.Purple_Sheep, blockLocation);
                            break;
                        case 9:
                            extendRoomData.removeSheepSpawn(SheepType.Gold_Sheep, blockLocation);
                            break;
                    }
                    break;
                case Block.WOOL:
                    switch (progress){
                        case 2:
                            player.sendMessage(TextFormat.GREEN+"新增白羊羊刷新点："+ blockLocation);
                            extendRoomData.addRandomSpawn(blockLocation);
                            break;
                        case 3:
                            player.sendMessage(TextFormat.GREEN+"新增白羊羊刷新点："+ blockLocation);
                            extendRoomData.addSheepSpawn(SheepType.NormalSheep, blockLocation);
                            break;
                        case 4:
                            player.sendMessage(TextFormat.GREEN+"新增黑羊羊刷新点："+ blockLocation);
                            extendRoomData.addSheepSpawn(SheepType.Black_Sheep, blockLocation);
                            break;
                        case 5:
                            player.sendMessage(TextFormat.GREEN+"新增红羊羊刷新点："+ blockLocation);
                            extendRoomData.addSheepSpawn(SheepType.Red_Sheep, blockLocation);
                            break;
                        case 6:
                            player.sendMessage(TextFormat.GREEN+"新增蓝羊羊刷新点："+ blockLocation);
                            extendRoomData.addSheepSpawn(SheepType.Blue_Sheep, blockLocation);
                            break;
                        case 7:
                            player.sendMessage(TextFormat.GREEN+"新增绿羊羊刷新点："+ blockLocation);
                            extendRoomData.addSheepSpawn(SheepType.Green_Sheep, blockLocation);
                            break;
                        case 8:
                            player.sendMessage(TextFormat.GREEN+"新增紫羊羊刷新点："+ blockLocation);
                            extendRoomData.addSheepSpawn(SheepType.Purple_Sheep, blockLocation);
                            break;
                        case 9:
                            player.sendMessage(TextFormat.GREEN+"新增金羊羊刷新点："+ blockLocation);
                            extendRoomData.addSheepSpawn(SheepType.Gold_Sheep, blockLocation);
                            break;
                    }
                    break;
                case Block.TORCH:
                    switch (progress){
                        case 0:
                            if(room.getWaitSpawn() == null) {
                                manager.sendMessage(TextFormat.RED+"等待地点未设置！");
                                return;
                            }
                            break;
                        case 1:
                            if(room.getEndSpawn() == null) {
                                manager.sendMessage(TextFormat.RED+"结束地点未设置！");
                                return;
                            }
                            break;
                        case 2:
                            if(extendRoomData.getRandomSpawn() == null || extendRoomData.getRandomSpawn().size() < 1) {
                                manager.sendMessage(TextFormat.RED+"玩家随机出生点未设置！");
                                return;
                            }
                            break;
                        case 3:
                            if(extendRoomData.getRandomSheepSpawnStrings(SheepType.NormalSheep) == null || extendRoomData.getRandomSheepSpawnStrings(SheepType.NormalSheep).size() < 1) {
                                manager.sendMessage(TextFormat.RED+"白羊羊刷新点未设置！");
                                return;
                            }
                            break;
                        case 4:
                            if(extendRoomData.getRandomSheepSpawnStrings(SheepType.Black_Sheep) == null || extendRoomData.getRandomSheepSpawnStrings(SheepType.Black_Sheep).size() < 1) {
                                manager.sendMessage(TextFormat.RED+"黑羊羊刷新点未设置！");
                                return;
                            }
                            break;
                        case 5:
                            if(extendRoomData.getRandomSheepSpawnStrings(SheepType.Red_Sheep) == null || extendRoomData.getRandomSheepSpawnStrings(SheepType.Red_Sheep).size() < 1) {
                                manager.sendMessage(TextFormat.RED+"红羊羊刷新点未设置！");
                                return;
                            }
                            break;
                        case 6:
                            if(extendRoomData.getRandomSheepSpawnStrings(SheepType.Blue_Sheep) == null || extendRoomData.getRandomSheepSpawnStrings(SheepType.Blue_Sheep).size() < 1) {
                                manager.sendMessage(TextFormat.RED+"蓝羊羊刷新点未设置！");
                                return;
                            }
                            break;
                        case 7:
                            if(extendRoomData.getRandomSheepSpawnStrings(SheepType.Green_Sheep) == null || extendRoomData.getRandomSheepSpawnStrings(SheepType.Green_Sheep).size() < 1) {
                                manager.sendMessage(TextFormat.RED+"绿羊羊刷新点未设置！");
                                return;
                            }
                            break;
                        case 8:
                            if(extendRoomData.getRandomSheepSpawnStrings(SheepType.Purple_Sheep) == null || extendRoomData.getRandomSheepSpawnStrings(SheepType.Purple_Sheep).size() < 1) {
                                manager.sendMessage(TextFormat.RED+"紫羊羊刷新点未设置！");
                                return;
                            }
                            break;
                        case 9:
                            if(extendRoomData.getRandomSheepSpawnStrings(SheepType.Gold_Sheep) == null || extendRoomData.getRandomSheepSpawnStrings(SheepType.Gold_Sheep).size() < 1) {
                                manager.sendMessage(TextFormat.RED+"金羊羊刷新点未设置！");
                                return;
                            }
                            break;
                        case 10:
                            if(extendRoomData.getChestPos() == null || extendRoomData.getChestPos().size() < 1) {
                                manager.sendMessage(TextFormat.RED+"资源箱刷新点未设置！");
                                return;
                            }
                            break;
                    }
                    progress+=1;
                    execute(progress);
                    break;
                case Block.REDSTONE_TORCH:
                    progress-=1;
                    execute(progress);
                    break;
            }
        }
    }

    @EventHandler
    public void Break(BlockBreakEvent event){
        if(manager == null){return;}
        if(!event.getPlayer().getName().equals(manager.getName())){
            return;
        }
        event.setCancelled(true);
    }
    

    @EventHandler
    public void quit(PlayerQuitEvent event){
        if(event.getPlayer().equals(manager)){
            reset();
        }
    }

    public static void reset(){
        manager = null;
        progress = 0;
        room = new Room("SheepWar",new RoomRule(0),"",1);
        extendRoomData = new ExtendRoomData(new HashMap<>(), new ArrayList<>(), new ArrayList<>(), null);
    }


}
