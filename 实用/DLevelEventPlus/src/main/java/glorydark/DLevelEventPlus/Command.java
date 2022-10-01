package glorydark.DLevelEventPlus;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.DLevelEventPlus.gui.GuiMain;
import glorydark.DLevelEventPlus.utils.ConfigUtil;
import glorydark.DLevelEventPlus.utils.DefaultConfigUtils;

import java.io.File;

public class Command extends cn.nukkit.command.Command {
    public Command(String name) {
        super(name,"§e世界保护插件","/dwp");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender.isPlayer()) || (sender.isPlayer() && (ConfigUtil.isAdmin((Player) sender) || ConfigUtil.isOperator((Player) sender, ((Player) sender).getLevel())))){
            switch (args.length){
                case 0:
                    if(sender.isPlayer()){
                        GuiMain.showMainMenu((Player) sender);
                    }else{
                        sender.sendMessage("§a[DLevelEventPlus] 请在游戏内使用!");
                    }
                    return true;
                case 1:
                    if(sender.isPlayer() && !ConfigUtil.isAdmin((Player) sender)){
                        sender.sendMessage("§c[DLevelEventPlus] 您没有权限！");
                        return false;
                    }
                    switch (args[0]){
                        case "save":
                            MainClass.saveAllConfig();
                            return true;
                        case "reload":
                            MainClass.loadAllLevelConfig();
                            MainClass.loadTemplateConfig();
                            MainClass.loadLang();
                            MainClass.show_actionbar_text = new cn.nukkit.utils.Config(MainClass.path+"/config.yml", cn.nukkit.utils.Config.YAML).getBoolean("show_actionbar_text", false);
                            return true;
                        case "fixall":
                            File world_folder = new File(MainClass.path + "/worlds/");
                            File template_folder = new File(MainClass.path + "/templates/");
                            File[] worldsFiles = world_folder.listFiles();
                            if(worldsFiles != null) {
                                for (File file : worldsFiles) {
                                    if(DefaultConfigUtils.isYaml(file.getName())){
                                        MainClass.defaultConfigUtils.checkAll(file.getName(), new Config(file, Config.YAML));
                                    }
                                }
                            }

                            File[] TemplateFiles = template_folder.listFiles();
                            if(TemplateFiles != null) {
                                for (File file : TemplateFiles) {
                                    if(DefaultConfigUtils.isYaml(file.getName())){
                                        MainClass.defaultConfigUtils.checkAll(file.getName(), new Config(file, Config.YAML));
                                    }
                                }
                            }
                            return true;
                    }
                    break;
                case 2:
                    if(sender.isPlayer() && !ConfigUtil.isAdmin((Player) sender)){
                        sender.sendMessage("§c[DLevelEventPlus] 您没有权限！");
                        return false;
                    }
                    if(args[0].equals("addworld")){
                        if(MainClass.defaultConfigUtils.writeAll(0, args[1])){
                            sender.sendMessage("§a[DLevelEventPlus] 创建成功!");
                        }else{
                            sender.sendMessage("§c[DLevelEventPlus] 创建失败!");
                        }
                        return true;
                    }
                    break;
                case 3:
                    if(sender.isPlayer()){
                        sender.sendMessage("§c[DLevelEventPlus] 您没有权限！");
                        return false;
                    }
                    if(args[0].equals("admin")){
                        switch (args[1]){
                            case "add":
                                ConfigUtil.adminList(sender,0,args[2]);
                                return true;
                            case "del":
                                ConfigUtil.adminList(sender,1,args[2]);
                                return true;
                        }
                    }
                    break;
                case 4:
                    if(sender.isPlayer() && !ConfigUtil.isAdmin((Player) sender)){
                        sender.sendMessage("§c[DLevelEventPlus] 您没有权限！");
                        return false;
                    }
                    switch (args[0]) {
                        case "operatorlist":
                            switch (args[1]){
                                case "add":
                                    ConfigUtil.operatorList(sender, 0,args[2],args[3]);
                                    return true;
                                case "del":
                                    ConfigUtil.operatorList(sender, 1,args[2],args[3]);
                                    return true;
                            }
                        case "whitelist":
                            switch (args[1]){
                                case "add":
                                    ConfigUtil.whiteList(sender,0,args[2],args[3]);
                                    return true;
                                case "del":
                                    ConfigUtil.whiteList(sender, 1,args[2],args[3]);
                                    return true;
                            }
                            break;
                    }
                    break;
            }
            sendCommandUsage(sender);
        }
        return true;
    }

    public void sendCommandUsage(CommandSender p){
        //dgamerule operatorlist add/del xx XX
        //dgamerule whitelist add/del xx
        //dgamerule addworld xx
        for(String string: ConfigUtil.getLangList("Help")) {
            p.sendMessage(TextFormat.YELLOW + string);
        }
    }
}