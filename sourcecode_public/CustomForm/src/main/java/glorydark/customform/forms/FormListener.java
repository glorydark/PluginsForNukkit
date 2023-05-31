package glorydark.customform.forms;


import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import glorydark.customform.scriptForms.form.ScriptForm;

import java.util.HashMap;


public class FormListener implements Listener {

    @EventHandler
    public void DataPacketReceiveEvent(DataPacketReceiveEvent event){
        if(event.getPacket() instanceof ModalFormResponsePacket) {
            ModalFormResponsePacket pk = (ModalFormResponsePacket) event.getPacket();
            //event.getPlayer().sendMessage(pk.formId+":"+pk.data);
            if(pk.formId == FormCreator.formId) {
                dealResponse(event.getPlayer(), pk.data);
            }
        }
    }

    public void dealResponse(Player p, String response) {
        if (p == null) {
            return;
        }
        String pName = p.getName();
        FormType formType = FormCreator.UI_CACHE.containsKey(pName) ? FormCreator.UI_CACHE.get(pName).getType() : null;
        if(formType == null){
            FormCreator.UI_CACHE.remove(pName);
            return;
        }
        String script = FormCreator.UI_CACHE.get(pName).getScript();
        ScriptForm form = FormCreator.windowScripts.get(script);
        if (translateResponse(response.trim(), form.getWindow(p)) == null) {
            return;
        }
        FormCreator.UI_CACHE.remove(pName);
        form.execute(p, translateResponse(response.trim(), form.getWindow(p)));
    }

    public FormResponse translateResponse(String string, FormWindow window) {
        if(window instanceof FormWindowSimple) {
            if (string.equals("null")) {
                return null;
            } else {
                int id = Integer.parseInt(string);
                return new FormResponseSimple(id, ((FormWindowSimple) window).getButtons().get(id));
            }
        }
        if(window instanceof FormWindowCustom) {
            if (string.equals("null") || string.equals("")) {
                return null;
            } else {
                String replaced = string.trim().replace("[", "").replace("]", "");
                String[] split = replaced.split(",");
                HashMap<Integer, Object> hashMap = new HashMap<>();
                for(int i = 0; i<split.length; i++){
                    hashMap.put(i, split[i]);
                }
                return new FormResponseCustom(hashMap, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
            }
        }
        if(window instanceof FormWindowModal) {
            if (string.equals("null")) {
                return null;
            } else {
                int id = string.equals("true")? 0:1;
                return new FormResponseModal(id, id == 0? ((FormWindowModal) window).getButton1(): ((FormWindowModal) window).getButton2());
            }
        }
        return null;
    }
}
