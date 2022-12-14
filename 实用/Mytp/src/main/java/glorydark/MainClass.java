package glorydark;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.commands.MytpCommand;
import glorydark.commands.WildCommand;
import glorydark.commands.WorldListCommand;
import glorydark.commands.WorldTpCommand;
import glorydark.event.EventListener;
import glorydark.gui.GuiListener;
import glorydark.gui.GuiType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import static glorydark.BaseAPI.buildButton;
import static glorydark.BaseAPI.getLang;

@SuppressWarnings("ALL")
public class MainClass extends PluginBase implements Listener {
    public static final int MainMenu = 100100;
    public static final int TeleportInitiativeMENU = 120100;
    public static final int TeleportPassiveMENU = 120101;
    public static final int AcceptListInitiativeMENU = 120102;
    public static final int AcceptListPassiveMENU = 120103;
    public static final int WarpMenu = 120104;
    public static final int SETTINGMENU = 120105;
    public static final int ErrorMenu = 120999;
    public static final int WarpsSettingMenu = 120106;
    public static final int WarpSettingDownMenu = 120107;
    public static final int WarpsSettingManageMenu = 120108;
    public static final int WarpsSettingCreateMenu = 120109;
    public static final int WarpsSettingDeleteMenu = 120110;
    public static final int HomeMainMenu = 120111;
    public static final int HomeTeleportMenu = 120112;
    public static final int HomeDeleteMenu = 120113;
    public static final int HomeCreateMenu = 120114;
    public static final int MainSETTINGMENU = 120115;
    public static final int WorldTeleportMenu = 120116;
    public static String path = null;
    public static MainClass plugin;
    public static Timer timer = new Timer();
    public static List<Player> godPlayer = new ArrayList<>();
    public static HashMap<Player, String> editTeleportPoint = new HashMap<>();

    @Override
    public void onLoad(){
        this.getLogger().info("DEssential Onloaded!");
        path = this.getDataFolder().getPath();
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this); // ??????Event
        this.getServer().getPluginManager().registerEvents(new GuiListener(), this); // ??????????????????Event
        this.getServer().getPluginManager().registerEvents(new EventListener(), this); // ??????????????????Event
        this.getServer().getCommandMap().register("",new MytpCommand());
        this.getServer().getCommandMap().register("",new WildCommand());
        this.getServer().getCommandMap().register("",new WorldListCommand());
        this.getServer().getCommandMap().register("",new WorldTpCommand());
        this.getLogger().info("DEssential Enabled!");
        this.saveResource("config.yml",false);
        this.saveResource("lang.yml",false);
        BaseAPI.upgradeConfig();
        loadLevel();
        Config config = new Config(path+"/config.yml",Config.YAML);
        if(!config.exists("?????????????????????")){
            config.set("?????????????????????","1000.000000");
        }
        if(!config.exists("???????????????")){
            config.set("???????????????",false);
            config.save();
        }
        if(!config.exists("????????????")){
            config.set("????????????","null");
            config.save();
        }
        if(!config.exists("?????????")){
            config.set("?????????",20210530);
        }else{
            if(!config.getString("?????????").equals("20210530")){
                ArrayList<String> strl = new ArrayList<String>();
                strl.add("------ Mytp Manual ------");
                strl.add("???????????? /mytp open");
                strl.add("??????????????? /mytp ??????????????? ???????????? (????????????)");
                strl.add("??????????????? /mytp ??????????????? ???????????? (????????????)");
                strl.add("------ Mytp Manual ------");
                config.set("??????", strl);
            }
        }
        if(!config.exists("??????")) {
            ArrayList<String> strl = new ArrayList<String>();
            strl.add("------ Mytp Manual ------");
            strl.add("???????????? /mytp open");
            strl.add("??????????????? /mytp ??????????????? ???????????? (????????????)");
            strl.add("??????????????? /mytp ??????????????? ???????????? (????????????)");
            strl.add("------ Mytp Manual ------");
            config.set("??????", strl);
        }
        if(!config.exists("????????????????????????")){
            config.set("????????????????????????", true);
        }
        if(!config.exists("????????????ID")){
            config.set("????????????ID", 347);
        }
        if(!config.exists("????????????????????????")){
            config.set("????????????????????????", true);
        }
        if(!config.exists("?????????????????????")){
            config.set("?????????????????????", 10);
        }
        if(!config.exists("??????????????????")){
            config.set("??????????????????", false);
        }
        config.save();
    }

    public void loadLevel(){
        for(String worldName: getWorlds()){
            if(!this.getServer().isLevelLoaded(worldName)){
                if(this.getServer().isLevelGenerated(worldName)){
                    this.getLogger().info("???????????????????????????:"+worldName);
                    this.getServer().loadLevel(worldName);
                }
            }
        }
    }

    public ArrayList<String> getWorlds(){
        ArrayList<String> worlds = new ArrayList<>();
        File file = new File(this.getServer().getFilePath()+"/worlds");
        File[] s = file.listFiles();
        if(s != null) {
            for (File file1 : s) {
                if (file1.isDirectory()) {
                    worlds.add(file1.getName());

                }
            }
        }
        return worlds;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DEssential Disabled!");
    }

    public static Boolean addtrust(String pn){
        Config trustlist = new Config(path+"/trust.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if(arrayList.contains(pn)) {
            return false;
        }else{
            arrayList.add(pn);
            trustlist.set("list",arrayList);
            trustlist.save();
            return true;
        }
    }
    public static Boolean removetrust(String pn){
        Config trustlist = new Config(path+"/trust.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if(arrayList.contains(pn)) {
            for (int i = 0;i<arrayList.size();i++){
                if(arrayList.get(i).equals(pn)){
                    arrayList.remove(i);
                    trustlist.set("list",arrayList);
                    trustlist.save();
                }
            }
            return true;
        }else{
            return false;
        }
    }

    public static void tp(Player asker, Player player){ //??????????????????????????????????????????
        if(!player.isOnline()){ return; }
        if(!asker.isOnline()){ return; }
        Level level = player.getLevel();
        if(!asker.getServer().isLevelGenerated(level.getName())){
            player.sendMessage(getLang("Tips","world_is_not_loaded"));
            return;
        }
        asker.teleportImmediate(player.getPosition().getLocation());
        asker.sendMessage(getLang("Tips","teleport_to_player").replace("%player%",player.getName()));
    }

    public static boolean checktrust(Player p,Boolean openGui){
        Config trustlist = new Config(path+"/trust.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if(arrayList.contains(p.getName())) {
            return true;
        }else{
            if(openGui){
                FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","operation_is_not_authorized"));
                form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                GuiListener.showFormWindow(p, form, GuiType.ErrorMenu);
            }
            return false;
        }
    }
}