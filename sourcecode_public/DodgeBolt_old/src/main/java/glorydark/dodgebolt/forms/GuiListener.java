package glorydark.dodgebolt.forms;


import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookEnchanted;
import gameapi.room.Room;
import glorydark.dodgebolt.MainClass;

import java.util.Set;


public class GuiListener implements Listener {

    @EventHandler
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event) {
        Player p = event.getPlayer();
        FormWindow window = event.getWindow();
        if (p == null || window == null) {
            return;
        }
        GuiType guiType = CreateGui.UI_CACHE.containsKey(p) ? CreateGui.UI_CACHE.get(p).get(event.getFormID()) : null;
        if(guiType == null){
            return;
        }
        CreateGui.UI_CACHE.get(p).remove(event.getFormID());
        if (event.getResponse() == null) {
            return;
        }
        if (event.getWindow() instanceof FormWindowSimple) {
            this.onSimpleClick(p, (FormWindowSimple) window, guiType);
        }
    }

    private void onSimpleClick(Player player, FormWindowSimple simple, GuiType guiType) {
        if(simple.getResponse() == null){ return; }
        switch (guiType) {
            case MainMenu:
                switch (simple.getResponse().getClickedButtonId()){
                    case 0:
                        CreateGui.showChooseRoomMenu(player, false);
                        break;
                    case 1:
                        CreateGui.showChooseRoomMenu(player, true);
                        break;
                }
                break;
            case ChooseRoomWindow:
                Set<Room> roomSet = MainClass.roomList.keySet();
                Room[] rooms = roomSet.toArray(new Room[roomSet.size()-1]);
                rooms[simple.getResponse().getClickedButtonId()].addPlayer(player);
                Item addItem1 = new ItemBookEnchanted();
                addItem1.setCustomName("§l§c退出房间");
                player.getInventory().setItem(0, addItem1);
                break;
            case ChooseRoomToSpectateWindow:
                roomSet = MainClass.roomList.keySet();
                rooms = roomSet.toArray(new Room[roomSet.size()-1]);
                rooms[simple.getResponse().getClickedButtonId()].setSpectator(player, false, false);
                player.sendMessage("您可以通过/dodgebolt quit 退出");
                break;
            default:
                break;
        }
    }
}
