package net.lisalaf.fantastikmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FireDragonScalesEffect extends MobEffect {

    public FireDragonScalesEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4400);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, amplifier, false, false, false));

            if (!player.isInWaterOrRain() && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                player.hurt(player.damageSources().onFire(), 1.0f);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}