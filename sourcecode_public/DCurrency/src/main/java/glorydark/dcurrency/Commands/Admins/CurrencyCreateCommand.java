package glorydark.dcurrency.Commands.Admins;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.Commands.SubCommand;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;

import java.util.Map;

public class CurrencyCreateCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyCreateCommand(String command, String help){
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return !sender.isPlayer() || sender.isOp();
    }

    @Override
    public String getName() {
        return command;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if(sender.isPlayer() && !sender.isOp()){ return false; }
        if(strings.length == 2) {
            if (CurrencyMain.registered_currencies.contains(strings[1])) {
                sender.sendMessage(CurrencyMain.getLang("message_default_moneyAlreadyRegistered", strings[1]));
            } else {
                CurrencyMain.registered_currencies.add(strings[1]);
                Config config = new Config(CurrencyMain.path + "/config.yml", Config.YAML);
                config.set("registered_currencies", CurrencyMain.registered_currencies);
                config.save();
                sender.sendMessage(CurrencyMain.getLang("message_default_moneyRegisteredSuccessfully", strings[1]));
            }
            return true;
        }
        return false;
    }

    @Override
    public String getHelp() {
        return help;
    }
}