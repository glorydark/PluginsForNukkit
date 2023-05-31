package gameapi.event.room;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.sound.Sound;

public class RoomGameStartEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomGameStartEvent(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
