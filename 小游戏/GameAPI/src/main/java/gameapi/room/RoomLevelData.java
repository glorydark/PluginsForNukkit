package gameapi.room;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import gameapi.room.Room;
import gameapi.utils.AdvancedLocation;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Glorydark
 */
@Setter
@Getter
public class RoomLevelData {
    private int type; //0 wait  1 start  2 end  3 spectator
    private AdvancedLocation advancedLocation;
    private Room room;

    public RoomLevelData(AdvancedLocation location, Room room, int type){
        this.type = type;
        this.advancedLocation = location;
        this.room = room;
    }

    public void resetLevel(Level level){
        AdvancedLocation loc = advancedLocation;
        Location location = loc.getLocation();
        location.setLevel(level);
        advancedLocation.setLocation(location);
        switch (type){
            case 0:
                room.setWaitSpawn(advancedLocation);
                break;
            case 1:
                room.addStartSpawn(advancedLocation);
                break;
            case 2:
                room.setEndSpawn(advancedLocation);
                break;
            case 3:
                room.addSpectatorSpawn(advancedLocation.getLocation());
                break;
        }
    }
}
