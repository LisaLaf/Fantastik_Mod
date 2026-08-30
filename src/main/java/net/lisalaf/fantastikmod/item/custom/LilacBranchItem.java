package net.lisalaf.fantastikmod.item.custom;

import net.lisalaf.fantastikmod.effect.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class LilacBranchItem extends Item {

    private static final long EXPIRATION_TIME = 7 * 24000;

    public LilacBranchItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) return;

        if (selected && entity instanceof Player player) {
            player.addEffect(new MobEffectInstance(ModEffects.LILAC_AROMA.get(), 200, 0, false, false, true));
        }

        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("creationTime")) {
            tag.putLong("creationTime", level.getDayTime());
        }

        long creationTime = tag.getLong("creationTime");
        if (level.getDayTime() - creationTime >= EXPIRATION_TIME) {
            ItemStack stick = new ItemStack(Items.STICK, stack.getCount());
            if (entity instanceof Player player) {
                player.getInventory().setItem(slot, stick);
            }
        }
    }
}