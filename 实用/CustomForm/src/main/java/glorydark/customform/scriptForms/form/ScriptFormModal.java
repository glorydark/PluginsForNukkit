package glorydark.customform.scriptForms.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.window.FormWindowModal;
import glorydark.customform.CustomFormMain;
import glorydark.customform.scriptForms.SoundData;
import glorydark.customform.scriptForms.data.SimpleResponseExecuteData;
import lombok.Data;
import tip.utils.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ScriptFormModal implements ScriptForm {

    private SoundData openSound;
    private List<SimpleResponseExecuteData> data;

    private Map<String, Object> config;

    private FormWindowModal window;

    public ScriptFormModal(Map<String, Object> config, List<SimpleResponseExecuteData> data, SoundData openSound){
        this.config = config;
        this.data = data;
        this.window = initWindow();
        this.openSound = openSound;
    }

    public void execute(Player player, FormResponse response, Object... params){
        FormResponseModal responseModal = (FormResponseModal) response;
        if(data.size() <= (responseModal.getClickedButtonId())){
            return;
        }
        data.get(responseModal.getClickedButtonId()).execute(player, 0, params);
    }

    public FormWindowModal initWindow(){
        FormWindowModal modal;
        String content = (String) config.getOrDefault("content", "");
        List<Map<String, Object>> buttons = (List<Map<String, Object>>) config.getOrDefault("components", new ArrayList<>());
        if(buttons.size() != 2){
            return null;
        }
        if(content.equals("")) {
            modal = new FormWindowModal(replace((String) config.getOrDefault("title", "")), "", (String) buttons.get(0).get("text"), (String) buttons.get(1).get("text"));
        }else{
            modal = new FormWindowModal(replace((String) config.getOrDefault("title", "")), content, (String) buttons.get(0).get("text"), (String) buttons.get(1).get("text"));
        }

        return modal;
    }

    public FormWindowModal getWindow(Player player){
        if(CustomFormMain.enableTips){
            FormWindowModal modal = getModifiableWindow();
            modal.setContent(Api.strReplace(modal.getContent(), player));
            modal.setTitle(Api.strReplace(modal.getTitle(), player));
            return modal;
        }
        return this.getWindow();
    }

    public FormWindowModal getModifiableWindow(){
        return new FormWindowModal(window.getTitle(), window.getContent(), window.getButton1(), window.getButton2());
    }

    @Override
    public SoundData getOpenSound() {
        return openSound;
    }

    public String replace(String string){
        return string.replace("\\n", "\n");
    }
}
