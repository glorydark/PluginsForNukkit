package glorydark.dcurrency.Commands.Admins;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.Commands.SubCommand;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;

import java.util.ArrayList;

public class CurrencyReloadCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyReloadCommand(String command, String help){
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if(sender.isPlayer() && !sender.isOp()){ return false; }
        CurrencyMain.loadRedeemsList();
        CurrencyMain.registered_currencies = new ArrayList<>(new Config(CurrencyMain.path+"/config.yml", Config.YAML).getStringList("registered_currencies"));
        sender.sendMessage(CurrencyMain.getLang("message_reload_success"));
        return true;
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
    public String getHelp() {
        return help;
    }
}
