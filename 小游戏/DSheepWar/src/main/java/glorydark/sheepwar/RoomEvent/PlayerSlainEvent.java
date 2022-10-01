package glorydark.sheepwar.RoomEvent;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

/*
  玩家被杀害的事件
 */
public class PlayerSlainEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;
    private final Player killer;
    private final Player victim;


    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerSlainEvent(Room room, Player killer, Player victim){
        this.room = room;
        this.killer = killer;
        this.victim = victim;
    }

    public Room getRoom(){ return room;}

    public Player getKiller() {
        return this.killer;
    }

    public Player getVictim() {
        return victim;
    }
}
