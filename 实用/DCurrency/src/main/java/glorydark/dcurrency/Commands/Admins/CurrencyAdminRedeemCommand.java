package glorydark.dcurrency.Commands.Admins;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.Commands.SubCommand;
import glorydark.dcurrency.CurrencyMain;

import java.io.File;
import java.util.ArrayList;

public class CurrencyAdminRedeemCommand extends SubCommand {
    private final String command;
    private final String help;

    public CurrencyAdminRedeemCommand(String command, String help){
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
        if(strings.length != 3){ sender.sendMessage(getHelp()); return true; }
        switch (strings[1]){
            case "add":
                File file = new File(CurrencyMain.path+"/redeems/"+strings[2]+".yml");
                if(!file.exists()){
                    Config config = new Config(file, Config.YAML);
                    config.set("displayName", "test");
                    config.set("description", "test");
                    config.set("needCurrency", new ArrayList<>());
                    config.set("executeCommands", new ArrayList<>());
                    config.set("limitTimes", 0);
                    config.save();
                    sender.sendMessage(CurrencyMain.getLang("message_op_redeem_create_success", strings[2]));
                }else{
                    sender.sendMessage(CurrencyMain.getLang("message_op_redeem_create_existed", strings[2]));
                }
                break;
            case "remove":
                file = new File(CurrencyMain.path+"/redeems/"+strings[2]+".yml");
                if(file.exists()){
                    if(file.delete()){
                        sender.sendMessage(CurrencyMain.getLang("message_op_redeem_remove_success", strings[2]));
                    }
                }else{
                    sender.sendMessage(CurrencyMain.getLang("message_op_redeem_remove_notExisted", strings[2]));
                }
                break;
        }
        return true;
    }

    @Override
    public String getHelp() {
        return help;
    }
}
