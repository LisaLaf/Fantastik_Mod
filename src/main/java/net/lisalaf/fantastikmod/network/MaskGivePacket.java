package net.lisalaf.fantastikmod.network;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.custom.KitsuneSoulMaskItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MaskGivePacket {

    public static final int ACTION_REFUSE = 0;
    public static final int ACTION_GIVE = 1;

    private final int kitsuneEntityId;
    private final int action;

    public MaskGivePacket(int kitsuneEntityId, int action) {
        this.kitsuneEntityId = kitsuneEntityId;
        this.action = action;
    }

    public static void encode(MaskGivePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.kitsuneEntityId);
        buf.writeInt(msg.action);
    }

    public static MaskGivePacket decode(FriendlyByteBuf buf) {
        return new MaskGivePacket(buf.readInt(), buf.readInt());
    }

    public static void handle(MaskGivePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Entity e = player.level().getEntity(msg.kitsuneEntityId);
            if (!(e instanceof KitsuneLightEntity kitsune)) return;

            if (msg.action == ACTION_REFUSE) {
                markMaskRefused(player);
                player.sendSystemMessage(Component.translatable("dialog.kitsune.mask.refuse_mask")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            } else if (msg.action == ACTION_GIVE) {
                if (takeMaskFromPlayer(player)) {
                    startKitsuneDisappear(player, kitsune);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void markMaskRefused(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.getItem() instanceof KitsuneSoulMaskItem) {
                s.getOrCreateTag().putBoolean("kitsuneRefused", true);
                return;
            }
        }
    }

    private static boolean takeMaskFromPlayer(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.getItem() instanceof KitsuneSoulMaskItem) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
                return true;
            }
        }
        return false;
    }

    private static void startKitsuneDisappear(ServerPlayer player, KitsuneLightEntity kitsune) {
        ServerLevel level = (ServerLevel) player.level();
        CompoundTag data = kitsune.getPersistentData();

        data.putLong("maskWaitUntil", level.getGameTime() + 6000L);
        data.putUUID("maskReturnPlayer", player.getUUID());
        data.putBoolean("maskDisappearing", true);

        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                kitsune.getX(), kitsune.getY() + 0.5, kitsune.getZ(),
                60, 0.4, 0.6, 0.4, 0.05);
        level.sendParticles(ParticleTypes.SMOKE,
                kitsune.getX(), kitsune.getY() + 0.5, kitsune.getZ(),
                40, 0.4, 0.6, 0.4, 0.02);
        level.playSound(null, kitsune.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0f, 1.2f);

        kitsune.addEffect(new MobEffectInstance(
                MobEffects.INVISIBILITY,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                false
        ));

        kitsune.setInvisible(true);
        kitsune.setNoAi(true);
        kitsune.getNavigation().stop();
        kitsune.setDeltaMovement(0, 0, 0);
        kitsune.setInvulnerable(true);
    }
}