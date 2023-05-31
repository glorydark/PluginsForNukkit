package glorydark.lootbattle;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import com.sun.istack.internal.NotNull;
import gameapi.room.Room;
import gameapi.room.Team;
import gameapi.utils.GameRecord;

public class Window {
    public static void showPlayerRoomListWindow(@NotNull Player player){
        if(!GameListener.playerFormWindowSimpleHashMap.containsKey(player)) {
            FormWindowSimple simple = new FormWindowSimple("LootBattle - §l§e选择房间", "请选择您的房间！");
            for (Room room : MainClass.roomList) {
                StringBuilder builder = new StringBuilder().append(room.getRoomName()).append("\n");
                switch (room.getRoomStatus()){
                    case ROOM_MapLoadFailed:
                    case ROOM_MapInitializing:
                    case ROOM_STATUS_NextRoundPreStart:
                    case ROOM_STATUS_End:
                        builder.append("§c无法加入");
                        break;
                    case ROOM_STATUS_WAIT:
                    case ROOM_STATUS_PreStart:
                        builder.append("§2未开始 | ").append(room.getPlayers().size()).append("/").append(room.getMaxPlayer());
                        break;
                    case ROOM_STATUS_GameEnd:
                    case ROOM_STATUS_Ceremony:
                    case ROOM_STATUS_GameStart:
                    case ROOM_STATUS_GameReadyStart:
                        builder.append("§6已开始");
                        break;
                }
                simple.addButton(new ElementButton(builder.toString()));
            }
            GameListener.playerFormWindowSimpleHashMap.put(player, simple);
            player.showFormWindow(simple);
        }
    }

    public static void showPlayerHistoryWindow(@NotNull Player player){
        if(!GameListener.playerFormWindowSimpleHashMap.containsKey(player)) {
            FormWindowSimple window = new FormWindowSimple("§l§a历史战绩", "");
            window.setContent("Win(获胜次数): §6" + GameRecord.getGameRecord("LootBattle", player.getName(), "win") + "\n§fFail(失败次数): §c" + GameRecord.getGameRecord("LootBattle", player.getName(), "lose"));
            GameListener.playerFormWindowSimpleHashMap.put(player, window);
            player.showFormWindow(window);
        }
    }

    public static void selectPlayerMenu(Player player, Room room) {
        if (!GameListener.playerFormWindowSimpleHashMap.containsKey(player)) {
            FormWindowSimple window = new FormWindowSimple("§l§a观战助手", "");
            for(Player p: room.getPlayers()){
                Team team = room.getPlayerTeam(p);
                window.addButton(new ElementButton(p.getName()+"\n"+team.getPrefix()+"["+team.getRegistryName()+"]"));
            }
            GameListener.playerFormWindowSimpleHashMap.put(player, window);
            player.showFormWindow(window);
        }
    }
}
