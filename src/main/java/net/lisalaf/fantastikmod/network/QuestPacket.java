package net.lisalaf.fantastikmod.network;

import net.lisalaf.fantastikmod.dialog.quest.Quest;
import net.lisalaf.fantastikmod.dialog.quest.QuestSystem;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class QuestPacket {

    private final int entityId;
    private final int action; // 0=start, 1=complete, 2=cancel

    public QuestPacket(int entityId, int action) {
        this.entityId = entityId;
        this.action = action;
    }

    public static void encode(QuestPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.action);
    }

    public static QuestPacket decode(FriendlyByteBuf buf) {
        return new QuestPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(QuestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Entity entity = player.level().getEntity(msg.entityId);
            if (entity == null) return;

            switch (msg.action) {
                case 0 -> {
                    Quest quest = null;
                    if (entity instanceof KitsuneLightEntity kitsune) {
                        quest = QuestSystem.getQuestForKitsune(kitsune);
                        if (quest == null) {
                            quest = QuestSystem.createNewQuest(kitsune);
                        } else if (!quest.isActive()) {
                            QuestSystem.startQuest(kitsune, player);
                        }
                    } else if (entity instanceof MoonDeerEntity moonDeer) {
                        quest = QuestSystem.getQuestForMoonDeer(moonDeer);
                        if (quest == null) {
                            quest = QuestSystem.createNewQuest(moonDeer);
                        } else if (!quest.isActive()) {
                            QuestSystem.startQuest(moonDeer, player);
                        }
                    }
                    if (quest != null || QuestSystem.getQuest(entity.getUUID()) != null) {
                        Quest syncQuest = QuestSystem.getQuest(entity.getUUID());
                        fantastikmod.sendToPlayer(new SyncQuestPacket(entity.getUUID(), syncQuest), player);
                    }
                }
                case 1 -> {
                    if (entity instanceof KitsuneLightEntity kitsune) {
                        QuestSystem.completeQuest(kitsune, player);
                    } else if (entity instanceof MoonDeerEntity moonDeer) {
                        QuestSystem.completeQuest(moonDeer, player);
                    }
                    fantastikmod.sendToPlayer(new SyncQuestPacket(entity.getUUID(), null), player);
                }
                case 2 -> {
                    QuestSystem.cancelQuest(entity);
                    fantastikmod.sendToPlayer(new SyncQuestPacket(entity.getUUID(), null), player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}