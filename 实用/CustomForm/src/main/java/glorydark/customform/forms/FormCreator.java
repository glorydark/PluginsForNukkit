package glorydark.customform.forms;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import glorydark.customform.CustomFormMain;
import glorydark.customform.scriptForms.SoundData;
import glorydark.customform.scriptForms.data.ResponseExecuteData;
import glorydark.customform.scriptForms.data.SimpleResponseExecuteData;
import glorydark.customform.scriptForms.data.SliderResponseExecuteData;
import glorydark.customform.scriptForms.data.ToggleResponseExecuteData;
import glorydark.customform.scriptForms.form.ScriptForm;
import glorydark.customform.scriptForms.form.ScriptFormCustom;
import glorydark.customform.scriptForms.form.ScriptFormModal;
import glorydark.customform.scriptForms.form.ScriptFormSimple;
import lombok.Data;

import java.util.*;

public class FormCreator {
    public static final LinkedHashMap<String, WindowInfo> UI_CACHE = new LinkedHashMap<>();

    public static LinkedHashMap<String, ScriptForm> windowScripts = new LinkedHashMap<>();

    public static int formId = -1;

    @Data
    public static class WindowInfo{
        private FormType type;

        private String script;

        public WindowInfo(FormType type, String script){
            this.type = type;
            this.script = script;
        }
    }

    public static void showFormWindow(Player player, FormType formType, String identifier) {
        if(player.namedTag.contains("lastFormRequestMillis") && System.currentTimeMillis() - player.namedTag.getLong("lastFormRequestMillis") < CustomFormMain.coolDownMillis) {
            player.sendMessage("您点的太快了，请稍后再试！");
            return;
        }
        FormWindow window = windowScripts.get(identifier).getWindow(player);
        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.formId = formId;
        packet.data = window.getJSONData();
        player.dataPacket(packet);
        player.namedTag.putLong("lastFormRequestMillis", System.currentTimeMillis());
        UI_CACHE.put(player.getName(), new WindowInfo(formType, identifier));
    }

    public static void showScriptGui(Player player, String identifier){
        if(windowScripts.containsKey(identifier)){
            ScriptForm script = windowScripts.get(identifier);
            FormWindow window = script.getWindow(player);
            if(script.getOpenSound() != null){
                script.getOpenSound().addSound(player);
            }
            if(window instanceof FormWindowSimple) {
                showFormWindow(player, FormType.ScriptSimple, identifier);
            }
            if(window instanceof FormWindowModal) {
                showFormWindow(player, FormType.ScriptModal, identifier);
            }
            if(window instanceof FormWindowCustom) {
                showFormWindow(player, FormType.ScriptCustom, identifier);
            }
        }
    }

    public static boolean loadWindow(String identifier, Map<String, Object> config){
        switch ((int) config.get("type")){
            case 0:
                //simple
                List<SimpleResponseExecuteData> simpleResponseExecuteDataList = new ArrayList<>();
                if(config.containsKey("components")) {
                    for (Map<String, Object> component : (List<Map<String, Object>>) config.getOrDefault("components", new ArrayList<>())) {
                        SimpleResponseExecuteData data = new SimpleResponseExecuteData((List<String>) component.getOrDefault("commands", new ArrayList<>()), (List<String>) component.getOrDefault("messages", new ArrayList<>()));
                        simpleResponseExecuteDataList.add(data);
                    }
                }
                ScriptFormSimple simple = new ScriptFormSimple(config, simpleResponseExecuteDataList, new SoundData("", 1f, 0f, true));
                if(config.containsKey("open_sound")) {
                    Map<String, Object> openSoundMap = (Map<String, Object>) config.get("open_sound");
                    simple.setOpenSound(new SoundData((String) openSoundMap.get("name"), Float.parseFloat(openSoundMap.getOrDefault("volume", 1f).toString()), Float.parseFloat(openSoundMap.getOrDefault("pitch", 0f).toString()), (Boolean) openSoundMap.getOrDefault("personal", true)));
                }
                if (simple.getWindow() != null) {
                    windowScripts.put(identifier, simple);
                    return true;
                }
                break;
            case 1:
                List<ResponseExecuteData> out = new ArrayList<>();
                //custom
                for (Map<String, Object> component : (List<Map<String, Object>>) config.getOrDefault("components", new ArrayList<>())) {
                    String type = (String) component.getOrDefault("type", "");
                    if (type.equals("StepSlider") || type.equals("Dropdown")) {
                        List<SimpleResponseExecuteData> data = new ArrayList<>();
                        List<Map<String, Object>> maps = (List<Map<String, Object>>) component.getOrDefault("responses", new LinkedHashMap<>());
                        for (Map<String, Object> map : maps) {
                            data.add(new SimpleResponseExecuteData((List<String>) map.getOrDefault("commands", new ArrayList<>()), (List<String>) map.getOrDefault("messages", new ArrayList<>())));
                        }
                        out.add(new SliderResponseExecuteData(data));
                    } else {
                        if (type.equals("Toggle")) {
                            Map<String, Object> maps = (Map<String, Object>) component.getOrDefault("responses", new LinkedHashMap<>());
                            out.add(new ToggleResponseExecuteData((List<String>) maps.get("true_commands"), (List<String>) maps.get("true_messages"), (List<String>) maps.get("false_commands"), (List<String>) maps.get("false_messages")));
                        } else {
                            out.add(new SimpleResponseExecuteData((List<String>) component.getOrDefault("commands", new ArrayList<>()), (List<String>) component.getOrDefault("messages", new ArrayList<>())));
                        }
                    }
                }
                ScriptFormCustom custom = new ScriptFormCustom(config, out, new SoundData("", 1f, 0f, true));
                if(config.containsKey("open_sound")) {
                    Map<String, Object> openSoundMap = (Map<String, Object>) config.get("open_sound");
                    custom.setOpenSound(new SoundData((String) openSoundMap.get("name"), Float.parseFloat(openSoundMap.getOrDefault("volume", 1f).toString()), Float.parseFloat(openSoundMap.getOrDefault("pitch", 0f).toString()), (Boolean) openSoundMap.getOrDefault("personal", true)));
                }
                if(custom.getWindow() != null){
                    windowScripts.put(identifier, custom);
                    return true;
                }
                break;
            case 2:
                //modal
                simpleResponseExecuteDataList = new ArrayList<>();
                for (Map<String, Object> component : (List<Map<String, Object>>) config.getOrDefault("components", new ArrayList<>())) {
                    SimpleResponseExecuteData data = new SimpleResponseExecuteData((List<String>) component.getOrDefault("commands", new ArrayList<>()), (List<String>) component.getOrDefault("messages", new ArrayList<>()));
                    simpleResponseExecuteDataList.add(data);
                }
                ScriptFormModal modal = new ScriptFormModal(config, simpleResponseExecuteDataList, new SoundData("", 1f, 0f, true));
                if(config.containsKey("open_sound")) {
                    Map<String, Object> openSoundMap = (Map<String, Object>) config.get("open_sound");
                    modal.setOpenSound(new SoundData((String) openSoundMap.get("name"), Float.parseFloat(openSoundMap.getOrDefault("volume", 1f).toString()), Float.parseFloat(openSoundMap.getOrDefault("pitch", 0f).toString()), (Boolean) openSoundMap.getOrDefault("personal", true)));
                }
                if (modal.getWindow() != null) {
                    windowScripts.put(identifier, modal);
                    return true;
                }
                break;
        }
        return false;
    }
}
