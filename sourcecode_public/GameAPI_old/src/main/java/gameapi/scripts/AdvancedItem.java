package gameapi.scripts;

import gameapi.effect.Effect;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//To do
@Getter
@Setter
public class AdvancedItem {
    private Integer id;
    private Integer meta;
    private Integer damage;
    private List<String> trigger_events = new ArrayList<>();
    private List<Effect> effects = new ArrayList<>();
    private String lore;
}
