package glorydark.dcurrency;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.Commands.CommandsExecutor;
import glorydark.dcurrency.Utils.RedeemItem;
import tip.utils.Api;
import tip.utils.variables.BaseVariable;

import java.io.File;
import java.util.*;

public class CurrencyMain extends PluginBase {

    public static String path;
    public static Map<String, Object> lang = new HashMap<>();

    public static HashMap<String, RedeemItem> redeems = new HashMap<>();

    public static List<String> registered_currencies = new ArrayList<>();

    @Override
    public void onLoad() {
        this.getLogger().info("DCurrency OnLoad");
    }

    @Override
    public void onEnable() {
        //initialize
        path = this.getDataFolder().getPath();
        //load language config
        this.saveDefaultConfig();
        this.saveResource("lang.properties", false);

        lang = new Config(path+"/lang.properties", Config.PROPERTIES).getAll();

        registered_currencies = new ArrayList<>(new Config(path+"/config.yml", Config.YAML).getStringList("registered_currencies"));
        //create folder
        File dic = new File(this.getDataFolder()+"/players/");
        if(!dic.exists()){
            if(!dic.mkdirs()){
                this.getLogger().info(getLang("tips_createFolder_fail"));
                this.setEnabled(false);
                return;
            }
        }
        //register Commands
        this.getServer().getCommandMap().register("", new CommandsExecutor(CurrencyMain.getLang("command_main")));
        //loading functions
        this.loadRedeemsList();
        if(this.getServer().getPluginManager().getPlugin("Tips") != null){
            this.getLogger().info("Detect Tips Enabled!");
            Api.registerVariables("DCurrency", VariableTest.class);
        }
        this.getLogger().info("DCurrency OnEnable");
    }

    public static class VariableTest extends BaseVariable {

        public VariableTest(Player player) {
            super(player);
        }

        @Override
        public void strReplace() {
            for(String currency: registered_currencies) {
                this.addStrReplaceString("{DCurrency_balance_"+currency+"}", String.valueOf(CurrencyAPI.getCurrencyBalance(player.getName(), currency)));
            }
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DCurrency OnDisable");
    }

    public static String getLang(String string, Object... params){
        if(lang.containsKey(string)) {
            String out = (String) lang.get(string);
            for (int i = 1; i <= params.length; i++) {
                out = out.replace("%" + i + "%", String.valueOf(params[i - 1]));
            }
            return out;
        }else{
            return "Key Not Found!";
        }
    }

    public void loadRedeemsList(){
        File dic = new File(this.getDataFolder()+"/redeems/");
        if(!dic.exists()){
            if(dic.mkdirs()){
                this.getLogger().info(getLang("tips_createFolder_fail"));
            }
            return;
        }
        for(File file: Objects.requireNonNull(dic.listFiles())){
            Config config = new Config(file, Config.YAML);
            String identifier = file.getName().split("\\.")[0];
            RedeemItem item = new RedeemItem(identifier, config.getString("description"), config.getInt("limitTimes", 0), new ArrayList<>(config.getStringList("needCurrency")), new ArrayList<>(config.getStringList("executeCommands")));
            redeems.put(identifier, item);
            this.getLogger().info(getLang("tips_loadRedeem_success", identifier));
        }
    }
}