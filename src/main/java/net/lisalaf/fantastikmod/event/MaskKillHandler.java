package net.lisalaf.fantastikmod.event;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.custom.KitsuneSoulMaskItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = fantastikmod.MOD_ID)
public class MaskKillHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (event.getEntity() == player) return;
        if (event.getEntity() instanceof Player) return;
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(head.getItem() instanceof KitsuneSoulMaskItem)) return;

        CompoundTag tag = head.getOrCreateTag();
        int charge = tag.getInt(KitsuneSoulMaskItem.TAG_CHARGE);
        if (charge >= KitsuneSoulMaskItem.MAX_CHARGE) return;

        charge = Math.min(KitsuneSoulMaskItem.MAX_CHARGE, charge + KitsuneSoulMaskItem.POINTS_PER_KILL);
        tag.putInt(KitsuneSoulMaskItem.TAG_CHARGE, charge);
        head.setTag(tag);
    }
}