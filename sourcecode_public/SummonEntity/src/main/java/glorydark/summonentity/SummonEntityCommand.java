package glorydark.summonentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class SummonEntityCommand extends Command {
    public SummonEntityCommand(String command) {
        super(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        //summonentity player id
        if(strings.length != 2){ commandSender.sendMessage("指令格式错误，应为：/summonentity id player"); return true; }
        Player player = Server.getInstance().getPlayer(strings[1]);
        if(player != null && player.isOnline()) {
            MainClass.summonEntity(player, strings[0]);
            commandSender.sendMessage("生成成功！");
        }else{
            commandSender.sendMessage("生成失败，玩家不存在或不在线！");
        }
        return true;
    }
}
