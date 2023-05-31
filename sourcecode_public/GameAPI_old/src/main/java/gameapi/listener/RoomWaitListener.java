package gameapi.listener;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.event.room.RoomPreStartEvent;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

/**
 * @author Glorydark
 */
public class RoomWaitListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomWaitListener(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
