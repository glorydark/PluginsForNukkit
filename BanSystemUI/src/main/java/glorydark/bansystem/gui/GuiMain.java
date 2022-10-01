package glorydark.bansystem.gui;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import glorydark.bansystem.MainClass;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuiMain {
    public static void showManageMainMenu(Player player){
        FormWindowSimple window = new FormWindowSimple("封禁系统","您好，【管理员】"+player.getName()+",请选择你所需要的功能！");
        window.addButton(new ElementButton("操作玩家"));
        window.addButton(new ElementButton("处理举报"));
        window.addButton(new ElementButton("跟随玩家"));
        /*
        window.addButton(new ElementButton("发送邮件"));
         */
        GuiListener.showFormWindow(player,window, GuiType.ManagerMain);
    }

    public static void showManagerBindPlayerMenu(Player player){
        FormWindowSimple window = new FormWindowSimple("封禁系统 - 跟随玩家","选择您需要跟随的玩家!");
        List<Player> onlinePlayers = new ArrayList<>(Server.getInstance().getOnlinePlayers().values());
        onlinePlayers.sort(Comparator.comparing(Player::getName));
        for(Player player1:onlinePlayers){
            if(!player1.equals(player)){
                window.addButton(new ElementButton(player1.getName()));
            }
        }
        if(onlinePlayers.size() < 2){
            window.setContent("当前没有在线玩家！");
        }
        GuiListener.showFormWindow(player,window, GuiType.ManagerBindPlayer);
    }

    public static void showManagerManagePlayerMenu(Player player){
        FormWindowSimple window = new FormWindowSimple("封禁系统 - 操作玩家","您好，【管理员】"+player.getName()+",请选择你所需要的功能！");
        window.addButton(new ElementButton("封禁玩家"));
        window.addButton(new ElementButton("解禁玩家"));
        window.addButton(new ElementButton("踢出玩家"));
        GuiListener.showFormWindow(player,window, GuiType.ManagerPlayerMinor);
    }

    public static void showManagerKickMenu(Player player){
        FormWindowCustom window = new FormWindowCustom("封禁系统 - 踢出玩家");
        ElementDropdown dropdown = new ElementDropdown("请选择被踢出的玩家");
        for(Player p:Server.getInstance().getOnlinePlayers().values()){
            dropdown.addOption(p.getName());
        }
        window.addElement(dropdown);
        window.addElement(new ElementInput("理由"));
        GuiListener.showFormWindow(player,window, GuiType.ManagerKickPlayer);
    }

    public static void showManagerBanMenu(Player player){
        FormWindowCustom window = new FormWindowCustom("封禁系统 - 封禁玩家");
        window.addElement(new ElementInput("请输入封禁的玩家名称"));
        window.addElement(new ElementInput("理由"));
        window.addElement(new ElementSlider("封禁时间(年)",0,99,1));
        window.addElement(new ElementSlider("封禁时间(月)",0,12,1));
        window.addElement(new ElementSlider("封禁时间(日)",0,30,1));
        window.addElement(new ElementSlider("封禁时间(时)",0,24,1));
        window.addElement(new ElementSlider("封禁时间(分)",0,60,1));
        window.addElement(new ElementSlider("封禁时间(秒)",0,60,1));
        GuiListener.showFormWindow(player,window, GuiType.ManagerBanPlayer);
    }

    public static void showManagerRemoveBanMenu(Player player){
        FormWindowCustom window = new FormWindowCustom("封禁系统 - 解禁玩家");
        ElementDropdown toggle = new ElementDropdown("请选择解禁的玩家");
        File file = new File(MainClass.path+"/players/");
        for(File file1:file.listFiles()){
            if(file1.isFile()){
                Config config = new Config(file1.getPath(),Config.JSON);
                if(config.exists("pardonDate")){
                    toggle.addOption(file1.getName().split("\\.")[0]);
                }
            }
        }
        window.addElement(toggle);
        window.addElement(new ElementInput("理由"));
        GuiListener.showFormWindow(player,window, GuiType.ManagerRemoveBanPlayer);
    }

    public static void showPlayerMainMenu(Player player){
        FormWindowSimple window = new FormWindowSimple("封禁系统","您好，[%level%] %player%,请选择您需要的功能");
        window.addButton(new ElementButton("举报玩家"));
        /*
        window.addButton(new ElementButton("通知"));

         */
        GuiListener.showFormWindow(player,window,GuiType.PlayerMain);
    }

    public static void showManagerReportListMenu(Player player){
        FormWindowSimple window = new FormWindowSimple("封禁系统 - 处理举报","下面是所有未处理的举报！");
        Config config = new Config(MainClass.path+"/reportRecord.json",Config.JSON);
        for(String key : config.getKeys(false)){
            Map<String,Object> map = (Map<String, Object>) config.get(key);
            window.addButton(new ElementButton(map.get("类型") + "\n" + map.get("举报时间")));
        }
        GuiListener.showFormWindow(player,window,GuiType.ManagerReportListMenu);
    }

    public static void showManagerReportDealMenu(Player player,String selected){
        FormWindowCustom window = new FormWindowCustom("封禁系统 - 处理举报 - 查看详情");
        Config config = new Config(MainClass.path+"/reportRecord.json",Config.JSON);
        if(config.exists(selected)){
            Map<String,Object> map = (Map<String, Object>) config.get(selected);
            String append = "举报类型:"+map.get("类型") + "\n举报时间:"+map.get("举报时间") + "\n被举报人:"+map.get("被举报人");
            if(!Boolean.valueOf(String.valueOf(map.get("是否匿名举报")))){
                append += "\n举报人" + map.get("举报人");
            }
            window.addElement(new ElementLabel(append));
            ElementDropdown dropdown = new ElementDropdown("处理方式");
            Config cfg = new Config(MainClass.path+"/config.yml");
            List<String> strings = new ArrayList<>(cfg.getStringList("处理方式"));
            if(strings != null && strings.size() > 0) {
                for (String s: strings){
                    dropdown.addOption(s);
                }
            }
            window.addElement(dropdown);
            window.addElement(new ElementInput("理由"));
            GuiListener.showFormWindow(player,window,GuiType.ManagerReportDealMenu);
        }
    }

    public static void showPlayerReportMenu(Player player){
        FormWindowCustom window = new FormWindowCustom("封禁系统 - 举报玩家");
        window.addElement(new ElementInput("请输入你要举报的玩家"));
        ElementDropdown dropdown = new ElementDropdown("请选择举报类型");
        Config config = new Config(MainClass.path+"/config.yml");
        List<String> strings = new ArrayList<>(config.getStringList("举报类型"));
        if(strings != null && strings.size() > 0) {
            for (String s: strings){
                dropdown.addOption(s);
            }
        }
        window.addElement(dropdown);
        window.addElement(new ElementInput("理由"));
        window.addElement(new ElementToggle("是否匿名举报"));
        GuiListener.showFormWindow(player,window, GuiType.PlayerReport);
    }

    public static void showPlayerEmailMenu(Player player){
        FormWindowSimple window = new FormWindowSimple("封禁系统 - 通知","这里可以查看通知");
        Config config = new Config(MainClass.path+"/emails/"+player+".yml");
        if(config.getKeys(false).size() >= 0){
            for(String key: config.getKeys(false)){
                window.addButton(new ElementButton(config.getString(key+".title")));
            }
        }
        GuiListener.showFormWindow(player,window,GuiType.PlayerEmail);
    }

    public static void showReturnMenu(Player player,String content, GuiType guiType){
        FormWindowModal window = new FormWindowModal("提示",content,"返回","取消");
        GuiListener.showFormWindow(player,window,guiType);
    }
}
