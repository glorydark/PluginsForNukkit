package glorydark.sheepwar.RoomEvent;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.level.Position;
import gameapi.room.Room;
import glorydark.sheepwar.utils.SheepType;

/*
  玩家使用技能
 */
public class SheepSpawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;
    private final SheepType sheepType;
    private final Position spawnPoint;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public SheepSpawnEvent(Room room, SheepType sheepType, Position spawnPoint){
        this.room = room;
        this.sheepType = sheepType;
        this.spawnPoint = spawnPoint;
    }

    public Room getRoom(){ return room;}

    public Position getSpawnPoint() {
        return spawnPoint;
    }

    public SheepType getSheepType() {
        return sheepType;
    }

    @Override
    public Player getPlayer() {
        return super.getPlayer();
    }
}
