package glorydark.lotterybox.event;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class LotteryForceCloseEvent extends PlayerEvent {
    private final Player player;

    private static final HandlerList handlers = new HandlerList();

    public LotteryForceCloseEvent(Player player){
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
