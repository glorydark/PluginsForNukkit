package glorydark.lotterybox.tools;

import cn.nukkit.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Prize {
    private String name;
    private String description;
    private Item displayitem;
    private Boolean broadcast;
    private Item[] items;
    private List<String> consolecommands;
    private Integer possibility;

    private Boolean showOriginName;

    private String rarity;
    public Prize(String name, String description, Item displayItem, Boolean broadcast, Item[] items, List<String> consolecommands, Integer possibility, Boolean showOriginName, String rarity){
        this.name = name;
        this.description = description;
        this.displayitem = displayItem;
        this.broadcast = broadcast;
        this.items = items;
        this.consolecommands = consolecommands;
        this.possibility = possibility;
        this.showOriginName = showOriginName;
        this.rarity = rarity;
    }

    @Override
    public String toString() {
        return "Prize{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", displayitem=" + displayitem +
                ", broadcast=" + broadcast +
                ", items=" + Arrays.toString(items) +
                ", consolecommands=" + consolecommands +
                ", possibility=" + possibility +
                '}';
    }
}
