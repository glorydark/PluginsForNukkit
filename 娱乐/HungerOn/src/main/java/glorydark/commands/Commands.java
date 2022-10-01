package glorydark.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.HungerOn;

public class Commands extends Command {
    public Commands(String name) {
        super(name,"§e§lHungerOn","/HungerOn help");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Config cfg = HungerOn.getlang();
        if (args.length >= 1) {
            if(args[0].equals("help")){
                for(String string: cfg.getStringList("CommandHelp")){
                    commandSender.sendMessage(string);
                }
            }
            if(args[0].equals("edit")){
                if(!commandSender.isOp() && commandSender instanceof Player){
                    commandSender.sendMessage(cfg.getString("NotAuthorizedMessage"));
                    return false;
                }
                if(args.length == 3){
                    Config config = new Config(HungerOn.filepath+"/config.yml",Config.YAML);
                    if(config.getAll().containsKey(args[1])) {
                        config.set(args[1],args[2]);
                        config.save();
                        commandSender.sendMessage(cfg.getString("EditedSuccessfullyMessage").replace("%1s", args[1]).replace("%2s", args[2]));
                    }else{
                        commandSender.sendMessage(cfg.getString("EditedFailedMessage").replace("%s", args[1]));
                    }
                }
            }
        }else{
            commandSender.sendMessage(cfg.getString("WrongCommandMessage"));
        }
        return true;
    }
}
