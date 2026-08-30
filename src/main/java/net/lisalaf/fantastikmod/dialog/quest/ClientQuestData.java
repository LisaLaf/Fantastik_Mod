package net.lisalaf.fantastikmod.dialog.quest;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("removal")
public class ClientQuestData {
    private static final Map<UUID, ClientQuestInfo> questData = new HashMap<>();

    public static void updateQuestData(UUID entityUUID, boolean hasQuest, boolean isActive,
                                       String questId, String nameKey, String descKey,
                                       String requiredItemId, int requiredAmount,
                                       String rewardItemId, int rewardAmount, long timeLeft) {
        if (hasQuest) {
            Item requiredItem = ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation(requiredItemId));
            Item rewardItem = ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation(rewardItemId));

            questData.put(entityUUID, new ClientQuestInfo(
                    isActive, questId, nameKey, descKey,
                    requiredItem, requiredAmount,
                    rewardItem, rewardAmount, timeLeft
            ));
        } else {
            questData.remove(entityUUID);
        }
    }

    public static ClientQuestInfo getQuestData(UUID entityUUID) {
        return questData.get(entityUUID);
    }

    public static void clearData() {
        questData.clear();
    }
    public static class ClientQuestInfo {
        public final boolean isActive;
        public final String questId;
        public final String nameKey;
        public final String descKey;
        public final Item requiredItem;
        public final int requiredAmount;
        public final Item rewardItem;
        public final int rewardAmount;
        public final long timeLeft;

        public ClientQuestInfo(boolean isActive, String questId, String nameKey, String descKey,
                               Item requiredItem, int requiredAmount,
                               Item rewardItem, int rewardAmount, long timeLeft) {
            this.isActive = isActive;
            this.questId = questId;
            this.nameKey = nameKey;
            this.descKey = descKey;
            this.requiredItem = requiredItem;
            this.requiredAmount = requiredAmount;
            this.rewardItem = rewardItem;
            this.rewardAmount = rewardAmount;
            this.timeLeft = timeLeft;
        }
    }
}