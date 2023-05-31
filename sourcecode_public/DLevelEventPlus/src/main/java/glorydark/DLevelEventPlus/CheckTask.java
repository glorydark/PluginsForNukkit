package glorydark.DLevelEventPlus;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.Task;
public class CheckTask extends Task {
    @Override
    public void onRun(int i) {
        for(Level level: Server.getInstance().getLevels().values()){
            if(level.getPlayers().size() > 0){
                Boolean bool = MainClass.getLevelBooleanInit(level.getName(), "Player", "Interact");
                Boolean bool1 = MainClass.getLevelBooleanInit(level.getName(), "Block", "AllowPlaceBlock");
                Boolean bool2 = MainClass.getLevelBooleanInit(level.getName(), "Block", "AllowBreakBlock");
                boolean final1 = bool != null && !bool;
                boolean final2 = bool1 != null && bool2 != null && !bool1 && !bool2;
                level.getPlayers().values().forEach(player -> {
                    player.setAllowInteract(!final1);
                    player.setAllowModifyWorld(!final2);
                    player.sendCommandData();
                });
            }
        }
    }
}
