package glorydark.events;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.utils.Config;
import glorydark.HungerOn;

public class EntityEvents implements Listener{
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlers(){
        return HANDLER_LIST;
    }

    @EventHandler
    public void EntityRegainHealthEvent(EntityRegainHealthEvent event){
        Config cfg = new Config(HungerOn.filepath+"/records.yml",Config.YAML);
        if(cfg.exists("AllowRecoverHungerValue")) {
            if(cfg.getBoolean("AllowRecoverHungerValue")) {
                event.setCancelled(true);
            }
        }else{
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player p = event.getDamager().getServer().getPlayer(event.getDamager().getName());
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "DamageEntity")) * HungerOn.GetItemHoldDecreaseRate(p));
    }
}
