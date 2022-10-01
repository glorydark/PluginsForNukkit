package testgame.scripts.controller.type;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import gameapi.GameAPI;
import gameapi.room.Room;
import testgame.scripts.controller.type.ControllerData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Glorydark
 */
@Getter
@Setter
public class TriggerControllerData implements ControllerData {
    private String objectType;
    private String check_type;
    private Object checkValue;

    public TriggerControllerData(String objectType, String check_type, Object checkValue) {
        this.objectType = objectType;
        this.check_type = check_type;
        this.checkValue = checkValue;
    }

    public boolean checkData(Event event, String eventName){
        switch (eventName){
            case "PlayerInteractEvent":
                PlayerInteractEvent PlayerInteractEvent = (PlayerInteractEvent) event;
                return parseEventData(PlayerInteractEvent.getPlayer(), PlayerInteractEvent.getItem(), PlayerInteractEvent.getBlock());
            case "PlayerDropItemEvent":
                PlayerDropItemEvent PlayerDropItemEvent = (PlayerDropItemEvent) event;
                return parseEventData(PlayerDropItemEvent.getPlayer(), PlayerDropItemEvent.getItem());
            case "PlayerJumpEvent":
                PlayerJumpEvent PlayerJumpEvent = (PlayerJumpEvent) event;
                return parseEventData(PlayerJumpEvent.getPlayer());
            case "PlayerMoveEvent":
                PlayerMoveEvent PlayerMoveEvent = (PlayerMoveEvent) event;
                return parseEventData(PlayerMoveEvent.getPlayer());
            case "PlayerEatFoodEvent":
                PlayerEatFoodEvent PlayerEatFoodEvent = (PlayerEatFoodEvent) event;
                return parseEventData(PlayerEatFoodEvent.getPlayer());
            case "PlayerItemHeldEvent":
                PlayerItemHeldEvent PlayerItemHeldEvent = (PlayerItemHeldEvent) event;
                return parseEventData(PlayerItemHeldEvent.getPlayer(), PlayerItemHeldEvent.getItem());
            case "PlayerMouseOverEntityEvent":
                PlayerMouseOverEntityEvent PlayerMouseOverEntityEvent = (PlayerMouseOverEntityEvent) event;
                return parseEventData(PlayerMouseOverEntityEvent.getPlayer(), null, PlayerMouseOverEntityEvent.getEntity());
            case "PlayerToggleSprintEvent":
                PlayerToggleSprintEvent PlayerToggleSprintEvent = (PlayerToggleSprintEvent) event;
                return parseEventData(PlayerToggleSprintEvent.getPlayer());
            case "PlayerToggleSneakEvent":
                PlayerToggleSneakEvent PlayerToggleSneakEvent = (PlayerToggleSneakEvent) event;
                return parseEventData(PlayerToggleSneakEvent.getPlayer());
            case "PlayerInteractEntityEvent":
                PlayerInteractEntityEvent PlayerInteractEntityEvent = (PlayerInteractEntityEvent) event;
                return parseEventData(PlayerInteractEntityEvent.getPlayer(), PlayerInteractEntityEvent.getItem(), PlayerInteractEntityEvent.getEntity());
            case "PlayerDamagePlayerEvent":
                EntityDamageByEntityEvent EntityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                Player victim = (Player) EntityDamageByEntityEvent.getEntity();
                return parseEventData(victim.getInventory().getItemInHand());
            case "PlayerHurtByPlayerEvent":
                EntityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                Player damager = (Player) EntityDamageByEntityEvent.getDamager();
                return parseEventData(damager, damager.getInventory().getItemInHand());
            case "BlockPlaceEvent":
                BlockPlaceEvent BlockPlaceEvent = (BlockPlaceEvent) event;
                return parseEventData(BlockPlaceEvent.getPlayer(), BlockPlaceEvent.getItem(), BlockPlaceEvent.getBlock());
            case "BlockBreakEvent":
                BlockBreakEvent BlockBreakEvent = (BlockBreakEvent) event;
                return parseEventData(BlockBreakEvent.getPlayer(), BlockBreakEvent.getItem(), BlockBreakEvent.getBlock());
        }
        return false;
    }

    public boolean parseEventData(Object... param){
        if(objectType.startsWith("item#")){
            Item item = ((Player) param[0]).getInventory().getItem(Integer.parseInt(checkValue.toString().replace("item#", "")));
            return checkItem(item);
        }

        switch (objectType){
            case "player":
                if(Room.getRoom((Player) param[0]) == null){ return false; }
                Room room = Room.getRoom((Player) param[0]);
                switch (check_type){
                    case "game_name":
                        return room.getGameName().equals(checkValue);
                    case "room_name":
                        return room.getRoomName().equals(checkValue);
                    case "room_sizes":
                        if(checkValue instanceof Integer) {
                            return room.getPlayers().size() == (int) checkValue;
                        }else{
                            wrongFormatWarning();
                        }
                        break;
                }
            case "use_item":
                Item item = ((Item) param[1]);
                return checkItem(item);
            case "entity":
                Entity entity = (Entity) param[2];
                if(check_type.startsWith("named_tag#")){
                    String key = check_type.replace("named_tag#", "");
                    if(checkValue.equals(entity.namedTag.getString(key))){
                        return true;
                    }
                }
                switch (check_type) {
                    case "entity_name":
                        return entity.getName().equals(checkValue);
                    case "entity_nametag":
                        return entity.getNameTag().equals(checkValue);
                }
                break;
            case "victim":
                return checkItem(((Item) param[3]));
            case "damager":
                return checkItem(((Item) param[1]));
            case "block":
                Block block = (Block) param[2];
                switch (check_type) {
                    case "block_id&meta":
                        return (block.getId()+":"+block.getDamage()).equals(checkValue);
                    case "block_name":
                        return block.getName().equals(checkValue);
                }
                break;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TriggerControllerData{" +
                "objectType='" + objectType + '\'' +
                ", check_type='" + check_type + '\'' +
                ", checkValue=" + checkValue +
                '}';
    }

    public boolean checkItem(Item item){
        if(check_type.startsWith("compound_tag#")){
            if(!item.hasCompoundTag()){return false;}
            String key = check_type.replace("compound_tag#", "");
            if(!item.getNamedTag().contains(key)){ return false; }
            if(checkValue.equals(item.getNamedTag().getString(key))){
                return true;
            }
        }
        switch (check_type) {
            case "item_customname":
                return item.getCustomName().equals(checkValue);
            case "item_id":
                return item.getId() == (int) checkValue;
            case "item_count":
                return item.getCount() == (int) checkValue;
            case "item_durability":
            case "item_meta":
                return item.getDamage() == (int) checkValue;
        }
        return false;
    }

    public void wrongFormatWarning(){
        GameAPI.plugin.getLogger().warning("Json Wrong Format! Binding type: "+ check_type + ",Binding values: "+ checkValue.toString());
    }
}
