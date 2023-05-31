package testgame.scripts.controller.type;

import cn.nukkit.event.Event;
import cn.nukkit.item.Item;

/**
 * @author Glorydark
 */
public interface ControllerData {
    boolean checkData(Event event, String eventName);

    boolean parseEventData(Object... param);

    @Override
    String toString();

    boolean checkItem(Item item);
}