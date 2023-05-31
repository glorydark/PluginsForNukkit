package gameapi.room;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Glorydark
 */
public class RoomRule {
    public Boolean allowBreakBlock = false;
    public Boolean allowPlaceBlock = false;
    public int gameMode;
    public Boolean noStartWalk = true;
    public Boolean noDropItem = true;
    public List<String> canBreakBlocks = new ArrayList<>();
    public List<String> canPlaceBlocks = new ArrayList<>();
    public Boolean allowDamagePlayer = false;
    public Boolean noTimeLimit = false;
    public Boolean antiExplosion = true;
    public Boolean allowEntityExplosionDamage = false;
    public Boolean allowBlockExplosionDamage = false;
    public Boolean allowMagicDamage = false;
    public Boolean allowFireDamage = false;
    public Boolean allowHungerDamage = false;
    public Boolean allowDrowningDamage = false;
    public Boolean allowLightningDamage = false;
    public Boolean allowFallDamage = false;
    public Boolean allowProjectTileDamage = false;
    public Boolean allowSuffocationDamage = false;
    public float defaultHealth = 20;
    public Boolean allowSkill = false;
    public Boolean allowFoodLevelChange = true;
    public Boolean allowRespawn = false;
    public Integer respawnCoolDownTick = 20;
    public Boolean allowSpectatorMode = false;

    public RoomRule(Integer gameMode){
        this.gameMode = gameMode;
    }

    @Override
    public String toString() {
        return "RoomRule{" +
                "allowBreakBlock=" + allowBreakBlock +
                ", allowPlaceBlock=" + allowPlaceBlock +
                ", gameMode=" + gameMode +
                ", noStartWalk=" + noStartWalk +
                ", noDropItem=" + noDropItem +
                ", canBreakBlocks=" + canBreakBlocks +
                ", canPlaceBlocks=" + canPlaceBlocks +
                ", allowDamagePlayer=" + allowDamagePlayer +
                ", noTimeLimit=" + noTimeLimit +
                ", antiExplosion=" + antiExplosion +
                ", allowEntityExplosionDamage=" + allowEntityExplosionDamage +
                ", allowBlockExplosionDamage=" + allowBlockExplosionDamage +
                ", allowMagicDamage=" + allowMagicDamage +
                ", allowFireDamage=" + allowFireDamage +
                ", allowHungerDamage=" + allowHungerDamage +
                ", allowDrowningDamage=" + allowDrowningDamage +
                ", allowLightningDamage=" + allowLightningDamage +
                ", allowFallDamage=" + allowFallDamage +
                ", allowProjectTileDamage=" + allowProjectTileDamage +
                ", allowSuffocationDamage=" + allowSuffocationDamage +
                ", defaultHealth=" + defaultHealth +
                ", allowSkill=" + allowSkill +
                ", allowFoodLevelChange=" + allowFoodLevelChange +
                ", allowRespawn=" + allowRespawn +
                ", respawnCoolDownTick=" + respawnCoolDownTick +
                ", allowSpectatorMode=" + allowSpectatorMode +
                '}';
    }
}
