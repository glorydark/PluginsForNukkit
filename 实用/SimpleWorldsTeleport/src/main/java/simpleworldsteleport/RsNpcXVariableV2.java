package simpleworldsteleport;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;

import java.util.HashMap;
import java.util.Map;

public class RsNpcXVariableV2 extends BaseVariableV2 {

    @Override
    public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
        HashMap<String, Integer> map = new HashMap<>(); //room - integer
        int all = 0;
        for (Level level : Server.getInstance().getLevels().values()) {
            map.put(level.getName(), level.getPlayers().size());
            all += level.getPlayers().size();
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            this.addVariable("{LevelPlayerCounts_" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        this.addVariable("{LevelPlayerCountsAll}", String.valueOf(all));
    }
}