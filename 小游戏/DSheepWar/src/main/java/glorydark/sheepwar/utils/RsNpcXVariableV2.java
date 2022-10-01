package glorydark.sheepwar.utils;

import cn.nukkit.Player;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;
import gameapi.room.Room;
import glorydark.sheepwar.SheepWarMain;

import java.util.HashMap;
import java.util.Map;

public class RsNpcXVariableV2 extends BaseVariableV2 {

    @Override
    public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
        HashMap<String, Integer> map = new HashMap<>(); //room - integer
        int all = 0;
        for (Room room : SheepWarMain.rooms.keySet()) {
            map.put(room.getRoomName(), room.getPlayers().size());
            all += room.getPlayers().size();
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            this.addVariable("{SheepWarRoomPlayerNumber" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        this.addVariable("{SheepWarRoomPlayerNumberAll}", String.valueOf(all));
    }
}