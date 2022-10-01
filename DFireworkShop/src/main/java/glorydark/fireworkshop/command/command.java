package glorydark.fireworkshop.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import glorydark.fireworkshop.gui.GuiMain;

public class command extends cn.nukkit.command.Command {

    public command(String name) {
        super(name,"§e烟花商店","/fs 打开菜单");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player)) { commandSender.sendMessage("请在游戏内执行此指令"); return true;}
        GuiMain.createMainMenu((Player) commandSender);
        return true;
    }
}
