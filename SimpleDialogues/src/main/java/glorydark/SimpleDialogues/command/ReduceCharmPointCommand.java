package glorydark.SimpleDialogues.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.charmsystem.CharmPoint;

public class ReduceCharmPointCommand extends Command {

    public ReduceCharmPointCommand() {
        super("减少魅力值", "减少魅力值", "/减少魅力值 玩家名 魅力值数量");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(strings.length < 1){ return false;}
        if(!(commandSender instanceof Player)) {
            if(strings.length < 2){ return false;}
            Player player = Server.getInstance().getPlayer(strings[0]);
            if(player != null && player.isOnline()){
                CharmPoint.reduceCharmPoint(player, Double.valueOf(strings[1]));
            }else{
                commandSender.sendMessage(TextFormat.RED+"找不到玩家！");
            }
        }else{
            if(commandSender.isOp()){
                if(strings.length < 2){ return false;}
                Player player = Server.getInstance().getPlayer(strings[0]);
                if(player != null && player.isOnline()){
                    CharmPoint.reduceCharmPoint(player, Double.valueOf(strings[1]));
                }else{
                    commandSender.sendMessage(TextFormat.RED+"找不到玩家！");
                }
            }else {
                commandSender.sendMessage(TextFormat.RED + "您没有权限！");
            }
        }
        return true;
    }
}
