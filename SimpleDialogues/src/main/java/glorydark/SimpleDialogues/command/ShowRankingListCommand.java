package glorydark.SimpleDialogues.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.SimpleDialogues.charmsystem.Window;

public class ShowRankingListCommand extends Command {

    public ShowRankingListCommand() {
        super("魅力值排行榜");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("请在游戏内查看!");
        }else{
            Window.showRankingList(((Player) commandSender).getPlayer());
        }
        return true;
    }
}
