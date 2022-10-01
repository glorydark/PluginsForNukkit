package glorydark.sheepwar.RoomEvent;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import gameapi.room.Room;

/*
  玩家夺取到羊的事件
 */
public class PlayerObtainSheepEventByCollision extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;
    private final Player player;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerObtainSheepEventByCollision(Room room, Player player){
        this.room = room;
        this.player = player;
    }

    public Room getRoom(){ return room;}

    public Player getPlayer() {
        return this.player;
    }
}
