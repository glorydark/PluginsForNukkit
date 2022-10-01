package glorydark.sheepwar.utils;

import cn.nukkit.Player;
import com.smallaswater.npc.variable.BaseVariable;
import gameapi.room.Room;
import glorydark.sheepwar.SheepWarMain;

import java.util.HashMap;
import java.util.Map;


public class RsNpcXVariable extends BaseVariable {

    @Override
    public String stringReplace(Player player, String s) {
        HashMap<String, Integer> map = new HashMap<>();
        int all = 0;
        for (Room room : SheepWarMain.rooms.keySet()) {
            map.put(room.getRoomName(), room.getPlayers().size());
            all += room.getPlayers().size();
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            s = s.replace("{SheepWarRoomPlayerNumber" + entry.getKey() + "}", entry.getValue() + "");
        }
        return s.replace("{SheepWarRoomPlayerNumberAll}", all + "");
    }
}

