package net.lisalaf.fantastikmod.dialog.quest;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import java.util.*;

public class QuestSystem {
    private static final Map<UUID, Quest> activeQuests = new HashMap<>();
    private static final Map<UUID, Long> lastQuestTimes = new HashMap<>();

    private static final Map<UUID, UUID> questPlayerMap = new HashMap<>(); // entityUUID -> playerUUID

    public static Map<UUID, Quest> getActiveQuests() {
        return Collections.unmodifiableMap(activeQuests);
    }

    public static boolean canGiveNewQuest(UUID entityUUID) {
        if (activeQuests.containsKey(entityUUID)) {
            Quest quest = activeQuests.get(entityUUID);
            if (quest.isActive() && !quest.isCompleted()) {
                return false;
            }
        }

        long lastTime = lastQuestTimes.getOrDefault(entityUUID, 0L);
        long currentTime = System.currentTimeMillis();
        long cooldown = 60 * 1000; // 60 секунд

        return (currentTime - lastTime) > cooldown;
    }

    public static Quest createNewQuest(KitsuneLightEntity kitsune) {
        if (!canGiveNewQuest(kitsune.getUUID())) {
            return null;
        }

        boolean isTamed = kitsune.isTamed();
        KitsuneQuest quest = new KitsuneQuest(kitsune, isTamed);

        if (quest.canStart()) {
            activeQuests.put(kitsune.getUUID(), quest);
            lastQuestTimes.put(kitsune.getUUID(), System.currentTimeMillis());
            return quest;
        }

        return null;
    }

    public static Quest createNewQuest(MoonDeerEntity moonDeer) {
        if (!canGiveNewQuest(moonDeer.getUUID())) {
            return null;
        }

        boolean isTamed = moonDeer.isTamed();
        MoonDeerQuest quest = new MoonDeerQuest(moonDeer, isTamed);

        if (quest.canStart()) {
            activeQuests.put(moonDeer.getUUID(), quest);
            lastQuestTimes.put(moonDeer.getUUID(), System.currentTimeMillis());
            return quest;
        }

        return null;
    }

    public static boolean startQuest(Entity entity, ServerPlayer player) {
        UUID entityUUID = entity.getUUID();
        Quest quest = activeQuests.get(entityUUID);

        if (quest != null && !quest.isActive()) {
            quest.start();
            questPlayerMap.put(entityUUID, player.getUUID());
            return true;
        }
        return false;
    }

    public static boolean completeQuest(Entity entity, ServerPlayer player) {
        UUID entityUUID = entity.getUUID();
        Quest quest = activeQuests.get(entityUUID);

        if (quest != null && quest.isActive() && quest.canComplete()) {
            quest.complete();
            activeQuests.remove(entityUUID);
            questPlayerMap.remove(entityUUID);
            return true;
        }
        return false;
    }

    public static void cancelQuest(Entity entity) {
        UUID entityUUID = entity.getUUID();
        Quest quest = activeQuests.get(entityUUID);

        if (quest != null) {
            quest.fail();
            activeQuests.remove(entityUUID);
            questPlayerMap.remove(entityUUID);
        }
    }

    public static Quest getQuest(UUID entityUUID) {
        return activeQuests.get(entityUUID);
    }

    public static Quest getQuestForKitsune(KitsuneLightEntity kitsune) {
        return getQuest(kitsune.getUUID());
    }

    public static Quest getQuestForMoonDeer(MoonDeerEntity moonDeer) {
        return getQuest(moonDeer.getUUID());
    }

    public static boolean canGiveNewQuest(KitsuneLightEntity kitsune) {
        return canGiveNewQuest(kitsune.getUUID());
    }

    public static boolean canGiveNewQuest(MoonDeerEntity moonDeer) {
        return canGiveNewQuest(moonDeer.getUUID());
    }

    public static void cancelQuestForEntity(Entity entity) {
        cancelQuest(entity);
    }
}