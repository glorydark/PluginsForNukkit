package glorydark.summonentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @OriginalAuthor SmallasWater
 */
public class DefaultEntity extends EntityHuman {

    private Player owner = null;

    private int recordTick = 0;

    private int changeInterval = 20;

    private Boolean isVaried = false;

    private Boolean isFollow;

    private Boolean isAttack;
    private Boolean isSpin;

    private Double damage;

    private Double defense;

    private Position position;

    private Entity target = null;

    private Double speed;

    private double deltaX;

    private double deltaY;

    private double deltaZ;

    public DefaultEntity(FullChunk chunk, CompoundTag nbt, Position position) {
        super(chunk, nbt);
        this.x += 0.5;
        this.z += 0.5;
        this.position = position;
    }

    @Deprecated //只是为了兼容PN核心
    public DefaultEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.position = new Position(0, 0, 0, Server.getInstance().getDefaultLevel()); //防止NPE
        this.close();
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

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Integer getRecordTick() {
        return recordTick;
    }

    public void setRecordTick(Integer val) {
        recordTick = val;
    }

    public void addRecordTick(Integer val) {
        recordTick += val;
    }

    public int getChangeInterval() {
        return changeInterval;
    }

    public void setChangeInterval(int changeInterval) {
        this.changeInterval = changeInterval;
    }

    public Boolean isVaried() {
        return isVaried;
    }

    public void setVaried(Boolean varied) {
        isVaried = varied;
    }

    public Boolean isFollow() {
        return isFollow;
    }

    public void setFollow(Boolean isFollow) {
        this.isFollow = isFollow;
    }

    public void setSpin(Boolean isSpin) {
        this.isSpin = isSpin;
    }

    public Boolean isAttack() {
        return isAttack;
    }

    public void setAttack(Boolean attack) {
        isAttack = attack;
    }

    public Double getDamage() {
        return damage;
    }

    public void setDamage(Double damage) {
        this.damage = damage;
    }

    public Double getDefense() {
        return defense;
    }

    public void setDefense(Double defense) {
        this.defense = defense;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public double getDeltaZ() {
        return deltaZ;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

    public void setDeltaZ(double deltaZ) {
        this.deltaZ = deltaZ;
    }
}