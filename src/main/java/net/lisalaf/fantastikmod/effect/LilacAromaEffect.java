package net.lisalaf.fantastikmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class LilacAromaEffect extends MobEffect {

    public LilacAromaEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFC8A2D8);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "a1b2c3d4-e5f6-7890-abcd-ef1234567890", 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                player.getFoodData().setSaturation(Math.min(player.getFoodData().getSaturationLevel() + 0.5f, 20.0f));
            }
            if (entity.getHealth() < entity.getMaxHealth() && entity.tickCount % 200 == 0) {
                entity.heal(0.0F);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}