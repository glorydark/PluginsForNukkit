package glorydark.sudo;

import cn.nukkit.plugin.PluginBase;

public class MainClass extends PluginBase {

    public static MainClass instance;

    @Override
    public void onLoad() {
        this.getLogger().info("Sudo onLoad!");
    }

    @Override
    public void onEnable() {
        instance = this;
        this.getServer().getCommandMap().register("", new SudoCommand("sudo"));
        this.getLogger().info("Sudo enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Sudo disabled!");
    }
}