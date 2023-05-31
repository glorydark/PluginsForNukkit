package glorydark.DTipSystem.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import glorydark.DTipSystem.title;

public class command extends Command {
    public command(String name) {
        super(name, TextFormat.YELLOW+"DTipSystem", "/dts");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player && !commandSender.isOp()){ commandSender.sendMessage("您没有权限！");return true; }
        if(strings.length == 0){ return false;}
        switch (strings[0]){
            case "title":
                if(strings.length == 7){
                    Player p = commandSender.getServer().getPlayer(strings[1]);
                    if(p == null){commandSender.sendMessage("该玩家不在线！");return true;}
                    if(p.isOnline()) {
                        title.addTitle(p, strings[2], strings[3], Integer.parseInt(strings[4]), Integer.parseInt(strings[5]), Integer.parseInt(strings[6]));
                    }else{
                        commandSender.sendMessage("该玩家不在线！");
                    }
                }else{
                    commandSender.sendMessage("/dts title player title subtitle fadein fadeout duration");
                }
                break;
            case "popup":
                if(strings.length == 4){
                    Player p = commandSender.getServer().getPlayer(strings[1]);
                    if(p == null){commandSender.sendMessage("该玩家不在线！");return true;}
                    if(p.isOnline()) {
                        title.addPopup(p,strings[2],strings[3]);
                    }else{
                        commandSender.sendMessage("该玩家不在线！");
                    }
                }else{
                    commandSender.sendMessage("/dts popup player title subtitle");
                }
                break;
            case "message":
                if(strings.length == 3){
                    Player p = commandSender.getServer().getPlayer(strings[1]);
                    if(p == null){commandSender.sendMessage("该玩家不在线！");return true;}
                    if(p.isOnline()) {
                        title.sendMessage(p,strings[2]);
                    }else{
                        commandSender.sendMessage("该玩家不在线！");
                    }
                }else{
                    commandSender.sendMessage("/dts message content");
                }
                break;
            case "actionbar":
                if(strings.length == 6){
                    Player p = commandSender.getServer().getPlayer(strings[1]);
                    if(p == null){commandSender.sendMessage("该玩家不在线！");return true;}
                    if(p.isOnline()) {
                        title.addActionBar(p,strings[2],Integer.parseInt(strings[3]),Integer.parseInt(strings[4]),Integer.parseInt(strings[5]));
                    }else{
                        commandSender.sendMessage("该玩家不在线！");
                    }
                }else{
                    commandSender.sendMessage("/dts actionbar player content fadein fadeout duration");
                }
                break;
            case "titletoall":
                if(strings.length == 6){
                    for(Player p: commandSender.getServer().getOnlinePlayers().values()) {
                        title.addTitle(p,strings[1], strings[2], Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]));
                    }
                }else{
                    commandSender.sendMessage("/dts titletoall title subtitle fadein fadeout duration");
                }
                break;
            case "help":
                commandSender.sendMessage("--- DTipSystem 指令帮助 ---");
                commandSender.sendMessage("向一名玩家展示title /dts title player title subtitle fadein fadeout duration");
                commandSender.sendMessage("向一名玩家展示popup /dts popup player title subtitle");
                commandSender.sendMessage("向一名玩家展示message /dts message content");
                commandSender.sendMessage("向一名玩家展示actionbar /dts actionbar player content fadein fadeout duration");
                commandSender.sendMessage("向全体玩家展示title /dts titletoall title subtitle fadein fadeout duration");
                break;
        }
        return true;
    }
}
