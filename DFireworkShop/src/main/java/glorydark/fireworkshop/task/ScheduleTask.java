package glorydark.fireworkshop.task;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import glorydark.fireworkshop.MainClass;
import glorydark.fireworkshop.api.CreateFireworkApi;
import glorydark.fireworkshop.event.EventListener;
import glorydark.fireworkshop.utils.FireworkData;

import java.util.HashMap;

public class ScheduleTask extends Task implements Runnable {
    public static HashMap<Player,Integer> tickcache = new HashMap<>();

    @Override
    public void onRun(int i) {
        for (Player p:MainClass.playerfireworkcache.keySet()){
            if(tickcache.containsKey(p)){
                if(tickcache.get(p) >= MainClass.MaxTickRecord.get(p)){
                    tickcache.remove(p);
                    MainClass.playerfireworkcache.remove(p);
                    MainClass.arrivepos = null;
                    return;
                }
                int tick = tickcache.get(p) + 1;
                tickcache.put(p,tick);
                if(MainClass.playerfireworkcache.get(p).get(tick) != null) {
                    for(FireworkData fireworkData : MainClass.playerfireworkcache.get(p).get(tick)) {
                        CreateFireworkApi.spawnFirework(fireworkData.position, fireworkData.color, fireworkData.explosionType);
                        Double tickCalculate = Double.valueOf(tick/10);
                        if(tickCalculate == tick/10){
                            EventListener.CreateCircleParticle(0, fireworkData.position, 5);
                        }
                    }
                }
                return;
            }
        }
    }
}
