package glorydark.customform.scriptForms;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class SoundData {
    private Sound sound;
    private float volume;
    private float pitch;
    private boolean isPersonal;

    public SoundData(String sound, float volume, float pitch, boolean isPersonal){
        Optional<Sound> find = Arrays.stream(Sound.values()).filter(get -> get.getSound().equals(sound)).findAny();
        if(find.isPresent()){
            this.sound = find.get();
        }else{
            this.sound = null;
        }
        this.volume = volume;
        this.pitch = pitch;
        this.isPersonal = isPersonal;
    }

    public void addSound(Player player){
        if(sound == null){ return; }
        if(isPersonal) {
            player.getLevel().addSound(player.getLocation(), sound, volume, pitch, player);
        }else{
            player.getLevel().addSound(player.getLocation(), sound, volume, pitch);
        }
    }
}
