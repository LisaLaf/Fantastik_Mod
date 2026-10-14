package net.lisalaf.fantastikmod.entity.ai.kitsune.soul;

import net.lisalaf.fantastikmod.entity.custom.KitsuneSoulPossessedEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class KitsuneSoulEatFoodGoal extends Goal {

    private final KitsuneSoulPossessedEntity mob;
    private int eatCooldown;

    public KitsuneSoulEatFoodGoal(KitsuneSoulPossessedEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob.getHealth() >= mob.getMaxHealth()) return false;
        if (eatCooldown > 0) { eatCooldown--; return false; }

        for (ItemStack stack : mob.getHandSlots()) {
            if (isFood(stack)) return true;
        }
        return false;
    }

    private boolean isFood(ItemStack stack) {
        return stack.is(Items.COOKED_BEEF) || stack.is(Items.BEEF)
                || stack.is(Items.COOKED_PORKCHOP) || stack.is(Items.PORKCHOP)
                || stack.is(Items.COOKED_CHICKEN) || stack.is(Items.CHICKEN)
                || stack.is(Items.COOKED_MUTTON) || stack.is(Items.MUTTON)
                || stack.is(Items.COOKED_RABBIT) || stack.is(Items.RABBIT)
                || stack.is(Items.ROTTEN_FLESH);
    }

    @Override
    public boolean canContinueToUse() {
        return mob.isEating();
    }

    @Override
    public void start() {
        mob.setEating(true);
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (mob.tryEatFood()) {
            eatCooldown = 40;
        } else {
            eatCooldown = 20;
        }
    }

    @Override
    public void stop() {
        mob.setEating(false);
        eatCooldown = 60;
    }
}