package glorydark.sheepwar;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class SheepwarMethod {
    public static void joinRoom(String roomName, Player player){
        Room room = Room.getRoom("DSheepWar",roomName);
        if(room != null){
            if(room.getPlayers().size() >= room.getMaxPlayer()){
                player.sendMessage(TextFormat.RED+"房间已满！");
                return;
            }
            if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_WAIT && room.getRoomStatus() != RoomStatus.ROOM_STATUS_PreStart){
                player.sendMessage(TextFormat.RED+"游戏已经开始，无法中途加入！");
                return;
            }
            room.addPlayer(player);
            player.sendMessage(TextFormat.GREEN+"您已加入房间");
        }else{
            player.sendMessage(TextFormat.RED+"房间不存在！");
        }
    }
}
