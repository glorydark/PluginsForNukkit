package gameapi.listener;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.utils.TextFormat;
import gameapi.event.room.RoomReadyStartEvent;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

/**
 * @author Glorydark
 */
public class RoomPreStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomPreStartListener(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
