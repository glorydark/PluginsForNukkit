package glorydark.sheepwar.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.room.Room;
import glorydark.sheepwar.SheepWarMain;

public class GuiMain {
    public static void playerMainMenu(Player player){
        FormWindowSimple simple = new FormWindowSimple("SheepWar","请选择您需要的功能！");
        simple.addButton(new ElementButton("加入房间"));
        simple.addButton(new ElementButton("查看战绩"));
    }

    public static void showRoomListMenu(Player player){
        FormWindowSimple simple = new FormWindowSimple("Room List","请选择您加入的房间");
        if(GameAPI.RoomHashMap.size() > 0) {
            for (Room room : SheepWarMain.rooms.keySet()) {
                switch (room.getRoomStatus()) {
                    case ROOM_STATUS_WAIT:
                        simple.addButton(new ElementButton(room.getRoomName() + "\n" + TextFormat.GREEN + "[ 等待中 ]"));
                        break;
                    case ROOM_STATUS_GameReadyStart:
                        simple.addButton(new ElementButton(room.getRoomName() + "\n" + TextFormat.LIGHT_PURPLE + "[ 将开始 ]"));
                        break;
                    case ROOM_MapInitializing:
                    case ROOM_STATUS_End:
                        simple.addButton(new ElementButton(room.getRoomName() + "\n" + TextFormat.DARK_GRAY + "[ 正在加载 ]"));
                        break;
                    case ROOM_STATUS_GameStart:
                    case ROOM_STATUS_PreStart:
                    case ROOM_STATUS_NextRoundPreStart:
                    case ROOM_STATUS_Ceremony:
                    case ROOM_STATUS_GameEnd:
                        simple.addButton(new ElementButton(room.getRoomName() + "\n" + TextFormat.DARK_RED + "[ 已开始 ]"));
                        break;
                }
            }
            GuiListener.showFormWindow(player, simple, GuiType.roomList);
        }else{
            player.sendMessage(TextFormat.RED + "暂无房间！");
        }
    }
}