package net.lisalaf.fantastikmod.event;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.ModItems;
import net.lisalaf.fantastikmod.item.custom.KitsuneSoulMaskItem;
import net.lisalaf.fantastikmod.network.OpenMaskDialogPacket;
import net.lisalaf.fantastikmod.dialog.mobs.kitsune.kitsune1tail.Kitsune1TailSoulMaskDialog;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = fantastikmod.MOD_ID)
public class MaskDialogHandler {

    private static final Map<UUID, Integer> CHECK_COOLDOWN = new HashMap<>();
    private static final Map<UUID, UUID> SHOWN_FOR_MASK = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        UUID uuid = player.getUUID();
        int cooldown = CHECK_COOLDOWN.getOrDefault(uuid, 0);
        if (cooldown > 0) {
            CHECK_COOLDOWN.put(uuid, cooldown - 1);
            return;
        }
        CHECK_COOLDOWN.put(uuid, 80);
        ItemStack maskStack = findMaskInInventory(player);
        if (maskStack.isEmpty()) return;
        CompoundTag tag = maskStack.getOrCreateTag();
        if (tag.getBoolean("kitsuneRefused")) return;
        UUID maskId = getMaskId(maskStack, tag);
        UUID shownMask = SHOWN_FOR_MASK.get(uuid);
        if (shownMask != null && shownMask.equals(maskId)) return;
        KitsuneLightEntity kitsune = findNearestTamedKitsune(player);
        if (kitsune == null) return;
        if (!uuid.equals(kitsune.getOwnerUUID())) return;
        SHOWN_FOR_MASK.put(uuid, maskId);
        fantastikmod.sendToPlayer(new OpenMaskDialogPacket(kitsune.getId()), player);
    }

    private static ItemStack findMaskInInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.getItem() instanceof KitsuneSoulMaskItem) return s;
        }
        return ItemStack.EMPTY;
    }

    private static UUID getMaskId(ItemStack stack, CompoundTag tag) {
        if (!tag.hasUUID("maskUniqueId")) {
            tag.putUUID("maskUniqueId", UUID.randomUUID());
        }
        return tag.getUUID("maskUniqueId");
    }

    private static KitsuneLightEntity findNearestTamedKitsune(ServerPlayer player) {
        return player.level().getEntitiesOfClass(KitsuneLightEntity.class,
                        player.getBoundingBox().inflate(16))
                .stream()
                .filter(k -> k.isTamed() && player.getUUID().equals(k.getOwnerUUID()))
                .min((a, b) -> Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)))
                .orElse(null);
    }

    @SubscribeEvent
    public static void onPlayerLogout(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            CHECK_COOLDOWN.remove(sp.getUUID());
            SHOWN_FOR_MASK.remove(sp.getUUID());
        }
    }
}