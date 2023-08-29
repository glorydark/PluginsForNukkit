package glorydark.lotterybox;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.lotterybox.commands.MainCommand;
import glorydark.lotterybox.forms.GuiListener;
import glorydark.lotterybox.languages.Lang;
import glorydark.lotterybox.listeners.EventListeners;
import glorydark.lotterybox.tools.*;
import tip.utils.Api;
import tip.utils.variables.BaseVariable;

import java.io.File;
import java.util.*;

public class MainClass extends PluginBase {

    public static String path = "";

    public static Lang lang;

    public static List<LotteryBox> lotteryBoxList = new ArrayList<>();

    public static MainClass instance;

    public static HashMap<Player, EntityMinecartChest> chestList = new HashMap<>();

    public static HashMap<Player, LotteryBox> playerLotteryBoxes = new HashMap<>();

    public static List<Player> playingPlayers = new ArrayList<>();

    public static HashMap<String, Rarity> rarities = new HashMap<>();

    public static boolean forceDefaultMode = false;

    public static int default_speed_ticks;

    public static int chest_speed_ticks;

    public static List<String> banWorlds;

    public static List<String> banWorldPrefix;

    public static String showType;

    public static boolean show_reward_window;

    public static List<String> inventory_cache_paths;

    public static boolean save_bag_enabled;

    public static List<String> registered_tickets;

    @Override
    public void onLoad() {
        this.getLogger().info("LotteryBox onLoad!");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        new File(path+"/languages/").mkdirs();
        new File(path+"/players/").mkdirs();
        new File(path+"/boxes/").mkdirs();
        new File(path+"/tickets/").mkdirs();
        this.saveResource("languages/zh-cn.yml", false);
        this.saveResource("rarity.yml", false);
        path = this.getDataFolder().getPath();
        instance = this;
        Config config = new Config(path+"/config.yml", Config.YAML);
        if(config.getInt("version", 0) != 2022082002) {
            updateConfig();
            if(config.exists("registered_tickets")) {
                config.set("registered_tickets", new ArrayList<>());
            }
            config.set("version", 2022082002);
            config.save();
        }
        forceDefaultMode = config.getBoolean("force_default_mode", false);
        default_speed_ticks = config.getInt("default_speed_ticks", 4);
        chest_speed_ticks = config.getInt("chest_speed_ticks", 4);
        banWorlds = new ArrayList<>(config.getStringList("ban_worlds"));
        banWorldPrefix = new ArrayList<>(config.getStringList("ban_worlds_prefixs"));
        show_reward_window = config.getBoolean("show_reward_window", true);
        showType = config.getString("show_type", "actionbar");
        inventory_cache_paths = new ArrayList<>(config.getStringList("inventory_cache_paths"));
        save_bag_enabled = config.getBoolean("save_bag_enabled", true);
        registered_tickets = new ArrayList<>(config.getStringList("registered_tickets"));
        String language = config.getString("language");
        lang = new Lang(new File(this.getDataFolder()+"/languages/"+language+".yml"));
        //this.getServer().getCommandMap().register("", new TestCommand("test"));
        this.getServer().getCommandMap().register("", new MainCommand("lotterybox"));
        Config rarityCfg = new Config(path+"/rarity.yml", Config.YAML);
        for(String key: rarityCfg.getKeys(false)){
            rarities.put(key, new Rarity(rarityCfg.getString(key+".blockParticle", "default")));
        }
        loadBoxesConfig();
        this.getServer().getPluginManager().registerEvents(new EventListeners(), this);
        this.getServer().getPluginManager().registerEvents(new GuiListener(), this);
        if(this.getServer().getPluginManager().getPlugin("Tips") != null){
            this.getLogger().info("Detect Tips Enabled!");
            Api.registerVariables("LotteryBox", VariableTest.class);
        }
        this.getLogger().info("LotteryBox onEnabled!");
    }

    public static class VariableTest extends BaseVariable{

        public VariableTest(Player player) {
            super(player);
        }

        @Override
        public void strReplace() {
            for(String ticket: registered_tickets) {
                this.addStrReplaceString("{lotterybox_tickets_"+ticket+"}", String.valueOf(BasicTool.getTicketCounts(player.getName(), ticket)));
            }
            for(LotteryBox box: lotteryBoxList){
                this.addStrReplaceString("{lotterybox_playtimes_"+box.getName()+"}", String.valueOf(BasicTool.getLotteryPlayTimes(player.getName(), box.getName())));
            }
        }
    }

    public static boolean isWorldAvailable(String level){
        for(String prefix: banWorldPrefix){
            if(level.startsWith(prefix)){
                return false;
            }
        }
        return true;
    }

    public void updateConfig(){
        File folder = new File(path+"/boxes/");
        if(folder.exists()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                Config config = new Config(file, Config.YAML);
                if(!config.exists("weightEnabled")){
                    config.set("weightEnabled", false);
                }
                config.save();
            }
        }
    }

    public static void loadBoxesConfig(){
        File folder = new File(path+"/boxes/");
        lotteryBoxList.clear();
        if(folder.exists()){
            for(File file: Objects.requireNonNull(folder.listFiles())){
                Config config = new Config(file, Config.YAML);
                Map<String, Object> prizesMap = (Map<String, Object>)config.get("prizes");
                List<Prize> prizes = new ArrayList<>();
                for(String key: prizesMap.keySet()){
                    Map<String, Object> subMap = (Map<String, Object>) prizesMap.get(key);
                    List<Item> items = new ArrayList<>();
                    for(String itemString: (List<String>)subMap.get("items")){
                        items.add(Inventory.getItem(itemString));
                    }
                    Prize prize;
                    prize = new Prize(key, (String) subMap.getOrDefault("description", ""), Inventory.getItem((String) subMap.getOrDefault("displayitem", "1:0:1:null")), (Boolean) subMap.getOrDefault("broadcast", true), items.toArray(new Item[0]),(List<String>) subMap.getOrDefault("consolecommands", new ArrayList<>()), (Integer) subMap.getOrDefault("possibility", 5), (Boolean) subMap.getOrDefault("showoriginname", false), (String) subMap.getOrDefault("rarity", "default"));
                    prizes.add(prize);
                }

                Map<String, Object> bonusesMap = (Map<String, Object>)config.get("bonuses");
                List<Bonus> bonuses = new ArrayList<>();
                for(String key: bonusesMap.keySet()){
                    Map<String, Object> subMap = (Map<String, Object>) bonusesMap.get(key);
                    List<Item> items = new ArrayList<>();
                    for(String itemString: (List<String>)subMap.get("items")){
                        items.add(Inventory.getItem(itemString));
                    }
                    bonuses.add(new Bonus(key, items.toArray(new Item[0]), (List<String>) subMap.get("consolecommands"), (Integer) subMap.get("times")));
                }
                LotteryBox lotteryBox = new LotteryBox(file.getName().split("\\.")[0],config.getString("displayName"), config.getStringList("needs"), config.getStringList("descriptions"), prizes, bonuses, config.getInt("permanentLimit"), config.getBoolean("spawnFirework"), config.getString("endParticle"), config.getString("sound", Sound.RANDOM_ORB.getSound()), config.getBoolean("weightEnabled", false));
                lotteryBoxList.add(lotteryBox);
                Server.getInstance().getLogger().info(MainClass.lang.getTranslation("Tips","LotteryBoxLoaded",lotteryBox.getName()));
            }
            Server.getInstance().getLogger().info(MainClass.lang.getTranslation("Tips","LotteryBoxFinish", lotteryBoxList.size()));
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("LotteryBox onDisabled!");
    }
}
