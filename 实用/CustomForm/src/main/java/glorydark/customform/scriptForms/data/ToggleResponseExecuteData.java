package glorydark.customform.scriptForms.data;

import cn.nukkit.Player;
import cn.nukkit.Server;
import lombok.Data;

import java.util.List;

@Data
public class ToggleResponseExecuteData implements ResponseExecuteData{
    List<String> true_commands;
    List<String> true_messages;
    List<String> false_commands;
    List<String> false_messages;

    public ToggleResponseExecuteData(List<String> true_commands, List<String> true_messages, List<String> false_commands,List<String> false_messages){
        this.true_commands = true_commands;
        this.true_messages = true_messages;
        this.false_commands = false_commands;
        this.false_messages = false_messages;
    }

    public void execute(Player player, int responseId, Object... params){
        switch (responseId){
            case 0:
                for(String command: true_commands){
                    if(command.startsWith("console#")){
                        Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), replace(command, player));
                    }else{
                        Server.getInstance().dispatchCommand(player, replace(command, player, params));
                    }
                }
                for(String message: true_messages){
                    player.sendMessage(replace(message, player, params));
                }
                break;
            case 1:
                for(String command: false_commands){
                    if(command.startsWith("console#")){
                        Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), replace(command, player));
                    }else{
                        Server.getInstance().dispatchCommand(player, replace(command, player, params));
                    }
                }
                for(String message: false_messages){
                    player.sendMessage(replace(message, player, params));
                }
                break;
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
