package glorydark.playanimation;

import cn.nukkit.Nukkit;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.DataPacket;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteOrder;

public class AvailableEntityIdentifiersPacket extends DataPacket {
    public static final byte NETWORK_ID = 119;
    private static final byte[] TAG;
    public byte[] tag;

    public AvailableEntityIdentifiersPacket() {
        this.tag = TAG;
    }

    public byte pid() {
        return 119;
    }

    public void decode() {
        this.tag = this.get();
    }

    public void encode() {
        this.reset();
        this.put(this.tag);
    }

    public String toString() {
        return "AvailableEntityIdentifiersPacket()";
    }

    static {
        try {
            InputStream inputStream = Nukkit.class.getClassLoader().getResourceAsStream("entity_identifiers.dat");
            if (inputStream == null) {
                throw new AssertionError("Could not find entity_identifiers.dat");
            }

            BufferedInputStream bis = new BufferedInputStream(inputStream);
            CompoundTag nbt = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true);
            ListTag<CompoundTag> list = nbt.getList("idlist", CompoundTag.class);
            CompoundTag add = new CompoundTag();
            add.putString("id", "foo:bar");
            nbt.putBoolean("summonable", true);
            nbt.putBoolean("hasspawnegg", false);
            nbt.putBoolean("experimental", false);
            list.add(add);
            nbt.putList(list);
            TAG = NBTIO.write(nbt, ByteOrder.BIG_ENDIAN, true);
        } catch (Exception var1) {
            throw new AssertionError("Error whilst loading entity_identifiers.dat", var1);
        }
    }
}
