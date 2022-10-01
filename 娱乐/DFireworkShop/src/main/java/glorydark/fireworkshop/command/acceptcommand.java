package glorydark.fireworkshop.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import glorydark.fireworkshop.MainClass;
import glorydark.fireworkshop.gui.GuiMain;

public class acceptcommand extends Command {
    public acceptcommand(String name) {
        super(name,"§e烟花商店接受邀请指令","/fsaccept");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player)) { commandSender.sendMessage("请在游戏内执行此指令"); return true;}
        if(MainClass.arrivepos == null){ commandSender.sendMessage("目前无玩家发射烟花！"); return true;}
        commandSender.getServer().getPlayer(commandSender.getName()).teleportImmediate(new Location(MainClass.arrivepos.x,MainClass.arrivepos.y,MainClass.arrivepos.z,MainClass.arrivepos.getLevel()));
        commandSender.sendMessage("已接受烟花邀请！!");
        return true;
    }
}
