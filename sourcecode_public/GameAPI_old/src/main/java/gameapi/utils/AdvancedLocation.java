package gameapi.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import gameapi.GameAPI;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class AdvancedLocation {
    private Location location;
    private double yaw;
    private double pitch;
    private double headYaw;
    private int version;

    public AdvancedLocation(){}

    public AdvancedLocation(String string){
        AdvancedLocation loc = getLocationByString(string);
        switch (loc.getVersion()){
            case 2:
                this.headYaw = loc.getHeadYaw();
            case 1:
                this.yaw = loc.getYaw();
                this.pitch = loc.getPitch();
            case 0:
                this.location = loc.getLocation();
                break;
        }
    }

    public Level getLevel() {
        return location.getLevel();
    }

    public void teleport(Player player){
        if(location == null || !location.isValid()){ return; }
        Location out;
        if (version == 1) {
            out = new Location(location.getX(), location.getY(), location.getZ(), yaw, pitch);
            out.setLevel(location.getLevel());
        }else{
            out = location;
        }
        player.teleportImmediate(out, null);
    }

    public AdvancedLocation getLocationByString(String string){
        String[] positions = string.split(":");
        if(positions.length < 4){
            if(positions.length == 3){
                AdvancedLocation loc = new AdvancedLocation();
                loc.setLocation(new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2])));
                loc.setVersion(0);
                return loc;
            }
            GameAPI.plugin.getLogger().warning("检测到坐标格式错误，请修改！");
            return null;
        }
        if(!Server.getInstance().isLevelLoaded(positions[3])){
            if(Server.getInstance().loadLevel(positions[3])){
                Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
                AdvancedLocation advancedLocation = new AdvancedLocation();
                advancedLocation.setLocation(location);
                advancedLocation.setVersion(0);
                if(positions.length >= 6){
                    advancedLocation.setYaw(Double.parseDouble(positions[4]));
                    advancedLocation.setPitch(Double.parseDouble(positions[5]));
                    advancedLocation.setVersion(1);
                    if(positions.length == 7){
                        advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                        advancedLocation.setVersion(2);
                    }
                }
                return advancedLocation;
            }else{
                return null;
            }
        }else{
            Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
            AdvancedLocation advancedLocation = new AdvancedLocation();
            advancedLocation.setLocation(location);
            advancedLocation.setVersion(0);
            if(positions.length >= 6){
                advancedLocation.setYaw(Double.parseDouble(positions[4]));
                advancedLocation.setPitch(Double.parseDouble(positions[5]));
                advancedLocation.setVersion(1);
                if(positions.length == 7){
                    advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                    advancedLocation.setVersion(2);
                }
            }
            return advancedLocation;
        }
    }
}