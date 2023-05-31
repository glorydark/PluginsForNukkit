package glorydark.customform.scriptForms.data;

import cn.nukkit.Player;

public interface ResponseExecuteData {
    String replace(String text, Player player, Object... params);

    void execute(Player player, int responseId, Object... params);
}