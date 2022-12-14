package glorydark;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.gui.GuiListener;
import glorydark.gui.GuiType;
import me.onebone.economyapi.EconomyAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BaseAPI {
    public static Config getAllLang(){
        return new Config(MainClass.path+"/lang.yml");
    }

    public static String getLang(String key, String subKey){
        Map<String, Object> map = getAllLang().getAll();
        Map<String, Object> map1 = (Map<String, Object>) map.get(key);
        if(map1.get(subKey) != null){
            return (String) map1.get(subKey);
        }else{
            return "Key Not Found!";
        }
    }

    public static Integer getWorldPlayerLimit(String level){
        Config cfg = new Config(MainClass.path+"/limit.yml",Config.YAML);
        if(cfg.exists(level)){
            return cfg.getInt(level);
        }else{
            return 99999;
        }
    }

    /*
    public static String getLang(String key){
        return getAllLang().getString(key,"Value Not Found!");
    }

     */

    public static ElementButton buildButton(String name,String path){
        //Debug: Server.getInstance().getLogger().alert(path);
        String[] splits = path.split(":",2);
        if(splits.length >=2){
            switch (splits[0]){
                case "path":
                    return new ElementButton(name,new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH,splits[1]));
                case "url":
                    return new ElementButton(name,new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL,splits[1]));
            }
        }
        return new ElementButton(name);
    }

    public static ElementButtonImageData buildIcon(String path){
        //Debug: Server.getInstance().getLogger().alert(path);
        String[] splits = path.split(":",2);
        if(splits.length >=2){
            switch (splits[0]){
                case "path":
                    return new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH,splits[1]);
                case "url":
                    return new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL,splits[1]);
            }
        }
        return null;
    }

    public static int rand(int min, int max) {
        if (min == max) {
            return max;
        }

        return min + new Random(System.currentTimeMillis()).nextInt(max - min);
    }

    public static void wild(Player p, Boolean free) {
        /*
        if(!((List<String>) BaseAPI.getDefaultConfig("????????????????????????")).contains(p.level.getName())){
            FormWindowSimple returnForm = new FormWindowSimple(getLang("Tips", "menu_default_title"), getLang("Tips", "world_not_allowed"));
            returnForm.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(p, returnForm, GuiType.ErrorMenu);
            return;
        }
         */
        if(((List<String>)BaseAPI.getDefaultConfig(("????????????????????????"))).contains(p.getLevel().getName())) {
            FormWindowSimple returnForm = new FormWindowSimple(getLang("Tips", "menu_default_title"), getLang("Tips", "world_not_allowed"));
            returnForm.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(p, returnForm, GuiType.ErrorMenu);
            return;
        }
        if(!free) {
            double cost = (double) BaseAPI.getDefaultConfig("??????????????????");
            if (cost != 0d) {
                if (EconomyAPI.getInstance().myMoney(p) < cost) {
                    p.sendMessage(getLang("Tips", "short_of_money"));
                    return;
                }
                EconomyAPI.getInstance().reduceMoney(p, cost);
            }
        }
        Location location = getSafePos(p);
        if(location == null) {
            p.sendMessage(getLang("Tips","wild_failed"));
            return;
        }
        if (p.teleport(location)) {
            p.sendMessage(getLang("Tips","wild_success"));
        } else {
            p.sendMessage(getLang("Tips","wild_failed"));
        }
    }

    public static Location getSafePos(Player p){
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        Position pos;
        if(p.getLevel().getName().equals("nether")) {
            pos = p.getLevel().getSafeSpawn(new Vector3(rand(config.getInt("wild_minX"), config.getInt("wild_maxX")), 126, rand(config.getInt("wild_minZ"), config.getInt("wild_maxZ"))));
            if(pos.y <= 32){
                return null;
            }
        }else{
            pos = p.getLevel().getSafeSpawn(new Vector3(rand(config.getInt("wild_minX"), config.getInt("wild_maxX")), 250, rand(config.getInt("wild_minZ"), config.getInt("wild_maxZ"))));
        }
        Position pos1 = pos.clone();
        pos1.y--;
        return pos.getLocation();
    }

    public static Object getDefaultConfig(String key){
        Config cfg = new Config(MainClass.path+"/config.yml",Config.YAML);
        return cfg.get(key);
    }

    public static void setDefaultConfig(String key, Object o){
        Config cfg = new Config(MainClass.path+"/config.yml",Config.YAML);
        cfg.set(key, o);
        cfg.save();
    }

    public static void removeDefaultConfig(String key){
        Config cfg = new Config(MainClass.path+"/config.yml",Config.YAML);
        cfg.remove(key);
        cfg.save();
    }

    public static void setLangConfig(String key, Object o){
        Config cfg = new Config(MainClass.path+"/lang.yml",Config.YAML);
        cfg.set(key, o);
        cfg.save();
    }

    public static void upgradeConfig(){
        int detectVersion = (int) getDefaultConfig("?????????");
        int newestVersion = 20220125;
        if(detectVersion != newestVersion){
            switch (detectVersion){
                /*
                case 20210504:
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Detected the config's version: 20210504");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Start updating the config...");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Stage 1: Updating config.yml[0/6]");
                    setDefaultConfig("??????????????????",new ArrayList<>());
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding: ??????????????????[1/6]");
                    setDefaultConfig("???????????????",new ArrayList<>());
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding: ???????????????[2/6]");
                    setDefaultConfig("?????????????????????",false);
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding: ?????????????????????[3/6]");
                    setDefaultConfig("?????????????????????",0.0);
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding: ?????????????????????[4/6]");
                    setDefaultConfig("??????????????????",0.0);
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding: ??????????????????[5/6]");
                    ArrayList<String> strl = new ArrayList<>();
                    strl.add("------ Mytp Manual ------");
                    strl.add("???????????? /mytp open");
                    strl.add("??????????????? /mytp ??????????????? ???????????? (????????????)");
                    strl.add("??????????????? /mytp ??????????????? ???????????? (????????????)");
                    strl.add("------ Mytp Manual ------");
                    setDefaultConfig("??????",strl);
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Upgrading key: ??????[6/6]");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Stage 1 complete!");
                    //Stage 1
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Stage 2: Updating lang.yml[0/7]");
                    setLangConfig("Tips.world_not_allowed","???????????????????????????!");
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding key: Tips.world_not_allowed[1/7]");
                    setLangConfig("MainMenu.button2_text","??b??l???????????? \n [ ???????????????/????????????????????? ]");
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Fixing key: MainMenu.button2_text[2/7]");
                    setLangConfig("MainMenu.button11_text","??e??l[OP]??????????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding key: MainMenu.button11[3/7]");
                    setLangConfig("MainMenu.button11_pic_path","path:textures/ui/realmsIcon.png");
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding key: MainMenu.button11[4/7]");
                    setLangConfig("SystemSettingMenu.toggle6_text","??e?????????????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding key: MainMenu.button11[5/7]");
                    setLangConfig("SystemSettingMenu.input7_text","??e?????????????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding key: MainMenu.button11[6/7]");
                    setLangConfig("SystemSettingMenu.input8_text","??e??????????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.YELLOW+"Adding key: MainMenu.button11[7/7]");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Stage 2 complete!");
                    setDefaultConfig("?????????",20210805);
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Version has changed to 20210805!");
                    break;

                 */
                case 20210805:
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Detected the config's version: 20210805");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Start updating the config...");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Stage 1: Updating config.yml[0/1]");
                    setDefaultConfig("????????????????????????",new ArrayList<>());
                    setDefaultConfig("?????????????????????",new ArrayList<>());
                    setDefaultConfig("????????????????????????",new ArrayList<>());
                    removeDefaultConfig("??????????????????");
                    removeDefaultConfig("???????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.RED+"???????????????: ??????????????????&?????????????????????????????????????????????????????????config.yml????????????????????????!");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Stage 1 complete!");
                    setDefaultConfig("???????????????????????????",new ArrayList<>());
                    MainClass.plugin.saveResource("limit.yml");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Here is the update description in Chinese!");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+">> 2022???1???25??????????????? <<");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"1. ?????????????????????????????????limit.yml???????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"2. config.yml???????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"3. ??????: ??????????????????&???????????????????????????????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"4. ??????: ??????????????????????????????????????????????????????????????????/???????????????????????????/????????????????????????????????????OP?????????????????????????????????????????????");
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+">> ??????bug????????????????????????????????????????????????:435913429??? <<");
                    setDefaultConfig("?????????",20210125);
                    MainClass.plugin.getLogger().alert(TextFormat.GREEN+"Version has changed to 20210125!");
                    break;
                case 20210125:
                    MainClass.plugin.getLogger().info(TextFormat.RED+"?????????????????????");
                    break;
            }
        }
    }

    public static void teleportToDeathPoint(Player p, Boolean free){
        Config playerconfig = new Config(MainClass.path+"/player/"+ p.getName()+".yml",Config.YAML);
        if(!free) {
            double cost = (double) BaseAPI.getDefaultConfig("?????????????????????");
            if (cost != 0d) {
                if (EconomyAPI.getInstance().myMoney(p) < cost) {
                    p.sendMessage(getLang("Tips", "short_of_money"));
                    return;
                }
                EconomyAPI.getInstance().reduceMoney(p, cost);
            }
        }
        if(playerconfig.exists("lastdeath")){
            p.sendMessage(getLang("Tips","on_teleporting"));
            double x = playerconfig.getDouble("lastdeath.x");
            double y = playerconfig.getDouble("lastdeath.y");
            double z = playerconfig.getDouble("lastdeath.z");
            Level level = p.getServer().getLevelByName(playerconfig.getString("lastdeath.level"));
            if(level != null) {
                p.teleportImmediate(new Location(x, y, z, level));
            }else{
                p.sendMessage(getLang("Tips","world_is_not_loaded"));
            }
        }else{
            FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","deathpoint_not_exist"));
            form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            GuiListener.showFormWindow(p, form, GuiType.ErrorMenu);
        }
    }

    public static void worldteleport(Player p, Level level){
        if(level == null){
            p.sendMessage(getLang("Tips","world_is_not_loaded"));
            return;
        }
        if(p.getServer().getLevels().containsValue(level)) {
            Position spawnpos = level.getSpawnLocation();
            p.teleportImmediate(spawnpos.getLocation());
            p.sendMessage(getLang("Tips","on_teleporting"));
        }else{
            p.sendMessage(getLang("Tips","world_is_not_loaded"));
        }
    }
}