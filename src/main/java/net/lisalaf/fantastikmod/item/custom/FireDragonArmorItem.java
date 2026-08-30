package net.lisalaf.fantastikmod.item.custom;

import net.lisalaf.fantastikmod.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@SuppressWarnings("removal")
public class FireDragonArmorItem extends ArmorItem {

    private static final int MAX_EFFECT_DURATION = 7200;

    public FireDragonArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide) return;

        if (!player.getItemBySlot(this.getType().getSlot()).equals(stack)) {
            return;
        }

        int pieces = countArmorPieces(player);
        if (pieces == 0) {
            player.removeEffect(ModEffects.FIRE_DRAGON_SCALES.get());
            return;
        }

        int slowLevel = pieces - 1;
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, slowLevel, false, false, false));

        if (player.isInWaterOrRain()) {
            MobEffectInstance effect = player.getEffect(ModEffects.FIRE_DRAGON_SCALES.get());
            if (effect != null && effect.getDuration() > 0) {
                player.removeEffect(ModEffects.FIRE_DRAGON_SCALES.get());
                int newDuration = effect.getDuration() - 2;
                if (newDuration > 0) {
                    player.addEffect(new MobEffectInstance(ModEffects.FIRE_DRAGON_SCALES.get(), newDuration, effect.getAmplifier(), false, false, true));
                }
            }
            return;
        }

        if (player.isOnFire() || player.isInLava()) {
            MobEffectInstance current = player.getEffect(ModEffects.FIRE_DRAGON_SCALES.get());
            int currentDuration = current != null ? current.getDuration() : 0;
            int newDuration = Math.min(currentDuration + 80, MAX_EFFECT_DURATION);
            player.addEffect(new MobEffectInstance(ModEffects.FIRE_DRAGON_SCALES.get(), newDuration, pieces - 1, false, false, true));
        }
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    public static int countArmorPieces(Player player) {
        int count = 0;
        for (ItemStack armor : player.getInventory().armor) {
            if (armor.getItem() instanceof FireDragonArmorItem) count++;
        }
        return count;
    }
}