package gameapi.listener.base;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.EventExecutor;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.EventException;

public class RoomRegisteredListener {
    private final Listener listener;
    private final EventPriority priority;
    private final Plugin plugin;
    private final EventExecutor executor;
    private final boolean ignoreCancelled;
    private final String gameName;

    public RoomRegisteredListener(String gameName, Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
        this.gameName = gameName;
    }

    public Listener getListener() {
        return this.listener;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public EventPriority getPriority() {
        return this.priority;
    }

    public void callEvent(String gameName, Event event) throws EventException {
        if(!gameName.equals(getGameName())){
            return;
        }
        if (!(event instanceof Cancellable) || !event.isCancelled() || !this.isIgnoringCancelled()) {
            this.executor.execute(this.listener, event);
        }
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isIgnoringCancelled() {
        return this.ignoreCancelled;
    }
}

