package glorydark.SimpleDialogues.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.SimpleDialogues.charmsystem.Window;

public class ShowAchievementCommand extends Command {

    public ShowAchievementCommand() {
        super("对话成就");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("请在游戏内查看！");
        }else{
            Window.showAchievementMenu(((Player) commandSender).getPlayer());
        }
        return true;
    }
}
