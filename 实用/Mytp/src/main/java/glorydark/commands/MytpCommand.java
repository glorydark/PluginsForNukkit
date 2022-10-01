package glorydark.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Config;
import glorydark.MainClass;
import glorydark.gui.GuiMainAPI;

public class MytpCommand extends Command {
    public MytpCommand(){
        super("mytp","Mytp","/mytp help");
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        if(args.length >= 1) {
            switch (args[0]) {
                case "help":
                    Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
                    if(config.exists("帮助")){
                        if(!config.getStringList("帮助").isEmpty()) {
                            for (String s : config.getStringList("帮助")) {
                                sender.sendMessage(s);
                            }
                        }
                    }else{
                        sender.sendMessage("帮助文件信息缺少,请删除原有config.yml重启服务器!");
                    }
                    break;
                case "open":
                    if (sender instanceof Player) {
                        GuiMainAPI.showMainMenu(sender.getServer().getPlayer(sender.getName()));
                        Config config1 = new Config(MainClass.path+"/config.yml",Config.YAML);
                        if(config1.getBoolean("是否启用打开音效",true)) {
                            sender.getServer().getPlayer(sender.getName()).getLevel().addSound(sender.getServer().getPlayer(sender.getName()), Sound.RANDOM_LEVELUP);
                        }
                    } else {
                        sender.sendMessage("请在游戏内使用本指令!");
                    }
                    break;
                case "添加白名单":
                    if (!(sender instanceof Player)) {
                        if(args.length == 2) {
                            if(MainClass.addtrust(args[1])) {
                                sender.sendMessage("给予玩家【"+args[1]+"】白名单成功!");
                            }else{
                                sender.sendMessage("该玩家已经拥有白名单了！");
                            }
                        }else{
                            sender.sendMessage("请填写玩家名字!");
                        }
                    }else{
                        sender.sendMessage("请在控制台使用本指令!");
                    }
                    break;
                case "删除白名单":
                    if (!(sender instanceof Player)) {
                        if(args.length == 2) {
                            if(MainClass.removetrust(args[1])) {
                                sender.sendMessage("移除玩家【"+args[1]+"】白名单成功!");
                            }else{
                                sender.sendMessage("该玩家没有白名单");
                            }
                        }else{
                            sender.sendMessage("请填写玩家名字!");
                        }
                    }else{
                        sender.sendMessage("请在控制台使用本指令!");
                    }
                    break;
            }
            return true;
        }
        return false;
    }
}
