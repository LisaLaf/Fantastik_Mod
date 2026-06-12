package net.lisalaf.fantastikmod.entity.ai.kitsune;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.network.chat.Component;

import java.util.EnumSet;

public class KitsuneTofuBlessingGoal extends Goal {
    private final KitsuneLightEntity kitsune;
    private long blessingDay = -1;
    private boolean blessingActive;

    public KitsuneTofuBlessingGoal(KitsuneLightEntity kitsune) {
        this.kitsune = kitsune;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return kitsune.isTamed() && blessingActive;
    }

    @Override
    public boolean canContinueToUse() {
        return kitsune.isTamed() && blessingActive;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void activate() {
        this.blessingDay = kitsune.level().getDayTime() / 24000L;
        this.blessingActive = true;
    }

    @Override
    public void tick() {
        if (!blessingActive) return;

        long currentDay = kitsune.level().getDayTime() / 24000L;
        if (currentDay != blessingDay) {
            blessingActive = false;
            return;
        }

        LivingEntity owner = kitsune.getOwner();
        if (owner == null) {
            return;
        }
        if (!(owner instanceof Player)) {
            return;
        }
        Player player = (Player) owner;

        double dist = kitsune.distanceToSqr(player);
        if (dist > 256.0D) {
            if (kitsune.tickCount % 200 == 0) System.out.println("TofuBlessing: player too far. Dist: " + dist);
            return;
        }

        if (kitsune.tickCount % 100 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 0, false, false, true));
        }
    }
}