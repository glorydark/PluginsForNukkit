package glorydark.customform.scriptForms.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import glorydark.customform.CustomFormMain;
import glorydark.customform.scriptForms.SoundData;
import glorydark.customform.scriptForms.data.SimpleResponseExecuteData;
import lombok.Data;
import tip.utils.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ScriptFormSimple implements ScriptForm {

    private List<SimpleResponseExecuteData> data;

    private Map<String, Object> config;

    private FormWindowSimple window;

    private SoundData openSound;

    public ScriptFormSimple(Map<String, Object> config, List<SimpleResponseExecuteData> data, SoundData openSound){
        this.config = config;
        this.data = data;
        this.window = initWindow();
        this.openSound = openSound;
    }

    public void execute(Player player, FormResponse response, Object... params){
        FormResponseSimple responseSimple = (FormResponseSimple) response;
        if(data.size() <= responseSimple.getClickedButtonId()){
            return;
        }
        data.get(responseSimple.getClickedButtonId()).execute(player, 0, params);
    }

    public String replace(String string){
        return string.replace("\\n", "\n");
    }

    public FormWindowSimple getWindow(Player player){
        if(CustomFormMain.enableTips){
            FormWindowSimple simple = getModifiableWindow(window.getButtons());
            List<ElementButton> buttons = new ArrayList<>();
            for(ElementButton element: simple.getButtons()){
                if(element.getImage() != null) {
                    buttons.add(new ElementButton(Api.strReplace(element.getText(), player), element.getImage()));
                }else{
                    buttons.add(new ElementButton(Api.strReplace(element.getText(), player)));
                }
            }
            simple = getModifiableWindow(buttons);
            simple.setContent(Api.strReplace(simple.getContent(), player));
            simple.setTitle(Api.strReplace(simple.getTitle(), player));
            return simple;
        }
        return this.getWindow();
    }

    public FormWindowSimple getModifiableWindow(List<ElementButton> buttons){
        return new FormWindowSimple(window.getTitle(), window.getContent(), buttons);
    }

    @Override
    public SoundData getOpenSound() {
        return openSound;
    }

    public FormWindowSimple initWindow(){
        FormWindowSimple simple;
        String content = replace((String) config.getOrDefault("content", ""));
        if(content.equals("")) {
            simple = new FormWindowSimple(replace((String) config.getOrDefault("title", "")), "");
        }else{
            simple = new FormWindowSimple(replace((String) config.getOrDefault("title", "")), content);
        }
        for(Map<String, Object> component: (List<Map<String, Object>>) config.getOrDefault("components", new ArrayList<>())) {
            String picPath = (String) component.getOrDefault("pic", "");
            if (picPath.equals("")) {
                simple.addButton(new ElementButton(replace((String) component.getOrDefault("text", ""))));
            } else {
                if (picPath.startsWith("path#")) {
                    simple.addButton(new ElementButton(replace((String) component.getOrDefault("text", "")), new ElementButtonImageData("path", picPath.replaceFirst("path#", ""))));
                }else {
                    if (picPath.startsWith("url#")) {
                        simple.addButton(new ElementButton(replace((String) component.getOrDefault("text", "")), new ElementButtonImageData("url", picPath.replaceFirst("url#", ""))));
                    }else{
                        return null;
                    }
                }
            }
        }
        return simple;
    }
}
