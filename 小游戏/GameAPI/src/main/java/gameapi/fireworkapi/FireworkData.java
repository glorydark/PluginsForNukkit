package gameapi.fireworkapi;

import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Position;
import cn.nukkit.utils.DyeColor;

/**
 * Adapted from: PetteriM's FireworkShow
 * Glorydark added some changes to make it more convenient to spawn a firework
 */
public class FireworkData {
    public final ItemFirework.FireworkExplosion.ExplosionType explosionType;
    public final Position position;
    public final DyeColor color;

    public FireworkData(ItemFirework.FireworkExplosion.ExplosionType explosionType, DyeColor color, Position position) {
        this.position = position;
        this.explosionType = explosionType;
        this.color = color;
    }
}
