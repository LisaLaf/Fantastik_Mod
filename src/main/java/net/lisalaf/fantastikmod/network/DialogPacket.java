package net.lisalaf.fantastikmod.network;

import net.lisalaf.fantastikmod.dialog.mobs.BakenekoDialog;
import net.lisalaf.fantastikmod.entity.custom.BakenekoEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DialogPacket {

    private final int entityId;
    private final int optionIndex;
    private final String dialogType;

    public DialogPacket(int entityId, int optionIndex, String dialogType) {
        this.entityId = entityId;
        this.optionIndex = optionIndex;
        this.dialogType = dialogType;
    }

    public static void encode(DialogPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.optionIndex);
        buf.writeUtf(msg.dialogType);
    }

    public static DialogPacket decode(FriendlyByteBuf buf) {
        return new DialogPacket(buf.readInt(), buf.readInt(), buf.readUtf());
    }

    public static void handle(DialogPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Entity entity = player.level().getEntity(msg.entityId);

            if ("bakeneko".equals(msg.dialogType) && entity instanceof BakenekoEntity bakeneko) {
                int optionIndex = msg.optionIndex;

                if (optionIndex == 0) {
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (bakeneko.isFood(stack) && !stack.isEmpty()) {
                            stack.shrink(1);
                            bakeneko.heal(4.0F);
                            player.sendSystemMessage(Component.translatable("chat.bakeneko.feed"));
                            bakeneko.playSound(SoundEvents.GENERIC_EAT, 0.5F, 1.0F);
                            break;
                        }
                    }
                }
                if (optionIndex == 1) {
                    player.hurt(player.damageSources().mobAttack(bakeneko), 3.0F);
                    player.sendSystemMessage(Component.translatable("chat.fantastikmod.bakeneko.hurt.0"));
                    bakeneko.playSound(SoundEvents.CAT_HISS, 1.0F, 0.5F);
                }
                if (optionIndex == 2) {
                    if (bakeneko.getLastThief() == player) {
                        ItemStack stolenItem = bakeneko.getHeldItem();
                        if (!stolenItem.isEmpty()) {
                            if (!player.getInventory().add(stolenItem.copy())) {
                                player.drop(stolenItem.copy(), false);
                            }
                            bakeneko.setHeldItem(ItemStack.EMPTY);
                            bakeneko.setHasItem(false);
                            bakeneko.setLastThief(null);
                            player.sendSystemMessage(Component.translatable("chat.fantastikmod.bakeneko.stolen_from_bakeneko.0"));
                        }
                    }
                }
                if (optionIndex == 3) {
                    bakeneko.setAngry(true);
                    bakeneko.setLastThief(player);
                    player.sendSystemMessage(Component.translatable("chat.bakeneko.angry"));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}