package glorydark.SimpleDialogues.particle;

import cn.nukkit.Player;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;

public class Particle {
    public static void CreateParticle(Player p, int type, Position pos, int r){
        switch(type) {
            case 0: // Process Particle
                ParticleEffect particleeffect = ParticleEffect.BASIC_FLAME;
                for(int angle = 0;angle < 360;angle++){
                    if(angle%90 == 0) {
                        double x1 = pos.x + r * Math.cos(angle * 3.14 / 180);
                        double z1 = pos.z + r * Math.sin(angle * 3.14 / 180);
                        p.getLevel().addParticleEffect(new Position(x1, pos.y, z1), particleeffect);
                    }
                }
                break;
            case 1: // Finish Particle
                particleeffect = ParticleEffect.HUGE_EXPLOSION_LEVEL;
                p.getLevel().addParticleEffect(pos, particleeffect);
                break;
        }
    }
}
