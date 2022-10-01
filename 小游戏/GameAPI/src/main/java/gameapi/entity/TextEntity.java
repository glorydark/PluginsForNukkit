package gameapi.entity;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TextEntity extends Entity
{

    private final Set<Player> hasSpawned = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public TextEntity(FullChunk chunk, Position position, String text, CompoundTag nbt)
    {
        super(chunk, nbt);
        this.setPosition(position);
        setNameTag(text);
    }

    @Deprecated
    public TextEntity(FullChunk chunk, CompoundTag nbt)
    {
        super(chunk, nbt);
        close();
    }

    public int getNetworkId()
    {
        return 64;
    }

    protected void initEntity()
    {
        super.initEntity();
        setNameTagAlwaysVisible(true);
        setImmobile(true);
        getDataProperties().putLong(0, 65536L);
    }

    public boolean onUpdate(int currentTick)
    {
        return super.onUpdate(currentTick);
    }

    @Override
    public void spawnToAll(){
        for(Player player: this.getLevel().getPlayers().values()){
            this.spawnTo(player);
        }
    }

    @Override
    public void despawnFromAll(){
        for(Player player: Server.getInstance().getOnlinePlayers().values()){
            this.despawnFrom(player);
        }
    }

    @Override
    public void spawnTo(Player player) {
        if(this.getLevel().equals(player.getLevel())) {
            if (this.hasSpawned.contains(player)) {
                this.despawnFrom(player);
            }
            this.hasSpawned.add(player);
            AddEntityPacket pk = new AddEntityPacket();
            pk.entityRuntimeId = this.getId();
            pk.entityUniqueId = this.getId();
            pk.type = 64;
            pk.yaw = 0;
            pk.headYaw = 0;
            pk.pitch = 0;
            pk.x = (float) this.getX();
            pk.y = (float) this.getY();
            pk.z = (float) this.getZ();
            pk.speedX = 0;
            pk.speedY = 0;
            pk.speedZ = 0;
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);
            EntityUtils.entityList.add(this);
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.contains(player)) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player);
        }
        EntityUtils.entityList.remove(this);
    }
}