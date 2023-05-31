package glorydark.dodgebolt.tasks;

import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.scheduler.Task;

public class ProjectileParticleTask extends Task {
    private EntityArrow arrow;

    public ProjectileParticleTask(EntityArrow arrow){
        this.arrow = arrow;
    }

    @Override
    public void onRun(int i) {
        if(!arrow.isCollided && !arrow.isClosed()) {
            arrow.getLevel().addParticle(new FlameParticle(arrow.getPosition()));
        }else{
            this.cancel();
        }
    }
}
