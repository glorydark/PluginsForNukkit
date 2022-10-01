package gameapi.bossbar;

import cn.nukkit.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.BossBarColor;
import cn.nukkit.utils.DummyBossBar;

import java.util.HashMap;

/**
 * @author Glorydark
 */
public class BossBar {
    public static HashMap<Player, Long> bossBars;

    public static void createBossBar(Player player, String text, BlockColor blockColor){
        if(bossBars.getOrDefault(player, null) != null){
            player.removeBossBar(bossBars.get(player));
        }
        DummyBossBar bossBar = new DummyBossBar.Builder(player).text(text).color(BossBarColor.BLUE).build();
        bossBars.put(player, bossBar.getBossBarId());
        player.createBossBar(bossBar);
    }
}
