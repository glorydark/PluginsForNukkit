package glorydark.SimpleDialogues;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.SimpleDialogues.charmsystem.AchievementMain;
import glorydark.SimpleDialogues.charmsystem.CharmPoint;
import glorydark.SimpleDialogues.command.*;
import glorydark.SimpleDialogues.dialogue.DialogueMain;

import java.io.File;
import java.util.HashMap;

public class MainClass extends PluginBase implements Listener {
    public static MainClass plugin;
    public static String path;
    public static HashMap<String, Dialogue> dialoguesMap = new HashMap<>();
    public static HashMap<Player, Dialogue> inDialogues = new HashMap<>();
    public static Boolean charmPoint = true;
    public static Boolean achievement = true;
    public static Boolean ranking = true;
    public static Boolean isCheckStartWindowOn = true;
    public static String showType = "actionbar";

    @Override
    public void onLoad() {
        this.getLogger().info("SimpleDialog V1.0.4 加载中");
        File file = new File(this.getDataFolder()+"/dialogues/");
        file.mkdir();
    }

    @Override
    public void onEnable() {
        this.getLogger().info("SimpleDialog V1.0.4 已被启用");
        plugin = this;
        path = plugin.getDataFolder().getPath();
        saveDefaultConfig();
        Config config = new Config(path+"/config.yml", Config.YAML);
        if(config.getInt("配置版本",0) != 2022051401){
            Tools.fixDialoguesFiles();
        }
        charmPoint = config.getBoolean("是否开启魅力值系统");
        if(charmPoint){
            this.getServer().getCommandMap().register("", new AddCharmPointCommand());
            this.getServer().getCommandMap().register("", new ReduceCharmPointCommand());
            this.getServer().getCommandMap().register("", new SetCharmPointCommand());
            CharmPoint.loadAll();
        }else{
            this.getLogger().alert("检测到您选择禁用魅力值系统，已启动该设置！");
        }
        ranking = config.getBoolean("是否开启排行榜系统");
        if(ranking){
            this.getServer().getCommandMap().register("", new ShowRankingListCommand());
        }else{
            this.getLogger().alert("检测到您选择禁用排行榜系统，已启动该设置！");
        }
        achievement = config.getBoolean("是否开启成就系统");
        if(achievement){
            this.getServer().getCommandMap().register("", new ShowAchievementCommand());
            AchievementMain.loadAll();
        }else{
            this.getLogger().alert("检测到您选择禁用排行榜系统，已启动该设置！");
        }
        showType = config.getString("对话显示方式");
        if(!showType.equals("popup") && !showType.equals("actionbar") && !showType.equals("message") && !showType.equals("tip") && !showType.equals("toast")){
            showType = "tip";
        }
        isCheckStartWindowOn = config.getBoolean("是否开启对话确认开始窗口");
        DialogueMain.loadAll();
        this.getServer().getPluginManager().registerEvents(new EventListener(),this);
        this.getServer().getCommandMap().register("", new PlayDialogueCommand());
        this.getServer().getCommandMap().register("", new QuitDialogueCommand());
        this.getServer().getCommandMap().register("", new ReloadDialogueDataCommand());
        this.getServer().getCommandMap().register("", new HelpCommand());
        this.getServer().getCommandMap().register("", new SaveNbtCommand());
    }

    @Override
    public void onDisable() {
        this.getLogger().info("SimpleDialog V1.0.4 卸载中！");
        DialogueMain.saveAll();
        if (charmPoint) {
            CharmPoint.saveAll();
        }
        if(achievement) {
            AchievementMain.saveAll();
        }
    }

    public static Boolean isAchievementAllowed() {
        return achievement;
    }

    public static Boolean isCharmPointAllowed() {
        return charmPoint;
    }

    public static Boolean isRankingAllowed() {
        return ranking;
    }
}
