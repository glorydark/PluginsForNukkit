package testgame.scripts.controller;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.GameAPI;
import gameapi.room.Room;
import lombok.Getter;
import lombok.Setter;
import testgame.scripts.controller.type.TriggerControllerData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Glorydark
 */
@Getter
@Setter
public class Controller {
    private final String binding_type;
    private final Object binding_value;

    private List<TriggerControllerData> triggerControllerData = new ArrayList<>();

    public Controller(String binding_type, String binding_value, List<TriggerControllerData> triggerControllerData){
        this.binding_type = binding_type;
        this.binding_value = binding_value;
        this.triggerControllerData = triggerControllerData;
    }

    public Controller(String binding_type, String binding_value){
        this.binding_type = binding_type;
        this.binding_value = binding_value;
    }

    public boolean isTrigger(Room room){
        switch (binding_type){
            case "game_name":
                return room.getGameName().equals(binding_value);
            case "room_name":
                return room.getRoomName().equals(binding_value);
            case "room_sizes":
                if(binding_value instanceof Integer) {
                    return room.getPlayers().size() == (int) binding_value;
                }else{
                    wrongFormatWarning();
                }
                break;
        }
        return false;
    }

    public boolean isTrigger(Player player){
        if(binding_type.startsWith("held_item_compound_tag#")){
            Item item = player.getInventory().getItemInHand();
            if(!item.hasCompoundTag()){return false;}
            String key = binding_type.replace("compound_tag#", "");
            GameAPI.debug.forEach(p -> p.sendMessage("[GameTest] Check Data: "+ item.getNamedTag().getString(key)));
            if(binding_value.equals(item.getNamedTag().getString(key))){
                return true;
            }
        }
        switch (binding_type){
            case "held_item_name":
                return player.getInventory().getItemInHand().getCustomName().equals(binding_value);
            case "held_item_id":
                if(binding_value instanceof Integer) {
                    return player.getInventory().getItemInHand().getId() == (int) binding_value;
                }else{
                    wrongFormatWarning();
                }
                break;
            case "player_name":
                return player.getName().equals(binding_value);
            case "permission":
                return player.hasPermission(binding_value.toString());
            case "player_status":
                switch (binding_value.toString()){
                    case "sprint":
                        return player.isSprinting();
                    case "glide":
                        return player.isGliding();
                    case "using_item":
                        return player.isUsingItem();
                    case "permission_op":
                        return player.isOp();
                    case "breaking_block":
                        return player.isBreakingBlock();
                    case "inside_of_fire":
                        return player.isInsideOfFire();
                    case "inside_of_solid":
                        return player.isInsideOfSolid();
                    case "inside_of_water":
                        return player.isInsideOfWater();
                    case "sneak":
                        return player.isSneaking();
                }
                break;
        }
        return false;
    }
    
    public void wrongFormatWarning(){
        GameAPI.plugin.getLogger().warning("Json Wrong Format! Binding type: "+binding_type + ",Binding values: "+ binding_value.toString());
    }

    @Override
    public String toString() {
        return "Controller{" +
                "binding_type='" + binding_type + '\'' +
                ", binding_value=" + binding_value +
                ", triggerControllerData= "+ triggerControllerData +
                '}';
    }
}
