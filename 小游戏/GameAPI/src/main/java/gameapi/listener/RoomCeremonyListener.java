package gameapi.listener;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.event.room.RoomEndEvent;
import gameapi.fireworkapi.CreateFireworkApi;
import gameapi.inventory.Inventory;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.scoreboard.ScoreboardAPI;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Glorydark
 */
public class RoomCeremonyListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomCeremonyListener(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
