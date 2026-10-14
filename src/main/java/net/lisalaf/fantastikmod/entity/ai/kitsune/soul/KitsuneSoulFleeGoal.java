package net.lisalaf.fantastikmod.entity.ai.kitsune.soul;

import net.lisalaf.fantastikmod.entity.custom.KitsuneSoulPossessedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class KitsuneSoulFleeGoal extends Goal {

    private final KitsuneSoulPossessedEntity mob;
    private final double speed;
    private int fleeTicks;

    public KitsuneSoulFleeGoal(KitsuneSoulPossessedEntity mob) {
        this(mob, 1.4D);
    }

    public KitsuneSoulFleeGoal(KitsuneSoulPossessedEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.isFleeing() && mob.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.isFleeing() && mob.getTarget() != null && fleeTicks > 0;
    }

    @Override
    public void start() {
        fleeTicks = 100 + mob.getRandom().nextInt(100); // 5-10 сек
    }

    @Override
    public void tick() {
        fleeTicks--;

        LivingEntity target = mob.getTarget();
        if (target == null) return;

        Vec3 fleeDir = mob.position().subtract(target.position()).normalize();
        Vec3 dest = mob.position().add(fleeDir.scale(10));

        mob.getNavigation().moveTo(dest.x, dest.y, dest.z, speed);
        mob.getLookControl().setLookAt(target, 30f, 30f);

        if (fleeTicks <= 0) {
            mob.stopFleeing();
        }
    }

    @Override
    public void stop() {
        mob.stopFleeing();
        mob.getNavigation().stop();
    }
}