package gameapi.listener;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Sound;
import gameapi.event.room.RoomGameStartEvent;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.utils.AdvancedLocation;

import java.util.List;
import java.util.Random;

/**
 * @author Glorydark
 */
public class RoomReadyStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomReadyStartListener(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
