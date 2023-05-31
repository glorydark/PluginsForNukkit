package glorydark.fasteneditem;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class CleanInventoryCommand extends Command {
    public CleanInventoryCommand(String command) {
        super(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            ((Player) commandSender).getPlayer().getInventory().clearAll();
        }else{
            commandSender.sendMessage("You should use this command in game.");
        }
        return true;
    }
}
