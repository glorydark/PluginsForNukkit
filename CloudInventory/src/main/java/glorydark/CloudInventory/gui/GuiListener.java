package glorydark.CloudInventory.gui;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import glorydark.CloudInventory.CloudBag;
import glorydark.CloudInventory.Lang;

public class GuiListener implements Listener {
    @EventHandler()
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event){
        Player p = event.getPlayer();
        FormWindow window = event.getWindow();
        if (p == null || window == null) { //先判断是否为玩家以及窗口是否存在
            return;
        }
        GuiType guiType = GuiCreate.UI_CACHE.containsKey(p) ? GuiCreate.UI_CACHE.get(p).get(event.getFormID()) : null; //如果UI_Cache中存在保存玩家的项
        if(guiType == null){ //如果Type不存在
            return;
        }
        GuiCreate.UI_CACHE.get(p).remove(event.getFormID());
        if (event.getResponse() == null) { //返回为空的话，取消
            return;
        }
        if (event.getWindow() instanceof FormWindowSimple) { //FormWindowSimple
            this.onSimpleClick(p, (FormWindowSimple) window, guiType);
        }
        if (event.getWindow() instanceof FormWindowCustom) { //FormWindowCustom
            this.onCustomClick(p, (FormWindowCustom) window, guiType);
        }
        if (event.getWindow() instanceof FormWindowModal) { //FormWindowCustom
            this.onModalClick(p, (FormWindowModal) window, guiType);
        }
    }

    private void onCustomClick(Player player, FormWindowCustom custom, GuiType guiType) {
        FormResponseData data = custom.getResponse().getDropdownResponse(0);
        switch (guiType){
            case PickItem:
                if (data != null){
                    CloudBag.getPlayerBagItem(data.getElementID()+1,player);
                    GuiCreate.showResultGui(player, Lang.getTranslation("Message.PickItemSuccessfully").replace("%ItemName%",data.getElementContent()),GuiType.ReturnToPickItem);
                }
                break;
            case PutItem:
                if (data != null) {
                    Item[] items = player.getInventory().getContents().values().toArray(new Item[player.getInventory().getContents().values().size()]);
                    Item item = items[data.getElementID()];
                    CloudBag.addItemToCloud(player,item);
                    player.getInventory().removeItem(item);
                    GuiCreate.showResultGui(player, Lang.getTranslation("Message.UploadItemSuccessfully").replace("%ItemName%",item.getName()+"*"+item.getCount()),GuiType.ReturnToPutItem);
                }
                break;
        }
    }

    private void onSimpleClick(Player player, FormWindowSimple simple, GuiType guiType) {
        if (guiType == GuiType.Main) {
            switch (simple.getResponse().getClickedButtonId()) {
                case 0:
                    GuiCreate.showPlayerBagGui(player);
                    break;
                case 1:
                    GuiCreate.showGetBagItemGui(player);
                    break;
                case 2:
                    GuiCreate.showPutBagItemGui(player);
                    break;
            }
        }
    }

    private void onModalClick(Player player, FormWindowModal custom, GuiType guiType) {
        if(custom.getResponse().getClickedButtonId() == 0) {
            switch (guiType) {
                case ReturnToMain:
                    GuiCreate.showMainMenu(player);
                    break;
                case ReturnToPutItem:
                    GuiCreate.showPutBagItemGui(player);
                    break;
                case ReturnToPickItem:
                    GuiCreate.showGetBagItemGui(player);
                    break;
            }
        }
    }
}
