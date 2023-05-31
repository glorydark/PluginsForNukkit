package glorydark.dcurrency.Commands.Admins;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.Commands.SubCommand;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;

public class CurrencyAllCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyAllCommand(String command, String help){
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if(sender.isPlayer() && !sender.isOp()){ return false; }
        HashMap<String, Double> map = new HashMap<>();
        File file = new File(CurrencyMain.path + "/players/");
        for(File json: Objects.requireNonNull(file.listFiles())){
            Config config = new Config(json, Config.JSON);
            for(String key: config.getKeys(false)){
                map.put(key, add(map.getOrDefault(key, 0.0), config.getDouble(key)));
            }
        }
        sender.sendMessage(CurrencyMain.getLang("message_op_seeAll_title"));
        map.forEach((key, value) -> sender.sendMessage(key + ":" + value));
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

    public static double add(double n1, double n2){
        return new BigDecimal(n1).add(new BigDecimal(n2)).doubleValue();
    }
}