package glorydark.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import glorydark.BaseAPI;
import glorydark.MainClass;
import glorydark.gui.GuiMainAPI;

import static glorydark.BaseAPI.getLang;

public class WorldTpCommand extends Command {
    public WorldTpCommand(){
        super("w","Multi-world Teleport Command","/w 世界名");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(MainClass.checktrust((Player) commandSender,false)) {
                if (strings.length >= 1) {
                    Level level = Server.getInstance().getLevelByName(strings[0]);
                    if (level != null) {
                        BaseAPI.worldteleport((Player) commandSender, level);
                    } else {
                        commandSender.sendMessage(getLang("Tips", "world_is_not_loaded"));
                    }
                    return true;
                } else {
                    return false;
                }
            }else{
                commandSender.sendMessage("您没有权限！");
                return true;
            }
        }else{
            commandSender.sendMessage("请在游戏内进行");
            return true;
        }
    }
}
