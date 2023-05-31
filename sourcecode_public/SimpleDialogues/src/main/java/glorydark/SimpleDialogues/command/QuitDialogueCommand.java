package glorydark.SimpleDialogues.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.MainClass;

public class QuitDialogueCommand extends Command {
    public QuitDialogueCommand() {
        super("退出对话", "退出对话", "/退出对话");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("请在游戏内执行此指令！");
        }else{
            if(MainClass.inDialogues.remove(((Player) commandSender).getPlayer()) != null){
                commandSender.sendMessage(TextFormat.GREEN + "您已成功退出对话！");
            }else{
                commandSender.sendMessage(TextFormat.RED + "您还不在对话中！");
            }
        }
        return true;
    }
}
