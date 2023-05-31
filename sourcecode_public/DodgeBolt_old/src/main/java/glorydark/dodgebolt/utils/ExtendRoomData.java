package glorydark.dodgebolt.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.EntityFlameParticle;
import cn.nukkit.nbt.tag.*;
import gameapi.utils.AdvancedLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class ExtendRoomData {

    private List<Player> team1 = new ArrayList<>();
    private List<Player> team2 = new ArrayList<>();

    private Integer team1_score = 0;
    private Integer team2_score = 0;

    private Integer max_score = 1;

    private AdvancedLocation arrow_spawn1;

    private AdvancedLocation arrow_spawn2;

    private WallPos wallPos;

    private AdvancedLocation team1_spawn;

    private AdvancedLocation team2_spawn;

    public ExtendRoomData(AdvancedLocation arrow_spawn1, AdvancedLocation arrow_spawn2){
        this.arrow_spawn1 = arrow_spawn1;
        this.arrow_spawn2 = arrow_spawn2;
    }

    public void reset(){
        team1 = new LinkedList<>();
        team2 = new LinkedList<>();
        team1_score = 0;
        team2_score = 0;
    }

    public List<Player> getTeamMembers(Integer integer){
        switch (integer){
            case 1:
                return team1;
            case 2:
                return team2;
        }
        return new ArrayList<>();
    }

    public Integer getTeamScore(Integer team){
        switch (team){
            case 1:
                return team1_score;
            case 2:
                return team2_score;
        }
        return 0;
    }

    public Integer getPlayerTeamIndex(Player player){
        if(this.getTeam1().contains(player)){
            return 1;
        }else{
            return 2;
        }
    }

    public String getPlayerTeamColorPrefix(Player player){
        switch (getPlayerTeamIndex(player)){
            case 1:
                return "§b";
            case 2:
                return "§e";
        }
        return "";
    }

    public String getPlayerTeamName(Integer integer){
        switch (integer){
            case 1:
                return "§b蓝队§f";
            case 2:
                return "§e黄队§f";
        }
        return "";
    }

    public void teleportToSpawn(Player player){
        switch (getPlayerTeamIndex(player)){
            case 1:
                team1_spawn.teleport(player);
                break;
            case 2:
                team2_spawn.teleport(player);
                break;
        }
    }

    public void addTeamScore(Integer team){
        switch (team){
            case 1:
                team1_score+=1;
                break;
            case 2:
                team2_score+=1;
                break;
        }
    }

    public void spawnArrow(Integer thrownTeam){
        switch (thrownTeam){
            case 1:
                Location position = arrow_spawn2.getLocation();
                CompoundTag nbt = (new CompoundTag()).putList((new ListTag("Pos")).add(new DoubleTag("", position.x)).add(new DoubleTag("", position.y)).add(new DoubleTag("", position.z)))
                        .putList(new ListTag("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", -1)).add(new DoubleTag("", Math.cos(0) * Math.cos(0))))
                        .putList((new ListTag("Rotation")).add(new FloatTag("", 0)).add(new FloatTag("", 0)))
                        .putShort("Fire", 0).putDouble("damage", 0d);
                EntityArrow arrow = (EntityArrow)Entity.createEntity("Arrow", position.getChunk(), nbt, new Object[]{null, false});
                position.getLevel().addParticle(new EntityFlameParticle(position));
                arrow.spawnToAll();
                break;
            case 2:
                position = arrow_spawn1.getLocation();
                nbt = (new CompoundTag()).putList((new ListTag("Pos")).add(new DoubleTag("", position.x)).add(new DoubleTag("", position.y)).add(new DoubleTag("", position.z)))
                        .putList(new ListTag("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", -1)).add(new DoubleTag("", Math.cos(0) * Math.cos(0))))
                        .putList((new ListTag("Rotation")).add(new FloatTag("", 0)).add(new FloatTag("", 0)))
                        .putShort("Fire", 0).putDouble("damage", 0d);
                arrow = (EntityArrow)Entity.createEntity("Arrow", position.getChunk(), nbt, new Object[]{null, true});
                position.getLevel().addParticle(new EntityFlameParticle(position));
                arrow.spawnToAll();
                break;
        }
    }


    @Override
    public String toString() {
        return "ExtendRoomData{" +
                "team1=" + team1 +
                ", team2=" + team2 +
                ", team1_score=" + team1_score +
                ", team2_score=" + team2_score +
                ", arrow_spawn1=" + arrow_spawn1 +
                ", arrow_spawn2=" + arrow_spawn2 +
                '}';
    }
}
