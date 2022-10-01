package glorydark.fireworkshop.utils;

import cn.nukkit.Player;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Position;
import cn.nukkit.utils.DyeColor;

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
