package net.lisalaf.fantastikmod.entity.ai.kitsune.soul;

import net.lisalaf.fantastikmod.entity.custom.KitsuneSoulPossessedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class KitsuneSoulJumpAttackGoal extends Goal {

    private final KitsuneSoulPossessedEntity mob;
    private int cooldown;
    private boolean hasJumped;

    public KitsuneSoulJumpAttackGoal(KitsuneSoulPossessedEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) { cooldown--; return false; }
        if (mob.isFleeing()) return false;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        double dist = mob.distanceToSqr(target);
        return dist > 4.0 && dist < 256.0 && mob.onGround();
    }

    @Override
    public boolean canContinueToUse() {
        return hasJumped && !mob.onGround();
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        Vec3 dir = target.position().subtract(mob.position()).normalize();
        double jumpPower = Math.sqrt(2 * 0.08 * 2.0); // высота 2 блока
        mob.setDeltaMovement(dir.x * 0.6, jumpPower, dir.z * 0.6);
        mob.hasImpulse = true;

        hasJumped = true;
        mob.playSound(net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, 0.8f, 1.4f);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        if (mob.getDeltaMovement().y < 0 && mob.distanceToSqr(target) < 9.0) {
            if (mob.getRandom().nextFloat() < 0.2f && mob.canAttack()) {
                mob.performFireAttack(target);
            }
        }
    }
    @Override
    public void stop() {
        hasJumped = false;
        cooldown = 60 + mob.getRandom().nextInt(60);
    }
}