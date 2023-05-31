package glorydark.customform.scriptForms.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.level.Sound;
import glorydark.customform.scriptForms.SoundData;

public interface ScriptForm {
    void execute(Player player, FormResponse response, Object... params);

    FormWindow getWindow(Player player);

    SoundData getOpenSound();
}