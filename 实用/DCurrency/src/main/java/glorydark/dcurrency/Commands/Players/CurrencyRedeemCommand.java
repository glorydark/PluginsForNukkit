package glorydark.dcurrency.Commands.Players;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.Commands.SubCommand;
import glorydark.dcurrency.CurrencyMain;

public class CurrencyRedeemCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyRedeemCommand(String command, String help){
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.isPlayer();
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
        if(strings.length != 2 && strings.length != 3){
            sender.sendMessage(getHelp());
            return false;
        }
        if(CurrencyMain.redeems.containsKey(strings[1])){
            int counts = 1;
            if(strings.length == 3){
                counts = Integer.parseInt(strings[2]);
                if(counts <= 0){
                    counts = 1;
                }
            }
            if(CurrencyMain.redeems.get(strings[1]).redeemItem((Player) sender, counts)){
                sender.sendMessage(CurrencyMain.getLang("message_player_redeem_success"));
            }
        }
        return true;
    }

    @Override
    public String getHelp() {
        return help;
    }
}
