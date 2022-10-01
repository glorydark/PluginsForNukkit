package gameapi.event.room;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomEndEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomEndEvent(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
