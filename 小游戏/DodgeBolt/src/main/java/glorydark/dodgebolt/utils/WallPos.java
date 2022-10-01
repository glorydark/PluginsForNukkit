package glorydark.dodgebolt.utils;

import cn.nukkit.level.Position;
import gameapi.utils.AdvancedLocation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WallPos {
    private AdvancedLocation wallPos1;
    private AdvancedLocation wallPos2;

    public WallPos(AdvancedLocation wallPos1, AdvancedLocation wallPos2){
        this.wallPos1 = wallPos1;
        this.wallPos2 = wallPos2;
    }

    public boolean checkMovement(Position position){
        int pos_x = position.getFloorX();
        int pos_y = position.getFloorY();
        int pos_z = position.getFloorZ();
        int x_min;
        int x_max;
        int y_min;
        int y_max;
        int z_min;
        int z_max;
        if(wallPos1.getLocation().getFloorX() <= wallPos2.getLocation().getFloorX()){
            x_min = wallPos1.getLocation().getFloorX();
            x_max = wallPos2.getLocation().getFloorX();
        }else{
            x_min = wallPos2.getLocation().getFloorX();
            x_max = wallPos1.getLocation().getFloorX();
        }

        if(wallPos1.getLocation().getFloorY() <= wallPos2.getLocation().getFloorY()){
            y_min = wallPos1.getLocation().getFloorY();
            y_max = wallPos2.getLocation().getFloorY();
        }else{
            y_min = wallPos2.getLocation().getFloorY();
            y_max = wallPos1.getLocation().getFloorY();
        }

        if(wallPos1.getLocation().getFloorZ() <= wallPos2.getLocation().getFloorZ()){
            z_min = wallPos1.getLocation().getFloorZ();
            z_max = wallPos2.getLocation().getFloorZ();
        }else{
            z_min = wallPos2.getLocation().getFloorZ();
            z_max = wallPos1.getLocation().getFloorZ();
        }

        if(pos_x <= x_max && pos_x >= x_min){
            if(pos_y <= y_max && pos_y >= y_min){
                return pos_z <= z_max && pos_z >= z_min;
            }
        }
        return false;
    }
}
