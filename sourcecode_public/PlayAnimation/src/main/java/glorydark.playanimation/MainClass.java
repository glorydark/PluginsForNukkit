package glorydark.playanimation;

import cn.nukkit.event.Listener;
import glorydark.playanimation.AvailableEntityIdentifiersPacket;
import cn.nukkit.plugin.PluginBase;

public class MainClass extends PluginBase implements Listener {

    public static MainClass instance;

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        instance = this;
        this.getLogger().info("PlayAnimation Enable!");
        this.getServer().getCommandMap().register("", new PlayAnimationCommand("playanimation"));
        this.getServer().getCommandMap().register("", new TestCommand("test"));
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getNetwork().registerPacket(AvailableEntityIdentifiersPacket.NETWORK_ID, AvailableEntityIdentifiersPacket.class);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}