package glorydark.backtolobby;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class BackToLobbyCommand extends Command {
    public BackToLobbyCommand(String command, String[] aliases) {
        super(command);
        this.setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender.isPlayer()) {
            MainClass.backToLobby((Player) commandSender);
        }else{
            commandSender.sendMessage("Please use in game!");
        }
        return true;
    }
}
