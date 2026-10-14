package net.lisalaf.fantastikmod.entity.ai.kitsune.soul;

import net.lisalaf.fantastikmod.entity.custom.KitsuneSoulPossessedEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;

import java.util.EnumSet;
import java.util.List;

public class KitsuneSoulHuntAnimalsGoal extends Goal {

    private final KitsuneSoulPossessedEntity mob;
    private final double speed;
    private Animal targetAnimal;
    private int searchCooldown;

    private static final double SEARCH_RADIUS = 24.0;

    public KitsuneSoulHuntAnimalsGoal(KitsuneSoulPossessedEntity mob) {
        this(mob, 1.1D);
    }

    public KitsuneSoulHuntAnimalsGoal(KitsuneSoulPossessedEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob.getTarget() != null) return false; // приоритет — игрок
        if (searchCooldown > 0) { searchCooldown--; return false; }

        List<Animal> animals = mob.level().getEntitiesOfClass(Animal.class,
                mob.getBoundingBox().inflate(SEARCH_RADIUS),
                a -> a.isAlive() && isHuntable(a));

        if (animals.isEmpty()) {
            searchCooldown = 60;
            return false;
        }

        targetAnimal = animals.get(mob.getRandom().nextInt(animals.size()));
        return true;
    }

    private boolean isHuntable(Animal animal) {
        return animal instanceof Chicken
                || animal instanceof Rabbit
                || animal instanceof Cow
                || animal instanceof Sheep
                || animal instanceof Pig;
    }

    @Override
    public boolean canContinueToUse() {
        return targetAnimal != null && targetAnimal.isAlive() && mob.getTarget() == null;
    }

    @Override
    public void tick() {
        if (targetAnimal == null) return;

        mob.getLookControl().setLookAt(targetAnimal, 30f, 30f);
        mob.getNavigation().moveTo(targetAnimal, speed);

        double dist = mob.distanceToSqr(targetAnimal);
        if (dist <= 4.0) {
            targetAnimal.hurt(mob.damageSources().mobAttack(mob), 1000f);
            mob.playSound(net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP, 0.8f, 1.0f);
            targetAnimal = null;
        }
    }

    @Override
    public void stop() {
        targetAnimal = null;
        mob.getNavigation().stop();
    }
}