package glorydark.playressound.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Sound;
import glorydark.playressound.MainClass;

public class addAmbientSoundCommand extends Command {
    public addAmbientSoundCommand() {
        super("addAmbientSound", "To play a certain ambient sound in the game", "/addAmbientSound player soundName");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isOp() || !(commandSender instanceof Player)) {
            if (strings.length > 1) {
                Player player = Server.getInstance().getPlayer(strings[0]);
                if (player != null) {
                    if (player.isOnline()) {
                        Sound sound = Sound.valueOf(strings[1]);
                        if (sound != null) {
                            MainClass.addAmbientSound(player.getLevel(), player, sound);
                        } else {
                            commandSender.sendMessage("Can not find the sound" + strings[1] + "!");
                        }
                    } else {
                        commandSender.sendMessage("Player [" + strings[0] + "] is not online!");
                    }
                } else {
                    commandSender.sendMessage("Can not find the player" + strings[0] + "!");
                }
            } else {
                commandSender.sendMessage("[Usage Example] /addAmbientSound player soundName");
            }
        }else{
            commandSender.sendMessage("You are not authorized!");
        }
        return true;
    }
}
