package glorydark.DLevelEventPlus.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartTNT;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.*;
import cn.nukkit.level.Level;
import glorydark.DLevelEventPlus.MainClass;
import glorydark.DLevelEventPlus.utils.ConfigUtil;

public class EntityEventListener implements Listener {
    //Entity
    @EventHandler
    public void EntityInteractEvent(EntityInteractEvent event) {
        //实体导致耕地踩踏
        Entity entity = event.getEntity();
        if (event.getBlock().getId() == Block.FARMLAND && !(entity instanceof Player)) {
            Boolean bool = MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(), "World","FarmProtect");
            if (bool != null && bool) {
                event.setCancelled(true);
            }
        }


        if (event.getBlock().getId() == Block.ITEM_FRAME_BLOCK && !(entity instanceof Player)) {
            if(MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(),"World","AllowInteractFrameBlock") == null){return;}
            Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Player","AllowInteractFrameBlock");
            if (bool != null && bool) {
                event.setCancelled(true);
            }
        }
    }

    //TNT爆炸
    @EventHandler
    public void EntityExplodeEvent(EntityExplodeEvent event){
        Entity entity = event.getEntity();
        Boolean bool1 = MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(),"World","TntExplodes");
        Boolean bool2 = MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(),"World","AllExplodes");
        if(bool1 == null){return;}
        if(bool2 == null){return;}
        if(entity instanceof EntityMinecartTNT || entity instanceof EntityPrimedTNT) {
            if (!bool1) {
                entity.despawnFromAll();
                if(MainClass.show_actionbar_text) {
                    MainClass.server.getLogger().info(ConfigUtil.getLang("Tips", "AntiTntExplode"));
                }
                event.setCancelled(true);
            }
        }
        if (!bool2) {
            entity.despawnFromAll();
            if(MainClass.show_actionbar_text) {
                MainClass.server.getLogger().info(ConfigUtil.getLang("Tips", "AntiAllExplode"));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityExplosionPrimeEvent(EntityExplosionPrimeEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(),"World","AllExplodes");
        if(bool == null){return;}
        if (!bool) {
            if(MainClass.show_actionbar_text) {
                MainClass.server.getLogger().info(ConfigUtil.getLang("Tips", "AntiTntExplode"));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(),"Player","NoFallDamage");
        if(bool == null){return;}
        if(event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            event.setCancelled(bool);
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(),"World","PVP");
        if(bool == null){return;}
        Entity entity = event.getEntity();
        if(entity instanceof Player && event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            if(ConfigUtil.isAdmin(p)){ return; }
            if(ConfigUtil.isOperator(p, event.getEntity().getLevel())){ return; }
            if (!bool) {
                p.sendActionBar(ConfigUtil.getLang("Tips","AntiPvp"));
                if(MainClass.show_actionbar_text) {
                    ((Player) event.getDamager()).sendActionBar(ConfigUtil.getLang("Tips", "AntiPvp"));
                }
                event.setCancelled();
            }
        }
    }

    @EventHandler
    public void EntityPortalEnterEvent(EntityPortalEnterEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(),"Entity","PortalEnter");
        if(bool == null){return;}
        Entity entity = event.getEntity();
        if(entity instanceof Player) {
            Player p = (Player) event.getEntity();
            if(ConfigUtil.isAdmin(p)){ return; }
            if(ConfigUtil.isOperator(p, p.getLevel())){ return; }
            if (!bool) {
                if(MainClass.show_actionbar_text) {
                    p.sendActionBar(ConfigUtil.getLang("Tips", "AntiPortalEnter"));
                }
                event.setCancelled(true);
            }
        }else{
            if (!bool) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void EntityVehicleEnterEvent(EntityVehicleEnterEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getEntity().getLevel().getName(),"Entity","VehicleEnter");
        if(bool == null){return;}
        Entity entity = event.getEntity();
        if(entity instanceof Player) {
            Player p = (Player) event.getEntity();
            if(ConfigUtil.isAdmin(p)){ return; }
            if(ConfigUtil.isOperator(p, p.getLevel())){ return; }
            if (!bool) {
                if(MainClass.show_actionbar_text) {
                    p.sendActionBar(ConfigUtil.getLang("Tips", "AntiVehicleEnter"));
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void EntityLevelChangeEvent(EntityLevelChangeEvent event){
        if(!(event.getEntity() instanceof Player)){return;}
        Player p = ((Player) event.getEntity()).getPlayer();
        Level level = event.getTarget();
        if(ConfigUtil.isAdmin(p)){ return; }
        if(ConfigUtil.isOperator(p, level)){ return;}
        boolean bool = ConfigUtil.isWhiteListed(p,level);
        if(bool){return;}
        if(MainClass.show_actionbar_text) {
            p.sendActionBar(ConfigUtil.getLang("Tips", "AntiTeleport").replace("%s", level.getName()));
        }
        event.setCancelled(true);
    }
}
