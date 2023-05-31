package gameapi.event.room;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
@Deprecated
public class RoomNextRoundPreStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomNextRoundPreStartEvent(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
