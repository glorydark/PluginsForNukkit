package glorydark.treasurehunt;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.Collection;
import java.util.Map;

/**
 * OriginalAuthor: SmallasWater
 */
public class TreasureEntity extends EntityHuman {
    private final Position position;
    private final double yawSpeed;
    private double yaw;

    public TreasureEntity(FullChunk chunk, CompoundTag nbt, Position position, double yawSpeed) {
        super(chunk, nbt);
        this.position = position;
        this.yawSpeed = yawSpeed;
        this.yaw = 0;
    }

    @Deprecated //只是为了兼容PN核心
    public TreasureEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.position = new Position(0, 0, 0, Server.getInstance().getDefaultLevel()); //防止NPE
        this.yawSpeed = 4;
        this.yaw = 0;
        this.close();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (yawSpeed > 0) {
            yaw += yawSpeed;
            this.setRotation(yaw, this.getPitch());
            if (yaw > 720) {
                yaw = 0;
                this.setRotation(0, this.getPitch());
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
        this.setHealth(20.0F);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public Collection<Player> hasSpawned(){
        return this.hasSpawned.values();
    }

    public Map<Integer, Player> getSpawned(){
        return this.hasSpawned;
    }

    @Override
    public void spawnTo(Player player){
        if (this.hasSpawned.containsValue(player)) {
            this.despawnFrom(player);
        }
        if(!player.getLevel().equals(this.getLevel())){ return; }
        this.hasSpawned.put(player.getLoaderId(), player);
        this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, new Player[]{player});
        AddPlayerPacket pk = new AddPlayerPacket();
        pk.uuid = this.getUniqueId();
        pk.username = this.getName();
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float)this.x;
        pk.y = (float)this.y;
        pk.z = (float)this.z;
        pk.speedX = (float)this.motionX;
        pk.speedY = (float)this.motionY;
        pk.speedZ = (float)this.motionZ;
        pk.yaw = (float)this.yaw;
        pk.pitch = (float)this.pitch;
        pk.item = this.getInventory().getItemInHand();
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);
        this.server.removePlayerListData(this.getUniqueId(), player);
    }

    @Override
    public void despawnFrom(Player player){
        if (this.hasSpawned.containsValue(player)) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }
}