package net.lisalaf.fantastikmod.entity.ai.kitsune;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class KitsuneExplodeCreepersGoal extends Goal {
    private final KitsuneLightEntity kitsune;
    private int checkCooldown = 0;

    public KitsuneExplodeCreepersGoal(KitsuneLightEntity kitsune) {
        this.kitsune = kitsune;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!kitsune.isTamed()) return false;
        if (kitsune.isSitting() || kitsune.isSleeping()) return false;
        if (checkCooldown > 0) { checkCooldown--; return false; }

        LivingEntity owner = kitsune.getOwner();
        if (owner == null) return false;

        AABB checkArea = owner.getBoundingBox().inflate(12.0D);
        List<Creeper> creepers = kitsune.level().getEntitiesOfClass(Creeper.class, checkArea);

        return !creepers.isEmpty();
    }

    @Override
    public void start() {
        LivingEntity owner = kitsune.getOwner();
        if (owner == null) return;

        AABB checkArea = owner.getBoundingBox().inflate(12.0D);
        List<Creeper> creepers = kitsune.level().getEntitiesOfClass(Creeper.class, checkArea);

        for (Creeper creeper : creepers) {
            creeper.getPersistentData().putBoolean("KitsuneProtectedExplosion", true);
            creeper.getPersistentData().putLong("KitsuneProtectionTime", kitsune.level().getGameTime() + 60);
        }

        checkCooldown = 200;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}