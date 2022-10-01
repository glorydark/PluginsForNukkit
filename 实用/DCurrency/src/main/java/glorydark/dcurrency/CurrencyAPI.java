package glorydark.dcurrency;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;

import java.io.File;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyAPI {

    public static double getCurrencyBalance(String name, String currencyName){
        return getCurrencyBalance(name, currencyName, 0);
    }

    public static double getCurrencyBalance(String name, String currencyName, double defaultValue){
        File file = new File(CurrencyMain.path + "/players/" + name + ".json");
        if(!file.exists()){
            return 0;
        }
        Config config = new Config(file, Config.JSON);
        return BigDecimal.valueOf(config.getDouble(currencyName, defaultValue)).doubleValue();
    }

    public static void addCurrencyBalance(String name, String currencyName, double counts) {
        double balance = add(getCurrencyBalance(name, currencyName, 0), counts);
        setCurrencyBalance(name, currencyName, balance, false);
        Player player = Server.getInstance().getPlayer(name);
        if(player != null){
            player.sendMessage(CurrencyMain.getLang("message_player_currencyReceive", currencyName, counts));
        }
    }

    public static void setCurrencyBalance(String name, String currencyName, double counts, boolean tip) {
        Config config = new Config(CurrencyMain.path + "/players/" + name + ".json", Config.JSON);
        config.set(currencyName, counts);
        config.save();
        if(tip) {
            Player player = Server.getInstance().getPlayer(name);
            if (player != null) {
                player.sendMessage(CurrencyMain.getLang("message_player_currencySet", currencyName, counts));
            }
        }
    }

    public static boolean reduceCurrencyBalance(String name, String currencyName, double counts) {
        double balance = add(getCurrencyBalance(name, currencyName, 0), -counts);
        if(balance < 0){ return false; }
        setCurrencyBalance(name, currencyName, balance, false);
        Player player = Server.getInstance().getPlayer(name);
        if(player != null){
            player.sendMessage(CurrencyMain.getLang("message_player_currencyReduced", currencyName, counts));
        }
        return true;
    }

    public static Map<String, Object> getPlayerConfigs(String name){
        File file = new File(CurrencyMain.path + "/players/" + name + ".json");
        if(!file.exists()){
            return new LinkedHashMap<>();
        }
        Config config = new Config(file, Config.JSON);
        return config.getAll();
    }

    public static double add(double n1, double n2){
        return new BigDecimal(n1).add(new BigDecimal(n2)).doubleValue();
    }
}
