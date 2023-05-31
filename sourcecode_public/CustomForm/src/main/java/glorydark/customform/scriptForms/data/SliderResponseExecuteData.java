package glorydark.customform.scriptForms.data;

import cn.nukkit.Player;
import cn.nukkit.Server;
import lombok.Data;

import java.util.List;

@Data
public class SliderResponseExecuteData implements ResponseExecuteData {

    public List<SimpleResponseExecuteData> responses;

    public SliderResponseExecuteData(List<SimpleResponseExecuteData> responses){
        this.responses = responses;
    }

    public void execute(Player player, int responseId, Object... params){
        for(String command: responses.get(responseId).getCommands()){
            if(command.startsWith("console#")){
                Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), replace(command, player, responseId, params[0]));
            }else{
                Server.getInstance().dispatchCommand(player, replace(command, player, responseId, params[0]));
            }
        }
        for(String message: responses.get(responseId).getMessages()){
            player.sendMessage(replace(message, player, responseId, params[0]));
        }
    }

    public String replace(String text, Player player, Object... params){
        if(params.length < 1) {
            return text.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName()).replaceFirst("console#", "");
        }else{
            String ready = text.replace("%player%", player.getName()).replace("%level%", player.getLevel().getName());
            return ready.replace("%content%", String.valueOf(params[1])).replace("%contentId%", String.valueOf(params[0])).replaceFirst("console#", "");
        }
    }
}
