package gameapi.listener;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.event.room.RoomPreStartEvent;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

@Deprecated
public class RoomNextRoundPreStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomNextRoundPreStartListener(Room room){
        this.room = room;
        if (room.getTime() >= room.getWaitTime()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_Ceremony);
            room.setTime(0);
        } else {
            for (Player p : room.getPlayers()) {
                p.sendActionBar("§l§e下一场游戏开始还剩 §l§6" + (room.getWaitTime() - room.getTime()) + " §l§e秒");
            }
            room.setTime(room.getTime()+1);
            room.setRoomStatus(RoomStatus.ROOM_STATUS_PreStart);
            Server.getInstance().getPluginManager().callEvent(new RoomPreStartEvent(room));
        }
    }

    public Room getRoom(){ return room;}
}
