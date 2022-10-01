package glorydark.dcurrency.Utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class RedeemItem {
    private Map<String, Double> neededCurrency = new HashMap<>();
    private List<String> commands;

    private String identifier;

    private String description;

    private int limitTimes;

    public RedeemItem(String identifier, String description, int limitTimes, List<String> stringList, List<String> commands){
        this.identifier = identifier;
        this.description = description;
        this.limitTimes = limitTimes;
        for(String text: stringList){
            String[] textSplits = text.split("\\|");
            neededCurrency.put(textSplits[0], Double.parseDouble(textSplits[1]));
        }
        this.commands = commands;
    }

    public boolean redeemItem(Player player, int counts){
        if(!checkTimeLimits(player, counts)) {
            player.sendMessage(CurrencyMain.getLang("message_player_redeem_overLimitTimes"));
            return false;
        }
        if(!checkNeedCurrency(player, counts)){
            player.sendMessage(CurrencyMain.getLang("message_player_redeem_noEnoughCurrency"));
            return false;
        }
        reduceCurrency(player, counts);
        addRecord(player, counts);
        for (int i = 0; i < counts; i++) {
            executeCommands(player);
        }
        return true;
    }

    public void executeCommands(Player player){
        for(String cmd: commands) {
            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), cmd.replace("%player%", player.getName()));
        }
    }

    public boolean checkNeedCurrency(Player player, int counts){
        for(String currency: neededCurrency.keySet()){
            double own = CurrencyAPI.getCurrencyBalance(player.getName(), currency);
            if(own < multiply(neededCurrency.get(currency), counts)){
                return false;
            }
        }
        return true;
    }

    public void reduceCurrency(Player player, int counts){
        for(String currency: neededCurrency.keySet()){
            CurrencyAPI.reduceCurrencyBalance(player.getName(), currency, multiply(neededCurrency.get(currency), counts));
        }
    }

    public void addRecord(Player player, int counts){
        Config config = new Config(CurrencyMain.path+"/redeems/"+identifier+".yml", Config.YAML);
        config.set("records."+player.getName(), config.getInt("records."+player.getName(), 0) + counts);
        config.save();
    }

    public boolean checkTimeLimits(Player player, int counts){
        Config config = new Config(CurrencyMain.path+"/redeems/"+identifier+".yml", Config.YAML);
        if(limitTimes == 0){
            return true;
        }
        return config.getInt("records."+player.getName(), 0)+counts <= limitTimes;
    }

    @Override
    public String toString() {
        return "RedeemItem{" +
                "neededCurrency=" + neededCurrency +
                ", commands=" + commands +
                ", identifier='" + identifier + '\'' +
                ", description='" + description + '\'' +
                ", limitTimes=" + limitTimes +
                '}';
    }

    public double multiply(double n1, int multiple){
        return new BigDecimal(n1).multiply(BigDecimal.valueOf(multiple)).doubleValue();
    }
}