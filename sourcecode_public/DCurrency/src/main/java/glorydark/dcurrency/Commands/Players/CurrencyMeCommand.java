package glorydark.dcurrency.Commands.Players;

import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.Commands.SubCommand;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;

import java.util.Map;

public class CurrencyMeCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyMeCommand(String command, String help){
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
        if(strings.length != 1 && strings.length != 2){
            return false;
        }
        Map<String, Object> map = CurrencyAPI.getPlayerConfigs(sender.getName());
        int maxIndex = map.keySet().size();
        if(maxIndex == 0){
            sender.sendMessage(CurrencyMain.getLang("message_player_see_noCurrency"));
            return true;
        }
        int startIndex = 1;
        if(strings.length == 2){
            startIndex = 1 + (Integer.parseInt(strings[1]) - 1)*10;
            if(startIndex <= maxIndex) {
                if(maxIndex%10 != 0) {
                    sender.sendMessage(CurrencyMain.getLang("message_player_see_startText", strings[1], (maxIndex / 10 + 1)));
                }else{
                    sender.sendMessage(CurrencyMain.getLang("message_player_see_startText", strings[1], (maxIndex / 10)));
                }
            }else{
                sender.sendMessage(CurrencyMain.getLang("message_player_see_pageNotFound"));
                return true;
            }
        }else{
            if(maxIndex%10 != 0) {
                sender.sendMessage(CurrencyMain.getLang("message_player_see_startText", 1, (maxIndex / 10 + 1)));
            }else{
                sender.sendMessage(CurrencyMain.getLang("message_player_see_startText", 1, (maxIndex / 10)));
            }
        }
        int PageMaxIndex = startIndex + 9;
        if(PageMaxIndex > maxIndex){
            PageMaxIndex = maxIndex;
        }
        for(int record = startIndex;record<=PageMaxIndex; record++){
            String key = map.keySet().toArray(new String[0])[record-1];
            sender.sendMessage(key+": "+map.get(key));
        }
        return true;
    }

    @Override
    public String getHelp() {
        return help;
    }
}