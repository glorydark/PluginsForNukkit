package glorydark.floatingtext;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author glorydark
 * @date {2023/7/24} {12:37}
 */

public class TextEntity extends Entity {
    public TextEntity(FullChunk chunk, CompoundTag nbt, String text) {
        super(chunk, nbt);
        this.setNameTag(text);
    }

    @Deprecated
    public TextEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.close();
    }

    public int getNetworkId() {
        return 64;
    }

    protected void initEntity() {
        super.initEntity();
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
        this.getDataProperties().putLong(0, 65536L);
    }

    public boolean onUpdate(int currentTick) {
        return super.onUpdate(currentTick);
    }

}