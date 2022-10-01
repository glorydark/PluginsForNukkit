package glorydark.lotterybox.tools;

import cn.nukkit.item.Item;

import java.util.List;

public class Bonus {
    private final String name;
    private final Item[] items;
    private final List<String> consolecommands;
    private final Integer needTimes;

    public Bonus(String prizeName, Item[] items, List<String> consolecommands, Integer needTimes){
        this.name = prizeName;
        this.items = items;
        this.consolecommands = consolecommands;
        this.needTimes = needTimes;
    }

    public String getName() {
        return name;
    }

    public Integer getNeedTimes() {
        return needTimes;
    }

    public List<String> getConsolecommands() {
        return consolecommands;
    }

    public Item[] getItems() {
        return items;
    }
}
