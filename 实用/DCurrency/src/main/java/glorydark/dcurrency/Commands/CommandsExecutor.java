package glorydark.dcurrency.Commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.Commands.Admins.*;
import glorydark.dcurrency.Commands.Players.CurrencyMeCommand;
import glorydark.dcurrency.Commands.Players.CurrencyPayCommand;
import glorydark.dcurrency.Commands.Players.CurrencyRedeemCommand;
import glorydark.dcurrency.CurrencyMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 若水
 * @author Glorydark
 */

public class CommandsExecutor extends Command {

    public List<SubCommand> commands = new ArrayList<>();

    public HashMap<String, Integer> commandPairs = new HashMap<>();

    public CommandsExecutor(String name) {
        super(name);
        //Admins
        loadSubCommand(new CurrencyGiveCommand(CurrencyMain.getLang("command_minor_give"), CurrencyMain.getLang("help_give")));
        loadSubCommand(new CurrencySeeCommand(CurrencyMain.getLang("command_minor_see"), CurrencyMain.getLang("help_see")));
        loadSubCommand(new CurrencySetCommand(CurrencyMain.getLang("command_minor_set"), CurrencyMain.getLang("help_set")));
        loadSubCommand(new CurrencyAdminRedeemCommand(CurrencyMain.getLang("command_minor_redeem_admin"), CurrencyMain.getLang("help_redeem_admin")));
        //Players
        loadSubCommand(new CurrencyMeCommand(CurrencyMain.getLang("command_minor_me"), CurrencyMain.getLang("help_me")));
        loadSubCommand(new CurrencyPayCommand(CurrencyMain.getLang("command_minor_pay"), CurrencyMain.getLang("help_pay")));
        loadSubCommand(new CurrencyRedeemCommand(CurrencyMain.getLang("command_minor_redeem"), CurrencyMain.getLang("help_redeem")));
        loadSubCommand(new CurrencyAllCommand(CurrencyMain.getLang("command_minor_allMoney"), CurrencyMain.getLang("help_all_money")));
    }

    private void loadSubCommand(SubCommand cmd) {
        commands.add(cmd);
        int commandId = (commands.size()) - 1;
        commandPairs.put(cmd.getName().toLowerCase(), commandId);
        for (String alias : cmd.getAliases()) {
            commandPairs.put(alias.toLowerCase(), commandId);
        }
    }

    private void sendHelp(CommandSender sender){
        sender.sendMessage(CurrencyMain.getLang("help_title"));
        sender.sendMessage(CurrencyMain.getLang("help_me"));
        sender.sendMessage(CurrencyMain.getLang("help_redeem"));
        sender.sendMessage(CurrencyMain.getLang("help_pay"));
        if(sender.isOp() || !sender.isPlayer()){
            sender.sendMessage(CurrencyMain.getLang("help_give"));
            sender.sendMessage(CurrencyMain.getLang("help_set"));
            sender.sendMessage(CurrencyMain.getLang("help_see"));
            sender.sendMessage(CurrencyMain.getLang("help_redeem_admin"));
            sender.sendMessage(CurrencyMain.getLang("help_all_money"));
        }
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(args.length == 0){
            sendHelp(sender);
        }else{
            String subCommand = args[0].toLowerCase();
            if (commandPairs.containsKey(subCommand)) {
                SubCommand command = commands.get(commandPairs.get(subCommand));
                boolean canUse = command.canUse(sender);
                if (canUse) {
                    if(!command.execute(sender, args)){
                        sender.sendMessage(command.getHelp());
                    }
                    return true;
                } else {
                    sender.sendMessage(CurrencyMain.getLang("message_default_noPermission"));
                    return false;
                }
            } else {
                this.sendHelp(sender);
                return true;
            }
        }
        return true;
    }
}
