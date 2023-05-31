package glorydark.playressound.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Sound;
import glorydark.playressound.MainClass;

public class stopSoundCommand extends Command {
    public stopSoundCommand() {
        super("stopSound", "To stop all the sound that is playing to the player", "/stopSound player");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isOp() || !(commandSender instanceof Player)) {
            if (strings.length > 0) {
                Player player = Server.getInstance().getPlayer(strings[0]);
                if (player != null) {
                    if (player.isOnline()) {
                        MainClass.stopResourcePackOggSound(player);
                    } else {
                        commandSender.sendMessage("Player [" + strings[0] + "] is not online!");
                    }
                } else {
                    commandSender.sendMessage("Can not find the player" + strings[0] + "!");
                }
            } else {
                commandSender.sendMessage("[Usage Example] /stopSound player");
            }
        }else{
            commandSender.sendMessage("You are not authorized!");
        }
        return true;
    }
}
