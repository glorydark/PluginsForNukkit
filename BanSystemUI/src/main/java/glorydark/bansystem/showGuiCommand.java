package glorydark.bansystem;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.bansystem.gui.GuiMain;

public class showGuiCommand extends Command {
    public showGuiCommand() {
        super("report","Start to use it!", "/report");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(commandSender.isOp()){
                GuiMain.showManageMainMenu((Player) commandSender);
            }else{
                GuiMain.showPlayerMainMenu((Player) commandSender);
            }
        }else{
            commandSender.sendMessage("请在游戏内使用！");
        }
        return true;
    }
}
