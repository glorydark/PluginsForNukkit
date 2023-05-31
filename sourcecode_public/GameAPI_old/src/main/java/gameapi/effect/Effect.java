package gameapi.effect;

import cn.nukkit.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author Glorydark
 */
@Getter
@Setter
public class Effect {
    Integer id;
    Integer amplifier;
    Integer duration;

    boolean bad = false;

    boolean visible = true;

    boolean ambient = false;

    String name = "";

    int[] rgb = new int[3];

    int version = 1;

    public Effect(int id, int amplifier, int duration){
        this.id = id;
        this.amplifier = amplifier;
        this.duration = duration;
        this.name = cn.nukkit.potion.Effect.getEffect(id).getName();
    }

    public void giveEffect(Player player){
        if(version == 1){
            cn.nukkit.potion.Effect effect = cn.nukkit.potion.Effect.getEffect(id);
            effect.setDuration(duration);
            effect.setAmplifier(amplifier);
            player.addEffect(effect);
            return;
        }
        if(version == 2) {
            cn.nukkit.potion.Effect give = new cn.nukkit.potion.Effect(id, name, rgb[0], rgb[1], rgb[2]);
            give.setVisible(visible);
            give.setAmplifier(amplifier);
            give.setDuration(duration);
            give.setAmbient(ambient);
            player.addEffect(give);
        }
    }

    public Effect(Map<String, Object> map){
        this.id = (Integer) map.get("id");
        this.amplifier = (Integer) map.get("amplifier");
        this.duration = (Integer) map.get("duration");
        if(map.containsKey("name")){
            this.name = (String) map.get("name");
        }
        if(map.containsKey("bad")){
            this.bad = (boolean) map.get("bad");
        }
        if(map.containsKey("visible")){
            this.bad = (boolean) map.get("visible");
        }
        if(map.containsKey("ambient")){
            this.bad = (boolean) map.get("ambient");
        }
        if(map.containsKey("color")){
            List<Integer> rgbs = (List<Integer>)map.get("color");
            rgb[0] = rgbs.get(0);
            rgb[1] = rgbs.get(1);
            rgb[2] = rgbs.get(2);
        }
        this.version = 2;
    }

    @Override
    public String toString() {
        return "Effect{" +
                "id=" + id +
                ", amplifier=" + amplifier +
                ", duration=" + duration +
                ", bad=" + bad +
                ", visible=" + visible +
                ", ambient=" + ambient +
                ", name='" + name + '\'' +
                ", rgb=" + Arrays.toString(rgb) +
                ", version=" + version +
                '}';
    }
}
