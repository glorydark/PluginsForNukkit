package gameapi.listener.base;

import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.MethodEventExecutor;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class BaseGameRegistry {
    private static final List<RoomRegisteredListener> listeners = new ArrayList<>();

    public void registerEvents(String gameName, Listener listener, Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin attempted to register " + listener.getClass().getName() + " while not enabled");
        } else {
            HashSet<Method> methods;
            try {
                Method[] publicMethods = listener.getClass().getMethods();
                Method[] privateMethods = listener.getClass().getDeclaredMethods();
                methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0F);
                Collections.addAll(methods, publicMethods);
                Collections.addAll(methods, privateMethods);
            } catch (NoClassDefFoundError var11) {
                plugin.getLogger().error("Plugin " + plugin.getDescription().getFullName() + " has failed to register events for " + listener.getClass() + " because " + var11.getMessage() + " does not exist.");
                return;
            }

            for(Method method: methods) {
                EventHandler eh = method.getAnnotation(EventHandler.class);
                RoomRegisteredListener evl;
                if(eh != null){
                    evl = new RoomRegisteredListener(gameName, listener, new MethodEventExecutor(method), eh.priority(), plugin, eh.ignoreCancelled());
                }else{
                    evl = new RoomRegisteredListener(gameName, listener, new MethodEventExecutor(method), EventPriority.NORMAL, plugin, false);
                }
                listeners.add(evl);
            }
        }
    }

    public void callEvent(String gameName, Event event) {
        List<RoomRegisteredListener> find = listeners.stream().filter(l -> l.getGameName().equals(gameName)).collect(Collectors.toList());
        for(RoomRegisteredListener listener: find){
            listener.callEvent(gameName, event);
        }
    }
}
