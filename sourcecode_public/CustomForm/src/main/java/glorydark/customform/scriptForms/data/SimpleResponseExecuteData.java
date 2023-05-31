package glorydark.customform.scriptForms.data;

import cn.nukkit.Player;
import cn.nukkit.Server;
import lombok.Data;

import java.util.List;

@Data
public class SimpleResponseExecuteData implements ResponseExecuteData{
    List<String> commands;
    List<String> messages;

    public SimpleResponseExecuteData(List<String> commands, List<String> messages){
        this.commands = commands;
        this.messages = messages;
    }

    public void execute(Player player, int responseId, Object... params){
        for(String command: commands){
            if(command.startsWith("console#")){
                Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), replace(command, player));
            }else{
                Server.getInstance().dispatchCommand(player, replace(command, player, params));
            }
        }
        for(String message: messages){
            player.sendMessage(replace(message, player, params));
        }
    }

    public String replace(String text, Player player, Object... params){
        if(params.length < 1) {
            return text.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName()).replaceFirst("console#", "");
        }else{
            String ready = text.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName());
            return ready.replace("%get%", String.valueOf(params[0])).replaceFirst("console#", "");
        }
    }
}
