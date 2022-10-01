package glorydark.summonentity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;

public class EventListeners implements Listener {
    @EventHandler
    public void Damage(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if(entity instanceof DefaultEntity && damager instanceof Player){
            if(!damager.equals(((DefaultEntity) entity).getOwner())){
                DefaultEntity e = ((DefaultEntity) entity);
                e.setTarget(damager);
                entity.attack(Float.parseFloat(String.valueOf(e.getDamage())));
            }else{
                ((DefaultEntity) entity).getOwner().sendMessage("主人调教的好！");
            }
        }
    }
}
