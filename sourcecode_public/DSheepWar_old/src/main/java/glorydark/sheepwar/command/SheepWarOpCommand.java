package glorydark.sheepwar.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import glorydark.sheepwar.settings.SetGameInit;

public class SheepWarOpCommand extends cn.nukkit.command.Command {
    public SheepWarOpCommand() {
        super("sheepwaradmin");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender.isPlayer())){
            commandSender.sendMessage("请在游戏内执行！");
            return true;
        }
        if(!commandSender.isOp()){
            commandSender.sendMessage("您没有该权限！");
            return true;
        }
        if(strings.length > 0){
            switch (strings[0]){
                case "设置":
                case "set":
                    if(strings.length > 1){
                        Level level = Server.getInstance().getLevelByName(strings[1]);
                        if(level != null){
                            commandSender.sendMessage("您已进入设置模式！");
                            SetGameInit.addPlayer(((Player) commandSender).getPlayer(), level.getName());
                        }
                    }
                    break;
            }
        }else{
            return false;
        }
        return true;
    }
}
