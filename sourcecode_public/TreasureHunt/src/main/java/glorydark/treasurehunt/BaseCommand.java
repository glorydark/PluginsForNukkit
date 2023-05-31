package glorydark.treasurehunt;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.io.File;
import java.util.Objects;

public class BaseCommand extends Command {
    public BaseCommand(String command) {
        super(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender.isPlayer()){
            if(commandSender.isOp()){
                if(strings.length == 0){ commandSender.sendMessage(MainClass.translateString("command_wrongUsage")); return true; }
                Player player = (Player) commandSender;
                switch (strings[0]){
                    case "help":
                        player.sendMessage(MainClass.translateString("command_help_1"));
                        player.sendMessage(MainClass.translateString("command_help_2"));
                        break;
                    case "create":
                        if(strings.length < 2){ return false; }
                        MainClass.createTreasure(player, player.getPosition(), strings[1]);
                        break;
                    case "remove":
                        MainClass.deleteTreasure(player, player.getPosition());
                        break;
                    case "clearall":
                        File file = new File(MainClass.path+"/players");
                        for(File one: Objects.requireNonNull(file.listFiles())) {
                            if (one.delete()){
                                commandSender.sendMessage(MainClass.translateString("clearall_success"));
                            }
                        }
                        player.sendMessage(MainClass.translateString("clearall_success"));
                        break;
                }
            }else{
                commandSender.sendMessage(MainClass.translateString("command_noPermission"));
            }
        }else{
            switch (strings[0]){
                case "help":
                    commandSender.sendMessage(MainClass.translateString("command_help_1"));
                    commandSender.sendMessage(MainClass.translateString("command_help_2"));
                    break;
                case "create":
                case "remove":
                    commandSender.sendMessage(MainClass.translateString("command_useInGame"));
                    break;
                case "clearall":
                    File file = new File(MainClass.path+"/players");
                    for(File one: Objects.requireNonNull(file.listFiles())) {
                        if (one.delete()){
                            commandSender.sendMessage(MainClass.translateString("clearall_success"));
                        }
                    }
                    break;
            }
        }
        return true;
    }
}
