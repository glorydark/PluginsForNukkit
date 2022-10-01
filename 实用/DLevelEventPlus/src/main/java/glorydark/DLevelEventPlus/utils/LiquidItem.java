package glorydark.DLevelEventPlus.utils;

import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LiquidItem {

    public static Boolean isLiquidItem(Item item){
        List<Integer> items = new ArrayList<>();
        items.add(Item.WATER);
        items.add(Item.WATER_LILY);
        items.add(Item.STILL_WATER);
        items.add(Item.LAVA);
        items.add(Item.STILL_LAVA);
        items.add(Item.BUCKET);
        return items.contains(item.getId());
    }
}
