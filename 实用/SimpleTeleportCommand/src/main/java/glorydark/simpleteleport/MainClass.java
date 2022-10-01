package glorydark.simpleteleport;

import cn.nukkit.plugin.PluginBase;

public class MainClass extends PluginBase {
    @Override
    public void onLoad() {
        this.getLogger().info("SimpleTeleportCommand onLoad");
    }

    @Override
    public void onEnable() {
        this.getServer().getCommandMap().register("", new SimpleTeleportCommand("tp"));
        this.getLogger().info("SimpleTeleportCommand onEnabled");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("SimpleTeleportCommand onDisabled");
    }
}
