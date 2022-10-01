package glorydark.bansystem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.bansystem.gui.GuiMain;

public class quitFollowCommand extends Command {
    public quitFollowCommand() {
        super("退出跟随","退出跟随", "/退出跟随");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(commandSender.isOp()){
                if(BindTask.bindPlayers.contains(commandSender)){
                    BindTask.bindPlayers.remove(Server.getInstance().getPlayer(commandSender.getName()));
                    commandSender.sendMessage("退出跟随成功！");
                }else{
                    commandSender.sendMessage("您还未跟随任何人！");
                }
            }else{
                commandSender.sendMessage("您没有权限！");
            }
        }else{
            commandSender.sendMessage("请在游戏内使用！");
        }
        return true;
    }
}
