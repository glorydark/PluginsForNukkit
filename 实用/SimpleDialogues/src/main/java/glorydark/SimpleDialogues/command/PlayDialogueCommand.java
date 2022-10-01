package glorydark.SimpleDialogues.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.MainClass;
import glorydark.SimpleDialogues.charmsystem.Window;
import glorydark.SimpleDialogues.dialogue.DialogueMain;

public class PlayDialogueCommand extends Command {

    public PlayDialogueCommand() {
        super("播放对话", "播放对话", "/播放对话 玩家名 对话名称");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(strings.length < 1){ return false;}
        if(!(commandSender instanceof Player)) {
            if(strings.length < 2){ return false;} //播放对话 玩家名 对话名称
            Player player = Server.getInstance().getPlayer(strings[0]);
            if(player != null && player.isOnline()){
                if(MainClass.isCheckStartWindowOn){
                    Window.showCheckBeforeDialoguePlayMenu(((Player) commandSender).getPlayer(), strings[0]);
                }else {
                    DialogueMain.showPlayerDialogue(player, strings[1]);
                }
            }else{
                commandSender.sendMessage(TextFormat.RED+"找不到玩家！");
            }
        }else{
            if(MainClass.isCheckStartWindowOn){
                Window.showCheckBeforeDialoguePlayMenu(((Player) commandSender).getPlayer(), strings[0]);
            }else{
                DialogueMain.showPlayerDialogue((Player) commandSender, strings[0]);
            }
        }
        return true;
    }
}
