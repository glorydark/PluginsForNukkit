package glorydark.lootbattle;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.room.Room;
import gameapi.utils.AdvancedLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TributeData {
    private List<String> simpleSpawns;
    private List<String> goldenSpawns;
    private int simpleIntervals;
    private int goldenIntervals;

    private int simpleScores;

    private int goldenScores;

    private String level;

    public TributeData(int simpleIntervals, int goldenIntervals, int simpleScores, int goldenScores, List<String> simpleSpawns, List<String> goldenSpawns, String level){
        this.goldenIntervals = goldenIntervals;
        this.simpleIntervals = simpleIntervals;
        this.simpleScores = simpleScores;
        this.goldenScores = goldenScores;
        this.simpleSpawns = simpleSpawns;
        this.goldenSpawns = goldenSpawns;
        this.level = level;
    }

    public void checkSpawn(Room room){
        if(room.getTime() % simpleIntervals == 0){
            spawnSimpleTribute(room);
            room.getPlayers().forEach(player -> player.sendMessage("[!] 普通贡品已刷新"));
        }
        if(room.getTime() % goldenIntervals == 0){
            spawnGoldenTribute(room);
            room.getPlayers().forEach(player -> player.sendMessage("[!] 金色贡品已刷新"));
        }
    }

    public void spawnSimpleTribute(Room room){
        for(String spawn: simpleSpawns){
            AdvancedLocation advancedLocation = new AdvancedLocation(spawn+":"+level);
            Location location = advancedLocation.getLocation();
            Item item = Item.get(Item.IRON_INGOT);
            item.setCustomName("§e§l普通贡品");
            EntityItem ironItem = createItemEntity(location, NBTIO.putItemHelper(item));
            ironItem.setNameTag("§e§l普通贡品");
            ironItem.setNameTagAlwaysVisible(true);
            ironItem.spawnToAll();
        }
    }

    public void spawnGoldenTribute(Room room){
        for(String spawn: goldenSpawns){
            AdvancedLocation advancedLocation = new AdvancedLocation(spawn+":"+level);
            Location location = advancedLocation.getLocation();
            Item item = Item.get(Item.IRON_INGOT);
            item.setCustomName("§6§l金色贡品");
            EntityItem goldenItem = createItemEntity(location, NBTIO.putItemHelper(item));
            goldenItem.setNameTag("§6§l金色贡品");
            goldenItem.setNameTagAlwaysVisible(true);
            goldenItem.spawnToAll();
        }
    }

    @Override
    public String toString() {
        return "TributeData{" +
                "simpleSpawns=" + simpleSpawns +
                ", goldenSpawns=" + goldenSpawns +
                ", simpleIntervals=" + simpleIntervals +
                ", goldenIntervals=" + goldenIntervals +
                ", simpleScores=" + simpleScores +
                ", goldenScores=" + goldenScores +
                '}';
    }

    //@original author: YaUhYeah
    public EntityItem createItemEntity(Location loc, CompoundTag itemTag) {
        return new EntityItem(
                loc.getLevel().getChunk((int) loc.getX() >> 4, (int) loc.getZ() >> 4, true),
                new CompoundTag()
                        .putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", loc.getX()))
                                .add(new DoubleTag("", loc.getY())).add(new DoubleTag("", loc.getZ())))

                        .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0))
                                .add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))

                        .putList(new ListTag<FloatTag>("Rotation")
                                .add(new FloatTag("", new java.util.Random().nextFloat() * 360))
                                .add(new FloatTag("", 0)))

                        .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", 0));
    }
}
