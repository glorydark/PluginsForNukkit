package glorydark.sheepwar.utils;

import cn.nukkit.item.Item;

public class RefreshItem {
    private Item item;
    private Integer odds;
    public RefreshItem(Item item, Integer odds){
        this.item = item;
        this.odds = odds;
    }

    public Integer getOdds() {
        return odds;
    }

    public Item getItem() {
        return item;
    }
}
