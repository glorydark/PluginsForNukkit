package glorydark.backtolobby;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;

public class ParticleSchedule extends Task {
    private Player player;
    private int time = 0;

    private int angle1 = 0;
    private int angle2 = 180;

    private int maxTicks;

    private Position pos1;

    private Position pos2;

    private Location spawn;

    public ParticleSchedule(Player player, int maxTicks, Location spawn){
        this.player = player;
        this.pos1 = player.getPosition();
        this.pos2 = player.getPosition();
        this.maxTicks = maxTicks;
        this.spawn = spawn;
    }

    @Override
    public void onRun(int i) {
        time++;
        if(!MainClass.playerList.contains(player) || !player.isOnline()){
            this.cancel();
            return;
        }
        if(time > maxTicks){
            player.sendTitle((String) MainClass.configs.getOrDefault("back-title", "回城成功！"), (String) MainClass.configs.getOrDefault("back-subtitle", ""));
            player.removeEffect(Effect.LEVITATION);
            Effect effect = Effect.getEffect(Effect.SLOW_FALLING);
            effect.setDuration(30);
            player.addEffect(effect);
            player.teleport(spawn, null);
            MainClass.playerList.remove(player);
            this.cancel();
            return;
        }
        angle1+=18;
        angle2+=18;
        showParticle(pos1, angle1, 2);
        pos1.setY(pos1.getY()+0.1);
        showParticle(pos2, angle2, 2);
        pos2.setY(pos2.getY()+0.1);
    }

    public void showParticle(Position pos, int angle, int r){
        ParticleEffect effect = ParticleEffect.BLUE_FLAME;
        double x1 = pos.x + r * Math.cos(angle * 3.14 / 180);
        double z1 = pos.z + r * Math.sin(angle * 3.14 / 180);
        double y1 = pos.y + 0.1;
        Position newPos = new Position(x1, y1, z1, pos.getLevel());
        newPos.getLevel().addParticleEffect(newPos, effect);
    }
}
