package gameapi.fireworkapi;

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

/**
 * Adapted from: PetteriM's FireworkShow
 * Glorydark added some changes to make it more convenient to spawn a firework
 */
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

    public static ItemFirework.FireworkExplosion.ExplosionType getExplosionTypeByString(String s){
        if(s.equals("BURST")){
            return ItemFirework.FireworkExplosion.ExplosionType.BURST;
        }
        if(s.equals("LARGE_BALL")){
            return ItemFirework.FireworkExplosion.ExplosionType.LARGE_BALL;
        }
        if(s.equals("SMALL_BALL")){
            return ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL;
        }
        if(s.equals("STAR_SHAPED")){
            return ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED;
        }
        if(s.equals("CREEPER_SHAPED")){
            return ItemFirework.FireworkExplosion.ExplosionType.CREEPER_SHAPED;
        }
        return ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED;
    }

    public static ItemFirework.FireworkExplosion.ExplosionType getExplosionTypeByInt(int enumNumber){
        if(enumNumber == 1){
            return ItemFirework.FireworkExplosion.ExplosionType.BURST;
        }
        if(enumNumber == 2){
            return ItemFirework.FireworkExplosion.ExplosionType.LARGE_BALL;
        }
        if(enumNumber == 3){
            return ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL;
        }
        if(enumNumber == 4){
            return ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED;
        }
        if(enumNumber == 5){
            return ItemFirework.FireworkExplosion.ExplosionType.CREEPER_SHAPED;
        }
        return ItemFirework.FireworkExplosion.ExplosionType.STAR_SHAPED;
    }

    public static DyeColor getColorByString(String s){
        if(s.equals("RED")){
            return DyeColor.RED;
        }
        if(s.equals("BLACK")){
            return DyeColor.BLACK;
        }
        if(s.equals("BLUE")){
            return DyeColor.BLUE;
        }
        if(s.equals("BROWN")){
            return DyeColor.BROWN;
        }
        if(s.equals("CYAN")){
            return DyeColor.CYAN;
        }
        if(s.equals("GRAY")){
            return DyeColor.GRAY;
        }
        if(s.equals("GREEN")){
            return DyeColor.GREEN;
        }
        if(s.equals("LIGHT_BLUE")){
            return DyeColor.LIGHT_BLUE;
        }
        if(s.equals("LIGHT_GRAY")){
            return DyeColor.LIGHT_GRAY;
        }
        if(s.equals("LIME")){
            return DyeColor.LIME;
        }
        if(s.equals("MAGENTA")){
            return DyeColor.MAGENTA;
        }
        if(s.equals("ORANGE")){
            return DyeColor.ORANGE;
        }
        if(s.equals("PINK")){
            return DyeColor.PINK;
        }
        if(s.equals("PURPLE")){
            return DyeColor.PURPLE;
        }
        if(s.equals("WHITE")){
            return DyeColor.WHITE;
        }
        if(s.equals("YELLOW")){
            return DyeColor.YELLOW;
        }
        return DyeColor.WHITE;
    }

    public static DyeColor getColorByInt(Integer integer){
        if(integer == 1){
            return DyeColor.RED;
        }
        if(integer == 2){
            return DyeColor.BLACK;
        }
        if(integer == 3){
            return DyeColor.BLUE;
        }
        if(integer == 4){
            return DyeColor.BROWN;
        }
        if(integer == 5){
            return DyeColor.CYAN;
        }
        if(integer == 6){
            return DyeColor.GRAY;
        }
        if(integer == 7){
            return DyeColor.GREEN;
        }
        if(integer == 8){
            return DyeColor.LIGHT_BLUE;
        }
        if(integer == 9){
            return DyeColor.LIGHT_GRAY;
        }
        if(integer == 10){
            return DyeColor.LIME;
        }
        if(integer == 11){
            return DyeColor.MAGENTA;
        }
        if(integer == 12){
            return DyeColor.ORANGE;
        }
        if(integer == 13){
            return DyeColor.PINK;
        }
        if(integer == 14){
            return DyeColor.PURPLE;
        }
        if(integer == 15){
            return DyeColor.WHITE;
        }
        if(integer == 16){
            return DyeColor.YELLOW;
        }
        return DyeColor.WHITE;
    }
}
