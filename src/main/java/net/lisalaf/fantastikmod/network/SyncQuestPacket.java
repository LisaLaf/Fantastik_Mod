package net.lisalaf.fantastikmod.network;

import net.lisalaf.fantastikmod.dialog.quest.ClientQuestData;
import net.lisalaf.fantastikmod.dialog.quest.Quest;
import net.lisalaf.fantastikmod.dialog.quest.QuestSystem;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncQuestPacket {
    private final UUID entityUUID;
    private final boolean hasQuest;
    private final boolean isActive;
    private final String questId;
    private final String nameKey;
    private final String descKey;
    private final String requiredItemId;
    private final int requiredAmount;
    private final String rewardItemId;
    private final int rewardAmount;
    private final long timeLeft;

    public SyncQuestPacket(UUID entityUUID, Quest quest) {
        this.entityUUID = entityUUID;
        this.hasQuest = quest != null;
        if (quest != null) {
            this.isActive = quest.isActive();
            this.questId = quest.getQuestId();
            this.nameKey = quest.getName().getString();
            this.descKey = quest.getDescription().getString();
            this.requiredItemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(quest.getRequiredItem()).toString();
            this.requiredAmount = quest.getRequiredAmount();
            this.rewardItemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(quest.getRewardItem()).toString();
            this.rewardAmount = quest.getRewardAmount();
            this.timeLeft = quest.getTimeLeft();
        } else {
            this.isActive = false;
            this.questId = "";
            this.nameKey = "";
            this.descKey = "";
            this.requiredItemId = "";
            this.requiredAmount = 0;
            this.rewardItemId = "";
            this.rewardAmount = 0;
            this.timeLeft = 0;
        }
    }

    private SyncQuestPacket(UUID entityUUID, boolean hasQuest, boolean isActive, String questId,
                            String nameKey, String descKey, String requiredItemId, int requiredAmount,
                            String rewardItemId, int rewardAmount, long timeLeft) {
        this.entityUUID = entityUUID;
        this.hasQuest = hasQuest;
        this.isActive = isActive;
        this.questId = questId;
        this.nameKey = nameKey;
        this.descKey = descKey;
        this.requiredItemId = requiredItemId;
        this.requiredAmount = requiredAmount;
        this.rewardItemId = rewardItemId;
        this.rewardAmount = rewardAmount;
        this.timeLeft = timeLeft;
    }

    public static void encode(SyncQuestPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.entityUUID);
        buf.writeBoolean(msg.hasQuest);
        if (msg.hasQuest) {
            buf.writeBoolean(msg.isActive);
            buf.writeUtf(msg.questId);
            buf.writeUtf(msg.nameKey);
            buf.writeUtf(msg.descKey);
            buf.writeUtf(msg.requiredItemId);
            buf.writeInt(msg.requiredAmount);
            buf.writeUtf(msg.rewardItemId);
            buf.writeInt(msg.rewardAmount);
            buf.writeLong(msg.timeLeft);
        }
    }

    public static SyncQuestPacket decode(FriendlyByteBuf buf) {
        UUID entityUUID = buf.readUUID();
        boolean hasQuest = buf.readBoolean();
        if (hasQuest) {
            return new SyncQuestPacket(entityUUID, true, buf.readBoolean(), buf.readUtf(),
                    buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readInt(),
                    buf.readUtf(), buf.readInt(), buf.readLong());
        }
        return new SyncQuestPacket(entityUUID, false, false, "", "", "", "", 0, "", 0, 0);
    }

    public static void handle(SyncQuestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                ClientQuestData.updateQuestData(
                        msg.entityUUID, msg.hasQuest, msg.isActive,
                        msg.questId, msg.nameKey, msg.descKey,
                        msg.requiredItemId, msg.requiredAmount,
                        msg.rewardItemId, msg.rewardAmount, msg.timeLeft
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public UUID getEntityUUID() { return entityUUID; }
    public boolean hasQuest() { return hasQuest; }
    public boolean isActive() { return isActive; }
    public String getQuestId() { return questId; }
    public String getNameKey() { return nameKey; }
    public String getDescKey() { return descKey; }
    public String getRequiredItemId() { return requiredItemId; }
    public int getRequiredAmount() { return requiredAmount; }
    public String getRewardItemId() { return rewardItemId; }
    public int getRewardAmount() { return rewardAmount; }
    public long getTimeLeft() { return timeLeft; }
}