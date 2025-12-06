package net.lisalaf.fantastikmod.dialog.quest;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import java.util.*;

public class QuestSystem {
    private static final Map<UUID, Quest> activeQuests = new HashMap<>();
    private static final Map<UUID, Long> lastQuestTimes = new HashMap<>();

    // === ОБЩИЕ МЕТОДЫ ===
    public static Map<UUID, Quest> getActiveQuests() {
        return Collections.unmodifiableMap(activeQuests);
    }

    private static boolean canGiveNewQuest(UUID uuid) {
        if (activeQuests.containsKey(uuid)) {
            Quest quest = activeQuests.get(uuid);
            if (quest.isActive() && !quest.isCompleted()) {
                return false;
            }
        }

        long lastTime = lastQuestTimes.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();
        long cooldown = 60 * 1000; // 60 секунд

        return (currentTime - lastTime) > cooldown;
    }

    private static boolean completeQuest(UUID entityId, Player player) {
        Quest quest = activeQuests.get(entityId);
        if (quest != null && quest.isActive() && quest.canComplete()) {
            quest.complete();
            activeQuests.remove(entityId);
            return true;
        }
        return false;
    }

    private static void cancelQuest(UUID entityId) {
        Quest quest = activeQuests.get(entityId);
        if (quest != null) {
            quest.fail();
            activeQuests.remove(entityId);
        }
    }

    // === МЕТОДЫ ДЛЯ КИЦУНЭ ===
    public static Quest getQuestForKitsune(KitsuneLightEntity kitsune) {
        return activeQuests.get(kitsune.getUUID());
    }

    public static boolean canGiveNewQuest(KitsuneLightEntity kitsune) {
        return canGiveNewQuest(kitsune.getUUID());
    }

    public static long getNextQuestTime(KitsuneLightEntity kitsune) {
        UUID uuid = kitsune.getUUID();

        if (activeQuests.containsKey(uuid)) {
            Quest quest = activeQuests.get(uuid);
            if (quest.isActive() && !quest.isCompleted()) {
                return 0;
            }
        }

        long lastTime = lastQuestTimes.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();
        long minCooldown = 300 * 1000;
        long maxCooldown = 1500 * 1000;

        long cooldown = minCooldown + (long)(Math.random() * (maxCooldown - minCooldown));
        long timePassed = currentTime - lastTime;

        if (timePassed > cooldown) {
            return 0;
        }

        return cooldown - timePassed;
    }

    public static Quest createNewQuest(KitsuneLightEntity kitsune) {
        if (!canGiveNewQuest(kitsune)) {
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

    public static boolean startQuest(KitsuneLightEntity kitsune) {
        Quest quest = getQuestForKitsune(kitsune);
        if (quest != null && !quest.isActive()) {
            quest.start();
            return true;
        }
        return false;
    }

    public static boolean completeQuest(KitsuneLightEntity kitsune, Player player) {
        return completeQuest(kitsune.getUUID(), player);
    }

    public static void cancelQuest(KitsuneLightEntity kitsune) {
        cancelQuest(kitsune.getUUID());
    }

    public static void checkQuestTimers(KitsuneLightEntity kitsune) {
        Quest quest = getQuestForKitsune(kitsune);
        if (quest != null && quest.isActive() && quest.getTimeLeft() <= 0) {
            quest.fail();
            activeQuests.remove(kitsune.getUUID());
        }
    }

    // === МЕТОДЫ ДЛЯ ЛУННОГО ОЛЕНЯ ===
    public static Quest getQuestForMoonDeer(MoonDeerEntity moonDeer) {
        return activeQuests.get(moonDeer.getUUID());
    }

    public static boolean canGiveNewQuest(MoonDeerEntity moonDeer) {
        return canGiveNewQuest(moonDeer.getUUID());
    }

    public static Quest createNewQuest(MoonDeerEntity moonDeer) {
        if (!canGiveNewQuest(moonDeer)) {
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

    public static boolean completeQuest(MoonDeerEntity moonDeer, Player player) {
        return completeQuest(moonDeer.getUUID(), player);
    }

    public static void cancelQuest(MoonDeerEntity moonDeer) {
        cancelQuest(moonDeer.getUUID());
    }

    public static void updateQuestProgress(Player player) {
        for (Quest quest : activeQuests.values()) {
            if (quest.isActive() && player.level() != null) {
                quest.updateProgress(player, player.level());
            }
        }
    }

    public static void cancelQuestForEntity(Entity entity) {
        if (entity instanceof KitsuneLightEntity kitsune) {
            cancelQuest(kitsune);
        } else if (entity instanceof MoonDeerEntity moonDeer) {
            cancelQuest(moonDeer);
        }
    }
}