package glorydark.fasteneditem;

import cn.nukkit.plugin.PluginBase;

public class MainClass extends PluginBase {

    @Override
    public void onEnable() {
        this.getServer().getLogger().info("FastenItem onEnable! Author: Glorydark");
        this.getServer().getCommandMap().register("", new FastenItemCommand("fastenitem"));
        this.getServer().getCommandMap().register("", new CleanInventoryCommand("cleaninv"));
    }

    @Override
    public void onLoad() {
        this.getServer().getLogger().info("FastenItem onLoad!");
    }

    @Override
    public void onDisable() {
        this.getServer().getLogger().info("FastenItem onDisable!");
    }
}
