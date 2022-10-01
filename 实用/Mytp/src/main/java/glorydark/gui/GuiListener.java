package glorydark.gui;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.BaseAPI;
import glorydark.MainClass;
import me.onebone.economyapi.EconomyAPI;

import java.io.File;
import java.util.*;

import static glorydark.BaseAPI.*;

public class GuiListener implements Listener {
    public static final HashMap<Player, HashMap<Integer, GuiType>> UI_CACHE = new HashMap<>();
    public static void showFormWindow(Player player, FormWindow window, GuiType guiType) {
        UI_CACHE.computeIfAbsent(player, i -> new HashMap<>()).put(player.showFormWindow(window), guiType);
    }

    @EventHandler()
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event){
        Player p = event.getPlayer();
        FormWindow window = event.getWindow();
        if (p == null || window == null) {
            return;
        }
        GuiType guiType = UI_CACHE.containsKey(p) ? UI_CACHE.get(p).get(event.getFormID()) : null;
        if(guiType == null){
            return;
        }
        UI_CACHE.get(p).remove(event.getFormID());
        if (event.getResponse() == null) {
            return;
        }
        if (event.getWindow() instanceof FormWindowSimple) {
            this.formWindowSimpleOnClick(p, (FormWindowSimple) window, guiType);
        }
        if (event.getWindow() instanceof FormWindowCustom) {
            assert window instanceof FormWindowCustom;
            this.formWindowCustomOnClick(p, (FormWindowCustom) window, guiType);
        }
    }

    private void formWindowCustomOnClick(Player p, FormWindowCustom custom, GuiType guiType) {
        switch (guiType) {
            case HomeDeleteMenu:
                if(custom.getResponse() == null){ return; }
                if(custom.getResponse().getDropdownResponse(0).getElementContent() == null || custom.getResponse().getDropdownResponse(0).getElementContent().equals("")){
                    p.sendMessage(getLang("Tips","settings_error"));
                    return;
                }
                Config wlcfg = new Config(MainClass.path+"/homes/"+p.getName()+".yml",Config.YAML);
                List<String> arr = new ArrayList<>(wlcfg.getStringList("list"));
                arr.remove(custom.getResponse().getDropdownResponse(0).getElementContent());
                wlcfg.set("list",arr);
                wlcfg.save();
                FormWindowSimple form1 = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","remove_home_successfully").replace("%name%",custom.getResponse().getDropdownResponse(0).getElementContent()));
                Config config = new Config(MainClass.path+"/homes/"+p.getName()+".yml");
                List<String> homes = config.getStringList("list");
                homes.remove(custom.getResponse().getDropdownResponse(0).getElementContent());
                config.set("list",homes);
                config.save();
                File homeCfg = new File(MainClass.path+"/homes/"+p.getName()+"/"+custom.getResponse().getDropdownResponse(0).getElementContent()+".yml");
                homeCfg.delete();
                form1.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                showFormWindow(p ,form1, GuiType.ErrorMenu);
                return;

            case HomeCreateMenu:
                Config plc = new Config(MainClass.path+"/homes/"+p.getName()+".yml",Config.YAML);
                Config config1 = new Config(MainClass.path+"/config.yml",Config.YAML);
                if(!p.isOp()){
                    if(plc.getStringList("list").size() >= config1.getInt("最多同时存在家")){
                        FormWindowSimple formsetting = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","home_counts_reach_maxinum"));
                        formsetting.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                        showFormWindow(p,formsetting, GuiType.ErrorMenu);
                        return;
                    }
                }
                double cost = config1.getDouble("设置家花费");
                if(EconomyAPI.getInstance().myMoney(p) < cost){p.sendMessage(getLang("Tips","short_of_money"));return;}
                EconomyAPI.getInstance().reduceMoney(p, cost);
                Config pointc = null;
                if(custom.getResponse().getResponses() == null){ return; }
                if(custom.getResponse().getInputResponse(0) != null && !custom.getResponse().getInputResponse(0).equals("")){
                    pointc = new Config(MainClass.path+"/homes/"+p.getName()+"/"+custom.getResponse().getInputResponse(0)+".yml",Config.YAML);
                }
                for (int i=0;i<4;i++) {
                    if (custom.getResponse().getInputResponse(i) != null && !custom.getResponse().getInputResponse(i).equals("")) {
                        if (i == 2) {
                            assert pointc != null;
                            pointc.set("简介", custom.getResponse().getInputResponse(1));
                        }
                    }
                }
                List<String> plcarr = new ArrayList<>(plc.getStringList("list"));
                plcarr.add(custom.getResponse().getInputResponse(0));
                plc.set("list",plcarr);
                plc.save();
                List<Double> arr1;
                arr1 = new ArrayList<>();
                arr1.add(p.getX());
                arr1.add(p.getY());
                arr1.add(p.getZ());
                assert pointc != null;
                pointc.set("坐标",arr1);
                pointc.set("世界",p.getLevel().getName());
                pointc.save();
                FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","create_home_successfully").replace("%name%",custom.getResponse().getInputResponse(0)).replace("%cost%",String.valueOf(cost)));
                form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                showFormWindow(p, form, GuiType.ErrorMenu);
                return;
            case SettingMenu:
                Config pconfig = new Config(MainClass.path+"/player/"+p.getName()+".yml",Config.YAML);
                if(custom.getResponse() == null){ return; }
                Boolean state = custom.getResponse().getToggleResponse(0);
                pconfig.set("自动接受传送请求",state);
                pconfig.save();
                if(custom.getResponse().getResponse(1) != null && custom.getResponse().getToggleResponse(1)){
                    Config cfg = new Config(MainClass.path+"/config.yml",Config.YAML);
                    cost = cfg.getDouble("设置重生点花费");
                    if(EconomyAPI.getInstance().myMoney(p) < cost){p.sendMessage(getLang("Tips","short_of_money"));return;}
                    EconomyAPI.getInstance().reduceMoney(p, cost);
                    pconfig.set("spawnpoint.x",p.x);
                    pconfig.set("spawnpoint.y",p.y);
                    pconfig.set("spawnpoint.z",p.z);
                    pconfig.set("spawnpoint.level",p.getLevel().getName());
                    pconfig.save();
                    p.getPlayer().setSpawn(p.getPosition());
                }
                FormWindowSimple formsetting = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","settings_saved"));
                formsetting.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                showFormWindow(p,formsetting, GuiType.ErrorMenu);
                break;
            case MainSETTINGMENU:
                FormResponseCustom response = custom.getResponse();
                pconfig = new Config(MainClass.path+"/config.yml",Config.YAML);
                if(response == null){ return; }
                if(response.getInputResponse(0) != null && !response.getInputResponse(0).equals("")){
                    Double homecost = Double.valueOf(response.getInputResponse(0));
                    pconfig.set("传送邀请花费",homecost);
                    pconfig.save();
                }
                if(response.getInputResponse(1) != null && !response.getInputResponse(1).equals("")){
                    Double tpcost = Double.valueOf(response.getInputResponse(1));
                    pconfig.set("设置家花费",tpcost);
                    pconfig.save();
                }
                if(response.getInputResponse(2) != null && !response.getInputResponse(2).equals("")){
                    Double spawncost = Double.valueOf(response.getInputResponse(2));
                    pconfig.set("设置重生点花费",spawncost);
                    pconfig.save();
                }
                if(response.getInputResponse(3) != null && !response.getInputResponse(3).equals("")){
                    int homeMaxCount = Integer.parseInt(response.getInputResponse(3));
                    pconfig.set("最多同时存在家",homeMaxCount);
                    pconfig.save();
                }
                if(response.getResponse(4) != null){
                    Boolean backToLobby = response.getToggleResponse(4);
                    pconfig.set("强制回主城",backToLobby);
                    pconfig.save();
                }
                if(response.getResponse(5) != null){
                    Boolean bool = response.getToggleResponse(5);
                    pconfig.set("开启设置重生点",bool);
                    pconfig.save();
                }
                if(response.getResponse(6) != null && !response.getInputResponse(6).equals("")){
                    pconfig.set("返回死亡点花费",Double.valueOf(response.getInputResponse(6)));
                    pconfig.save();
                }
                if(response.getResponse(7) != null && !response.getInputResponse(7).equals("")){
                    pconfig.set("随机传送花费",Double.valueOf(response.getInputResponse(7)));
                    pconfig.save();
                }
                form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","settings_saved"));
                form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                showFormWindow(p, form, GuiType.ErrorMenu);
                break;
            case WarpsSettingDeleteMenu:
                response = custom.getResponse();
                wlcfg = new Config(MainClass.path+"/warps.yml",Config.YAML);
                arr = new ArrayList<>(wlcfg.getStringList("list"));
                arr.remove(response.getDropdownResponse(0).getElementContent());
                wlcfg.set("list",arr);
                wlcfg.save();
                String root = MainClass.path+"/warps/"+MainClass.editTeleportPoint.get(p)+".yml";
                File file = new File(root);
                file.delete();
                form1 = new FormWindowSimple(getLang("Tips","menu_default_title"), "§e已移除点【"+response.getDropdownResponse(0).getElementContent()+"】");
                form1.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                showFormWindow(p, form1, GuiType.ErrorMenu);
                break;
            case WarpSettingDownMenu:
                response = custom.getResponse();
                String name,world;
                double x,y,z;
                if(response == null){
                    form1 = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","settings_not_changed"));
                    form1.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                    showFormWindow(p, form1, GuiType.ErrorMenu);
                    return;
                }
                Config cfg = new Config(MainClass.path+"/warps/"+MainClass.editTeleportPoint.get(p)+".yml",Config.YAML);
                for (int i=0;i<10;i++){
                    if(response.getInputResponse(i) != null && !response.getInputResponse(i).equals("")){
                        switch (i){
                            case 1:
                                name = response.getInputResponse(1);
                                cfg.set("name", name);
                                break;
                            case 2:
                                x = Double.parseDouble(response.getInputResponse(2));
                                cfg.set("x", x);
                                break;
                            case 3:
                                y = Double.parseDouble(response.getInputResponse(3));
                                cfg.set("y", y);
                                break;
                            case 4:
                                z = Double.parseDouble(response.getInputResponse(4));
                                cfg.set("z", z);
                                break;
                            case 5:
                                world = response.getInputResponse(5);
                                cfg.set("world", world);
                                break;
                            case 6:
                                boolean toggle = response.getToggleResponse(6);
                                cfg.set("state", toggle);
                                break;
                            default:
                                break;
                        }
                    }
                }
                if(response.getInputResponse(7) != null && response.getInputResponse(8) != null) {
                    if(!response.getInputResponse(7).equals("") && !response.getInputResponse(8).equals("")) {
                        cfg.set("title", response.getInputResponse(7) + ":" + response.getInputResponse(8));
                    }
                    if(!response.getInputResponse(7).equals("") && response.getInputResponse(8).equals("")) {
                        cfg.set("title", response.getInputResponse(7));
                    }
                }
                cfg.save();
                form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","settings_saved"));
                form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                showFormWindow(p, form, GuiType.ErrorMenu);
                break;
            case WarpsSettingCreateMenu:
                response = custom.getResponse();
                if(response == null){
                    form1 = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","input_not_all"));
                    form1.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                    showFormWindow(p, form1, GuiType.ErrorMenu);
                    return;
                }
                if(response.getInputResponse(0) == null || response.getInputResponse(0).equals("")){
                    form1 = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","input_wrong"));
                    form1.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                    showFormWindow(p, form1, GuiType.ErrorMenu);
                    return;
                }
                wlcfg = new Config(MainClass.path+"/warps.yml",Config.YAML);
                List<String> Arr = new ArrayList<>(wlcfg.getStringList("list"));
                if(Arr.contains(response.getInputResponse(0))){
                    form1 = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","warppoint_existed"));
                    form1.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                    showFormWindow(p, form1, GuiType.ErrorMenu);
                    return;
                }
                Arr.add(response.getInputResponse(0));
                wlcfg.set("list",Arr);
                wlcfg.save();
                cfg = new Config(MainClass.path+"/warps/"+response.getInputResponse(0)+".yml",Config.YAML);
                if(response.getResponse(6) != null) {
                    Boolean toggle = response.getToggleResponse(6);
                    cfg.set("state", toggle);
                }else{
                    cfg.set("state", true);
                }
                cfg.save();
                for (int i=0;i<10;i++){
                    if(response.getInputResponse(i) != null && !response.getInputResponse(i).equals("")){
                        switch (i){
                            case 1:
                                name = response.getInputResponse(1);
                                cfg.set("name", name);
                                cfg.save();
                                break;
                            case 2:
                                x = Double.parseDouble(response.getInputResponse(2));
                                cfg.set("x", x);
                                cfg.save();
                                break;
                            case 3:
                                y = Double.parseDouble(response.getInputResponse(3));
                                cfg.set("y", y);
                                cfg.save();
                                break;
                            case 4:
                                z = Double.parseDouble(response.getInputResponse(4));
                                cfg.set("z", z);
                                cfg.save();
                                break;
                            case 5:
                                world = response.getInputResponse(5);
                                cfg.set("world", world);
                                cfg.save();
                                break;
                            default:
                                break;
                        }
                    }else{
                        switch (i){
                            case 1:
                                cfg.set("name", "default");
                                cfg.save();
                                break;
                            case 2:
                                x = p.x;
                                cfg.set("x", x);
                                cfg.save();
                                break;
                            case 3:
                                y = p.y;
                                cfg.set("y", y);
                                cfg.save();
                                break;
                            case 4:
                                z = p.z;
                                cfg.set("z", z);
                                cfg.save();
                                break;
                            case 5:
                                cfg.set("world", p.level.getName());
                                cfg.save();
                                break;
                            default:
                                break;
                        }
                    }
                }
                if(response.getInputResponse(7) != null && response.getInputResponse(8) != null) {
                    if(!response.getInputResponse(7).equals("") && !response.getInputResponse(8).equals("")) {
                        cfg.set("title", response.getInputResponse(7) + ":" + response.getInputResponse(8));
                    }else{
                        cfg.set("title", "我是大标题:我是小标题");
                    }
                }else{
                    if(response.getInputResponse(7) != null && response.getInputResponse(8) == null) {
                        if(!response.getInputResponse(7).equals("")) {
                            cfg.set("title", response.getInputResponse(7));
                        }else{
                            cfg.set("title", "我是大标题:我是小标题");
                        }
                    }else{
                        cfg.set("title", "我是大标题:我是小标题");
                    }
                }
                cfg.save();
                form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","warppoint_created").replace("%name%",response.getInputResponse(0)));
                form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                showFormWindow(p,form, GuiType.ErrorMenu);
                break;
        }
    }

    private void formWindowSimpleOnClick(Player p, FormWindowSimple simple, GuiType guiType) {
        FormResponseSimple response = simple.getResponse();
        switch (guiType) {
            case WarpsSettingMenu:
                if(response == null){ return; }
                if(response.getClickedButton().getText().equals("")){ p.sendMessage(getLang("Tips","warppoint_closed")); return;}
                GuiMainAPI.showWarpsSettingDownMenu(p,response.getClickedButton().getText());
                break;
            case MainMenu:
                if(response == null){ return; }
                int buttonId = response.getClickedButtonId();
                switch (buttonId){
                    case 0:
                        GuiMainAPI.showWarpsMenu(p);
                        break;
                    case 1:
                        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
                        if(config.getStringList("禁止玩家传送世界").contains(p.getLevel().getName())) {
                            p.sendMessage(TextFormat.RED+"禁止玩家传送世界");
                            return;
                        }
                        GuiMainAPI.showTeleportMainMenu(p);
                        break;
                    case 2:
                        GuiMainAPI.showPlayerSettingMenu(p);
                        break;
                    case 3:
                        config = new Config(MainClass.path+"/config.yml",Config.YAML);
                        if(config.getStringList("禁止设置家世界").contains(p.getLevel().getName())) {
                            p.sendMessage(TextFormat.RED+"本世界禁止设置家");
                            return;
                        }
                        GuiMainAPI.showHomeMainMenu(p);
                        break;
                    case 4:
                        GuiMainAPI.showWorldListMenu(p);
                        break;
                    case 5:
                        config = new Config(MainClass.path+"/config.yml",Config.YAML);
                        Config playerconfig = new Config(MainClass.path+"/player/"+ p.getName()+".yml",Config.YAML);
                        if(playerconfig.exists("level")) {
                            String levelname = playerconfig.getString("level", Server.getInstance().getDefaultLevel().getName());
                            if(config.getStringList("禁止返回死亡点世界").contains(p.getLevel().getName())) {
                                p.sendMessage(TextFormat.RED+"本世界禁止返回死亡点");
                                return;
                            }
                        }
                        BaseAPI.teleportToDeathPoint(p,false);
                        break;
                    case 6:
                        wild(p,false);
                        break;
                    case 7:
                        if(!MainClass.checktrust(p,true)){ return; }
                        Map<UUID, Player> pl = p.getServer().getOnlinePlayers();
                        List<Player> list = new ArrayList<>(pl.values());
                        for (Player p1 : list) {
                            MainClass.tp(p1,p);
                            p1.sendMessage(getLang("Tips","manager_teleport_msg_to_manager").replace("%player%",p.getName()));
                            p.sendMessage(getLang("Tips","manager_teleport_msg_to_player").replace("%player%",p1.getName()));
                        }
                        break;
                    case 8:
                        GuiMainAPI.showWarpManageSelectMenu(p);
                        break;
                    case 9:
                        GuiMainAPI.showMainSettingMenu(p);
                        break;
                    case 10:
                        GuiMainAPI.showAllWorldTeleportMenu(p);
                        break;
                    default:
                        break;
                }
                break;
            case ManagerWorldMenu:
                if(response == null){ return; }
                BaseAPI.worldteleport(p,Server.getInstance().getLevelByName(response.getClickedButton().getText()));
                break;
            case TeleportMainMenu:
                if(response == null){ return; }
                switch (response.getClickedButtonId()){
                    case 0:
                        GuiMainAPI.showTeleportMenu(p,0);
                        break;
                    case 1:
                        GuiMainAPI.showTeleportMenu(p,1);
                        break;
                }
                break;
            case HomeMainMenu:
                if(response == null){ return; }
                switch(response.getClickedButtonId()){
                    case 0: //create
                        GuiMainAPI.showHomeCreateMenu(p,false);
                        break;
                    case 1: //delete
                        GuiMainAPI.showHomeTeleportMenu(p);
                        break;
                    case 2: //return
                        GuiMainAPI.showHomeDeleteMenu(p);
                        break;
                    case 3: //return
                        GuiMainAPI.showMainMenu(p);
                        break;
                }
                break;
            case TeleportInitiativeMenu:
            case TeleportInitiativeMenuForFree:
                response = simple.getResponse();
                if(response == null){ return; }
                String selected = response.getClickedButton().getText();
                Player selectedp = p.getServer().getPlayer(selected);
                Config pconfig = new Config(MainClass.path+"/player/"+selected+".yml",Config.YAML);
                boolean bool = pconfig.getBoolean("自动接受传送请求");
                if(p.isOp() || bool){ MainClass.tp(p,selectedp); return; }
                GuiMainAPI.showTeleportAcceptMenu(p,selectedp,0,guiType.equals(GuiType.TeleportInitiativeMenuForFree));
                break;
            case ErrorMenu:
                GuiMainAPI.showMainMenu(p);
                break;
            case HomeTeleportMenu:
                if(response == null){ return; }
                String[] strArr = response.getClickedButton().getText().split("\n");
                String[] sArr = strArr[0].split(":");
                p.sendMessage(getLang("Tips","teleport_to_home").replace("%name%",sArr[1]));
                Config pointc = new Config(MainClass.path+"/homes/"+p.getName()+"/"+sArr[1]+".yml",Config.YAML);
                List<Double> position = pointc.getDoubleList("坐标");
                String world = pointc.getString("世界");
                String intro = pointc.getString("简介");
                p.teleportImmediate(new Location(position.get(0),position.get(1),position.get(2),p.getServer().getLevelByName(world)));
                break;
            case WarpMenu: //Warp系统
                if(response == null){ return; }
                if(response.getClickedButton().getText().equals(getLang("Tips","menu_button_return_text"))){
                    GuiMainAPI.showMainMenu(p);
                }else{
                    Config warplistcfg = new Config(MainClass.path+"/warps.yml",Config.YAML);
                    List<String> warplist = new ArrayList<>(warplistcfg.getStringList("list"));
                    selected = warplist.get(response.getClickedButtonId());
                    Config warpconfig = new Config(MainClass.path+"/warps/"+selected+".yml",Config.YAML);
                    String placename = warpconfig.getString("name");
                    double x,y,z;
                    String name;
                    boolean state;
                    if(warpconfig.exists("x")){
                        x = warpconfig.getDouble("x");
                    }else{
                        FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","warppoint_settings_not_comprehensive"));
                        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                        showFormWindow(p, form, GuiType.ErrorMenu);
                        return;
                    }
                    if(warpconfig.exists("y")){
                        y = warpconfig.getDouble("y");
                    }else{
                        FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","warppoint_settings_not_comprehensive"));
                        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                        showFormWindow(p, form, GuiType.ErrorMenu);
                        return;
                    }
                    if(warpconfig.exists("z")){
                        z = warpconfig.getDouble("z");
                    }else{
                        FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","warppoint_settings_not_comprehensive"));
                        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                        showFormWindow(p, form, GuiType.ErrorMenu);
                        return;
                    }
                    if(warpconfig.exists("world")){
                        world = warpconfig.getString("world");
                    }else{
                        FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","warppoint_settings_not_comprehensive"));
                        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                        showFormWindow(p, form, GuiType.ErrorMenu);
                        return;
                    }
                    /* if(warpconfig.exists("name")){
                        name = warpconfig.getString("name");
                    }else{
                        FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), "§e该传送点未设置完毕，无法进行传送");
                        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                        showFormWindow(p, form, guitype.ErrorMenu);
                        return;
                    } */
                    if(warpconfig.exists("state")){
                        state = warpconfig.getBoolean("state");
                    }else{
                        FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","warppoint_settings_not_comprehensive"));
                        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                        showFormWindow(p, form, GuiType.ErrorMenu);
                        return;
                    }
                    if(!state) {
                        FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","warppoint_settings_not_comprehensive"));
                        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                        showFormWindow(p, form, GuiType.ErrorMenu);
                        return;
                    }
                    p.sendMessage(getLang("Tips","warppoint_teleport").replace("%name",placename));
                    if (!p.getServer().isLevelGenerated(world)){
                        p.sendMessage("WarpMenu");
                    }
                    p.teleportImmediate(new Location(x,y,z,p.getServer().getLevelByName(world)));
                    if(warpconfig.exists("title")){
                        String[] strings = warpconfig.getString("title").split(":");
                        if(strings.length==1){
                            p.sendTitle(strings[0]);
                        }else{
                            p.sendTitle(strings[0],strings[1]);
                        }
                    }
                    return;
                }
                break;
        case WarpsSettingManageMenu:
                if(response == null){ return; }
                int manageselected = response.getClickedButtonId();
                switch (manageselected){
                    case 0:
                        GuiMainAPI.showWarpSettingMenu(p);
                        break;
                    case 1:
                        GuiMainAPI.showWarpsCreateMenu(p);
                        break;
                    case 2:
                        GuiMainAPI.showWarpsDeleteMenu(p);
                        break;
                    case 3:
                        GuiMainAPI.showMainMenu(p);
                    default:
                        return;
                }
                break;
            case TeleportPassiveMENU: //传送系统
            case TeleportPassiveMENUForFree:
                if(response == null){ return; }
                String selectedtext = response.getClickedButton().getText();
                selectedp = p.getServer().getPlayer(selectedtext);
                pconfig = new Config(MainClass.path+"/player/"+selectedtext+".yml",Config.YAML);
                bool = pconfig.getBoolean("自动接受传送请求");
                if(p.isOp() || bool){ MainClass.tp(selectedp,p); return; }
                GuiMainAPI.showTeleportAcceptMenu(p, selectedp, 1, guiType.equals(GuiType.TeleportPassiveMENUForFree));
                break;
            case AcceptListInitiativeMENU: //接受邀请
                if(response == null){ return; }
                int AcceptbuttonId = response.getClickedButtonId();
                String name = response.getClickedButton().getText();
                if(AcceptbuttonId == 0){
                    MainClass.tp(p.getServer().getPlayer(name),p);
                }
                break;
            case AcceptListPassiveMENU: //将玩家传送到你 你接受
                if(response == null){ return; }
                AcceptbuttonId = response.getClickedButtonId();
                name = response.getClickedButton().getText();
                if(AcceptbuttonId == 0){
                    MainClass.tp(p, p.getServer().getPlayer(name));
                }
                break;
            case WorldTeleportMenu:
                if(response == null){ return; }
                if(response.getClickedButton().getText().equals(getLang("Tips","menu_button_return_text"))) {
                    GuiMainAPI.showMainMenu(p);
                }else{
                    Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
                    List<String> whiteWorld = new ArrayList<>(config.getStringList("世界白名单"));
                    Collection<Level> levels = Server.getInstance().getLevels().values();
                    LinkedList<String> finals = new LinkedList<>();
                    for(Level level: levels){
                        finals.add(level.getName());
                    }
                    String target = finals.get(response.getClickedButtonId());
                    // 如果 白名单包含此 或 为白名单用户 或 为本世界
                    if(whiteWorld.contains(target) || MainClass.checktrust(p,false) || target.equals(Server.getInstance().getDefaultLevel().getName())){
                        Level level = p.getServer().getLevelByName(target);
                        if(level.getPlayers().values().size() >= BaseAPI.getWorldPlayerLimit(target)){
                            p.sendMessage(TextFormat.RED+"该世界人数已满！");
                        }else {
                            BaseAPI.worldteleport(p, level);
                        }
                    }else{
                        p.sendMessage(TextFormat.RED+"该世界未开放！");
                    }
                }
                break;
        }
    }
}
