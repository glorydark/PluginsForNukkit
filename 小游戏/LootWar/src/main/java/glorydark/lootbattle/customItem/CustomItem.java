package glorydark.lootbattle.customItem;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public interface CustomItem {

    void getCustomItem();

    boolean checkItem(Item item);

    void execute();

    boolean isCoolDown(Player player);
}
