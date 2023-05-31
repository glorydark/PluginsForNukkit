package glorydark.dodgebolt.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;
import gameapi.room.Room;
import glorydark.dodgebolt.MainClass;

import java.util.HashMap;

public class CreateGui {
    public static final HashMap<Player, HashMap<Integer, GuiType>> UI_CACHE = new HashMap<>();

    public static void showFormWindow(Player player, FormWindow window, GuiType guiType) {
        UI_CACHE.computeIfAbsent(player, i -> new HashMap<>()).put(player.showFormWindow(window), guiType);
    }

    public static void showMainMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple("DodgeBolt", "Please select the function");
        simple.addButton(new ElementButton("加入\n[ Join ]"));
        simple.addButton(new ElementButton("观战\n[ Spectate ]"));
        simple.addButton(new ElementButton("我的战绩\n[ Record ]"));
        showFormWindow(player, simple, GuiType.MainMenu);
    }

    public static void showChooseRoomMenu(Player player, Boolean isSpectator) {
        FormWindowSimple simple = new FormWindowSimple("DodgeBolt", "Please select the function");
        for(Room room: MainClass.roomList.keySet()){
            simple.addButton(new ElementButton("房间名:"+room.getRoomName()+"\n[ 地图: "+room.getRoomLevelBackup()+" ]"));
        }
        simple.addButton(new ElementButton("返回"));
        if(isSpectator){
            showFormWindow(player, simple, GuiType.ChooseRoomToSpectateWindow);
        }else {
            showFormWindow(player, simple, GuiType.ChooseRoomWindow);
        }
    }
}
