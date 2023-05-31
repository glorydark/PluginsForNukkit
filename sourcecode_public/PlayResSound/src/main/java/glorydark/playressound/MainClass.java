package glorydark.playressound;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.StopSoundPacket;
import cn.nukkit.plugin.PluginBase;
import glorydark.playressound.commands.addAmbientSoundCommand;
import glorydark.playressound.commands.playResSoundCommand;
import glorydark.playressound.commands.stopSoundCommand;

public class MainClass extends PluginBase implements Listener {
    @Override
    public void onEnable() {
        this.getLogger().info("PlayResSound Enabled!");
        this.getServer().getCommandMap().register("",new addAmbientSoundCommand());
        this.getServer().getCommandMap().register("",new playResSoundCommand());
        this.getServer().getCommandMap().register("",new stopSoundCommand());
    }

    @Override
    public void onLoad() {
        this.getLogger().info("PlayResSound OnLoad!");
        this.getLogger().info("Author: Glorydark");
    }

    public static void stopResourcePackOggSound(Player player){
        StopSoundPacket pk = new StopSoundPacket();
        pk.name = " ";
        pk.stopAll = true;
        player.dataPacket(pk);
    }

    public static void playResourcePackOggMusic(Player player, String filename){
        PlaySoundPacket pk = new PlaySoundPacket();
        pk.name = filename;
        pk.x = player.getFloorX();
        pk.y = player.getFloorY();
        pk.z = player.getFloorZ();
        pk.volume = 1;
        pk.pitch = 1;
        player.dataPacket(pk);
    }

    public static void addAmbientSound(Level level, Player player, cn.nukkit.level.Sound sound){
        level.addSound(player.getPosition(),sound);
    }
}
