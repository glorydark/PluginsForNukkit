package glorydark.SimpleDialogues.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.SimpleDialogues.dialogue.DialogueMain;

public class ReloadDialogueDataCommand extends Command {

    public ReloadDialogueDataCommand() {
        super("重载对话数据");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("正在重载对话数据......");
            DialogueMain.loadAll();
            commandSender.sendMessage("重载完成！");
        }else{
            if(commandSender.isOp()){
                commandSender.sendMessage("正在重载对话数据......");
                DialogueMain.loadAll();
                commandSender.sendMessage("重载完成！");
            }else {
                commandSender.sendMessage("请在后台执行！");
            }
        }
        return true;
    }
}
