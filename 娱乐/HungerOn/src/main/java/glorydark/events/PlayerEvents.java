package glorydark.events;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import glorydark.HungerOn;

public class PlayerEvents extends PlayerEvent implements Listener {
    private static final HandlerList handlerlist = new HandlerList();

    public static HandlerList getHandlers() {
        return handlerlist;
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event){
        Player p = event.getPlayer();
        if(p.isCreative()){ return; }
        if(HungerOn.runtime < event.getPlayer().getServer().getTick() + 20){
            HungerOn.runtime = event.getPlayer().getServer().getTick();
            if(p.getMovementSpeed() > 0 && p.getMovementSpeed() < 0.2) {
                HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Idle")) * HungerOn.GetItemHoldDecreaseRate(p));
            }
            if(p.getMovementSpeed() > 0.2 && p.getMovementSpeed() < 2.0) {
                HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Walk")) * HungerOn.GetItemHoldDecreaseRate(p));
            }
            if(p.getMovementSpeed() > 2f) {
                if(p.fallDistance > 1){
                    return;
                }else{
                    HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Run")) * HungerOn.GetItemHoldDecreaseRate(p));
                }
            }
        }
        if(HungerOn.getHungerValue(p.getName()) >= Integer.parseInt(HungerOn.getConfigValue(1,"MaxDiminishHungerValue"))){
            p.setFoodEnabled(true);
            p.getFoodData().setFoodSaturationLevel(0);
            p.getFoodData().useHunger(Integer.parseInt(HungerOn.getConfigValue(1,"DiminishValue")));
            HungerOn.setHungerValue(p.getName(),0);
        }
    }

    @EventHandler
    public void PlayerGameModeChangeEvent(PlayerGameModeChangeEvent event){
        Player p = event.getPlayer();
        p.setFoodEnabled(p.isSurvival() || p.isAdventure());
    }

    @EventHandler
    public void PlayerEditBookEvent(PlayerEditBookEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "EditBook")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "DropItem")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerTeleportEvent(PlayerTeleportEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Teleport")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Sneak")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerBedEnterEvent(PlayerBedEnterEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "BedEnter")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerToggleGlideEvent(PlayerToggleGlideEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Glide")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerToggleFlightEvent(PlayerToggleFlightEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Flight")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerToggleSprintEvent(PlayerToggleSprintEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Sprint")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerToggleSwimEvent(PlayerToggleSwimEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "Swim")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerBlockPickEvent(PlayerBlockPickEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "BlockPick")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerBedLeaveEvent(PlayerBedLeaveEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "BedLeave")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "AchievementAwarded")) * HungerOn.GetItemHoldDecreaseRate(p));
    }

    @EventHandler
    public void PlayerItemConsumeEvent(PlayerItemConsumeEvent event){
        Player p = event.getPlayer();
        if(!p.isSurvival() && !p.isAdventure()){ return; }
        HungerOn.setHungerValue(p.getName(), HungerOn.getHungerValue(p.getName()) + Integer.parseInt(HungerOn.getConfigValue(1, "ItemConsume")) * HungerOn.GetItemHoldDecreaseRate(p));
    }
}
