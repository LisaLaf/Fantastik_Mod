package net.lisalaf.fantastikmod.entity.ai.kitsune.soul;

import net.lisalaf.fantastikmod.entity.custom.KitsuneSoulPossessedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class KitsuneSoulAttackGoal extends Goal {

    private final KitsuneSoulPossessedEntity mob;
    private final double speedModifier;
    private final double attackReach;
    private int pathRecalcTicks;

    public KitsuneSoulAttackGoal(KitsuneSoulPossessedEntity mob) {
        this(mob, 1.2D, 2.5D);
    }

    public KitsuneSoulAttackGoal(KitsuneSoulPossessedEntity mob, double speed, double reach) {
        this.mob = mob;
        this.speedModifier = speed;
        this.attackReach = reach;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob.isFleeing()) return false;
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        if (mob.isFleeing()) return false;
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.pathRecalcTicks = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        mob.getLookControl().setLookAt(target, 30f, 30f);

        double dist = mob.distanceToSqr(target);

        if (--pathRecalcTicks <= 0) {
            pathRecalcTicks = 4 + mob.getRandom().nextInt(7);
            mob.getNavigation().moveTo(target, speedModifier);
        }

        if (dist <= attackReach * attackReach && mob.canAttack()) {
            if (mob.getRandom().nextFloat() < 0.7f) {
                mob.performFireAttack(target);
            } else {
                if (mob.canStartFleeing()) {
                    mob.startFleeing();
                }
            }
        }
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }
}