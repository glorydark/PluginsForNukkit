package gameapi.event.base;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.listener.PlayerEventListener;
import gameapi.room.Room;

public class RoomPlayerDeathEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;
    private final Player player;

    private final PlayerEventListener.DamageSource lastDamageSource;

    private EntityDamageEvent.DamageCause cause;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomPlayerDeathEvent(Room room, Player player, EntityDamageEvent.DamageCause cause){
        this.room = room;
        this.player = player;
        this.lastDamageSource = (PlayerEventListener.DamageSource) room.getPlayerProperties(player.getName(), "last_damage_source", new PlayerEventListener.DamageSource("", 0L));
        this.cause = cause;
    }

    public Room getRoom(){ return room;}

    public Player getPlayer() {
        return this.player;
    }

    public Player getLastDamageSource() {
        if(!lastDamageSource.getDamager().equals("")){
            if(System.currentTimeMillis() - lastDamageSource.getMilliseconds() <= 5000){
                //When the difference between now and last damage time is over 5 seconds, GameAPI will regard this as an outdated damage source;
                return Server.getInstance().getPlayer(lastDamageSource.getDamager());
            }
        }
        return null;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
}
