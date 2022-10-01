package glorydark.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import glorydark.MainClass;

import static glorydark.BaseAPI.getLang;

public class WorldListCommand extends Command {
    public WorldListCommand(){
        super("wl","Multi-world Teleport Command","/wl");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(MainClass.checktrust((Player) commandSender,false) || !(commandSender instanceof Player)) {
            commandSender.sendMessage("已加载世界:");
            for(Level level: Server.getInstance().getLevels().values()){
                commandSender.sendMessage("-" + level.getName());
            }
        }else{
            commandSender.sendMessage("您没有权限！");
        }
        return true;
    }
}
