package glorydark.fireworkshop.api;

import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;

import java.util.Random;

public class CreateFireworkApi {
    public static CreateFireworkApi getInstance(){
        return new CreateFireworkApi();
    }

    public static void spawnFirework(Position position,DyeColor color,ItemFirework.FireworkExplosion.ExplosionType type) {
        Level level = position.getLevel();
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        Random random = new Random();
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByteArray("FireworkFade", new byte[0]);
        compoundTag.putBoolean("FireworkFlicker", random.nextBoolean());
        compoundTag.putBoolean("FireworkTrail", random.nextBoolean());
        tag.putCompound("Fireworks", (new CompoundTag("Fireworks")).putList(new ListTag<CompoundTag>("Explosions").add(compoundTag)).putByte("Flight", 1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", position.x + 1.0D))
                .add(new DoubleTag("", position.y + 1.0D))
                .add(new DoubleTag("", position.z + 1.0D))
        );
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", 0.0F))
                .add(new FloatTag("", 0.0F))

        );
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        compoundTag.putByteArray("FireworkColor", new byte[]{
                (byte) color.getDyeData()
        });
        compoundTag.putByte("FireworkType", type.ordinal());
        EntityFirework entity = new EntityFirework(level.getChunk((int) position.x >> 4, (int) position.z >> 4), nbt);
        entity.spawnToAll();
    }
}
