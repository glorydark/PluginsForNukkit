package glorydark.lotterybox.tools;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.particle.DestroyBlockParticle;
import lombok.Getter;

@Getter
public class Rarity {
    private final Block particle;

    public Rarity(String string){
        Block block = null;
        if(!string.equals("null")) {
            String[] particleSplit = string.split(":");
            block = Block.get(Integer.parseInt(particleSplit[0]), Integer.parseInt(particleSplit[1]));
        }
        this.particle = block;
    }

    public void addRarityParticle(Player player){
        if(particle == null){ return; }
        player.getLevel().addParticle(new DestroyBlockParticle(player.getPosition(), particle));
    }
}
