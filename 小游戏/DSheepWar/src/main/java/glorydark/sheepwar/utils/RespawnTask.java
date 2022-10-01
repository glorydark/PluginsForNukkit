package glorydark.sheepwar.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RespawnTask extends Task {
    private final Player player;
    private final Location location;
    private final Room room;

    @Override
    public void onRun(int i) {
        if(room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
            player.teleportImmediate(location);
            player.setGamemode(0);
            player.setHealth(10);
            player.setMaxHealth(10);
            player.getFoodData().reset();
            player.getInventory().clearAll();
            player.getInventory().addItem(Item.get(Item.WOODEN_SWORD, 0, 1));
            player.getInventory().addItem(Item.get(Item.SNOWBALL, 0, 16));
            player.getInventory().addItem(Item.get(Item.ENDER_PEARL, 0, 1));
            player.addEffect(Effect.getEffect(Effect.SPEED).setDuration(20).setAmplifier(2).setVisible(false));
            /*
            player.getInventory().setHelmet(Item.get(Item.IRON_HELMET));
            player.getInventory().setBoots(Item.get(Item.IRON_BOOTS));
            player.getInventory().setChestplate(Item.get(Item.IRON_CHESTPLATE));
            player.getInventory().setLeggings(Item.get(Item.IRON_LEGGINGS));
             */
            player.sendTitle("您已复活", "再战一次吧！");
        }else{
            player.setGamemode(0);
        }
    }

    public RespawnTask(Room room, Player player, Location location){
        this.player = player;
        this.location = location;
        this.room = room;
    }
}
