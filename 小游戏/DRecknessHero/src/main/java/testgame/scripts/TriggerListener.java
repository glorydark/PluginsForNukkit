package testgame.scripts;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJumpEvent;
import gameapi.GameAPI;
import gameapi.room.Room;
import testgame.MainClass;
import testgame.scripts.controller.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Glorydark
 * Specially for DRecknessHero
 */
public class TriggerListener implements Listener {

    public static List<String> eventList = new ArrayList<>();

    public boolean hasEventCheck(Event event){
        String[] split = event.getEventName().split("\\.");
        String checkValue = split[split.length - 1];
        return eventList.contains(checkValue);
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room == null){ return; }
        if(!room.getGameName().equals("DRecknessHero")){ return; }
        if(hasEventCheck(event)) {
            checkEvent(event);
        }
    }

    @EventHandler
    public void PlayerJumpEvent(PlayerJumpEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room == null){ return; }
        if(!room.getGameName().equals("DRecknessHero")){ return; }
        if(hasEventCheck(event)) {
            checkEvent(event);
        }
    }

    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
        Room room = Room.getRoom(event.getPlayer());
        if(room == null){ return; }
        if(!room.getGameName().equals("DRecknessHero")){ return; }
        if(hasEventCheck(event)) {
            checkEvent(event);
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Room room = Room.getRoom((Player) event.getDamager());
            Room room1 = Room.getRoom((Player) event.getEntity());
            if(room == null){ return; }
            if(room1 == null){ return; }
            if(room == room1 && room.getGameName().equals("DRecknessHero")) {
                if (hasEventCheck(event)) {
                    checkEvent(event);
                }
            }
        }
    }

    public void checkEvent(Event event){
        for (CustomSkill customSkill: MainClass.skills.values()){
            GameAPI.debug.forEach(player -> player.sendMessage("[GameTest] Check Skill: "+ customSkill.getCustomName()));
            for(Controller controller: customSkill.getControllers()) {
                GameAPI.debug.forEach(player -> player.sendMessage("[GameTest] Check controller, data: "+ controller));
                String[] split = event.getEventName().split("\\.");
                String checkValue = split[split.length - 1];
                GameAPI.debug.forEach(player -> player.sendMessage("[GameTest] Check controller, data: "+ controller));
                AtomicReference<Boolean> state = new AtomicReference<>(true);
                if (checkValue.equals(controller.getBinding_value())) {
                    if(event instanceof PlayerEvent) {
                        customSkill.getControllers().forEach(check_data -> check_data.getTriggerControllerData().forEach(
                                data -> {
                                    if(data.checkData(event, checkValue)){
                                        state.set(true);
                                    }
                                }
                        ));
                        if (!state.get()) {
                            continue;
                        }
                        GameAPI.debug.forEach(player -> player.sendMessage("[GameTest] Trigger Event Success. Skill Name: " + customSkill.getCustomName()));
                        customSkill.triggerSkill(((PlayerEvent) event).getPlayer());
                    }
                } else {
                    if (event instanceof EntityDamageByEntityEvent) {
                        EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent) event;
                        if (controller.getBinding_value().equals("PlayerDamageByPlayerEvent")) {
                            customSkill.getControllers().forEach(check_data -> check_data.getTriggerControllerData().forEach(
                                    data -> state.set(data.checkData(event, "PlayerDamageByPlayerEvent"))
                            ));
                            if (!state.get()) {
                                continue;
                            }
                            Player damager = (Player) newEvent.getDamager();
                            if(GameAPI.debug.contains(damager)){
                                damager.sendMessage("[GameTest] Trigger Event Success. Skill Name: " + customSkill.getCustomName());
                            }
                            customSkill.triggerSkill((Player) newEvent.getEntity(), ((EntityDamageByEntityEvent) event).getDamager(), ((EntityDamageByEntityEvent) event).getEntity());
                        }
                        if (controller.getBinding_value().equals("PlayerHurtByPlayerEvent")) {
                            customSkill.getControllers().forEach(check_data -> check_data.getTriggerControllerData().forEach(
                                    data -> state.set(data.checkData(event, "PlayerHurtByPlayerEvent"))
                            ));
                            if (!state.get()) {
                                continue;
                            }
                            Player entity = (Player) newEvent.getDamager();
                            if(GameAPI.debug.contains(entity)){
                                entity.sendMessage("[GameTest] Trigger Event Success. Skill Name: " + customSkill.getCustomName());
                            }
                            customSkill.triggerSkill((Player) newEvent.getEntity(), ((EntityDamageByEntityEvent) event).getDamager(), ((EntityDamageByEntityEvent) event).getEntity());
                        }
                    }
                }
            }
        }
    }

    public static void addEventList(String event) {
        eventList.add(event);
    }
}
