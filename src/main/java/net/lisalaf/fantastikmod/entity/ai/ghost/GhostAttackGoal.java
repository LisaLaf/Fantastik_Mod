package net.lisalaf.fantastikmod.entity.ai.ghost;

import net.lisalaf.fantastikmod.entity.custom.GhostGuardsEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class GhostAttackGoal extends Goal {

    private final GhostGuardsEntity ghost;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private int attackTime = -1;
    private int seeTime;
    private final double attackReachSqr;

    public GhostAttackGoal(GhostGuardsEntity ghost, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        this.ghost = ghost;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        double reach = 2.0D;
        this.attackReachSqr = reach * reach;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.ghost.getTarget();
        if (target == null) return false;
        if (!target.isAlive()) return false;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.ghost.getTarget();
        if (target == null) return false;
        if (!target.isAlive()) return false;
        if (!followingTargetEvenIfNotSeen) {
            return !this.ghost.getNavigation().isDone();
        }
        return true;
    }

    @Override
    public void start() {
        this.ghost.setAggressive(true);
    }

    @Override
    public void stop() {
        this.ghost.setAggressive(false);
        this.ghost.getNavigation().stop();
        this.seeTime = 0;
        this.attackTime = -1;
    }

    @Override
    public void tick() {
        LivingEntity target = this.ghost.getTarget();
        if (target == null) return;

        double distSqr = this.ghost.distanceToSqr(target);
        boolean canSee = this.ghost.getSensing().hasLineOfSight(target);

        if (canSee) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        if (distSqr > attackReachSqr) {
            this.ghost.getNavigation().moveTo(target, speedModifier);
        } else {
            this.ghost.getNavigation().stop();
        }

        this.ghost.getLookControl().setLookAt(target, 30.0F, 30.0F);

        this.attackTime = Math.max(this.attackTime - 1, 0);
        if (this.attackTime <= 0 && distSqr <= attackReachSqr) {
            this.attackTime = 38;
            this.ghost.doHurtTarget(target);
        }
    }
}