package glorydark.playressound.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.playressound.MainClass;

public class playResSoundCommand extends Command {
    public playResSoundCommand() {
        super("playResSound", "To play a certain and defined sound in a res-pack", "/playResSound player soundName");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isOp() || !(commandSender instanceof Player)) {
            if(strings.length > 1){
                Player player = Server.getInstance().getPlayer(strings[0]);
                if(player != null) {
                    if(player.isOnline()) {
                        MainClass.playResourcePackOggMusic(player, strings[1]);
                    }else{
                        commandSender.sendMessage("Player ["+strings[0]+"] is not online!");
                    }
                }else{
                    commandSender.sendMessage("Can not find the player"+strings[0]+"!");
                }
            }else{
                commandSender.sendMessage("[Usage Example] /playResSound player soundName");
            }
            return true;
        }else{
            commandSender.sendMessage("You are not authorized!");
        }
        return true;
    }
}
