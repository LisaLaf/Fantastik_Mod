package net.lisalaf.fantastikmod.dialog.quest;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.concurrent.TimeUnit;

public abstract class Quest {
    protected final String questId;
    protected final String nameKey;
    protected final String descriptionKey;
    protected final Item requiredItem;
    protected final Item rewardItem;
    protected final int requiredAmount;
    protected final int rewardAmount;
    protected final long durationMinutes; // Длительность квеста в минутах
    protected boolean isActive = false;
    protected boolean isCompleted = false;
    protected long startTime = 0;
    protected int currentCount = 0;

    public Quest(String questId, String nameKey, String descriptionKey,
                 Item requiredItem, int requiredAmount,
                 Item rewardItem, int rewardAmount,
                 long durationMinutes) {
        this.questId = questId;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.requiredItem = requiredItem;
        this.requiredAmount = requiredAmount;
        this.rewardItem = rewardItem;
        this.rewardAmount = rewardAmount;
        this.durationMinutes = durationMinutes;
    }

    public abstract boolean canStart(); // Условия начала квеста
    public abstract void onStart();
    public abstract void onComplete();
    public abstract void onFail();

    // Геттеры
    public String getQuestId() { return questId; }
    protected abstract void updateCustomProgress(Player player, Level level);
    public boolean isActive() { return isActive; }
    public boolean isCompleted() { return isCompleted; }
    public Item getRequiredItem() { return requiredItem; }
    public Item getRewardItem() { return rewardItem; }
    public int getRequiredAmount() { return requiredAmount; }
    public int getRewardAmount() { return rewardAmount; }
    public long getTimeLeft() {
        if (!isActive || startTime == 0) return 0;
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.max(0, durationMinutes - elapsed);
    }

    public Component getName() {
        return Component.translatable(nameKey);
    }

    public Component getDescription() {
        return Component.translatable(descriptionKey);
    }

    public void start() {
        if (canStart()) {
            isActive = true;
            startTime = System.currentTimeMillis();
            onStart();
        }
    }


    public void complete() {

        if (isActive && !isCompleted) {
            isActive = false;
            isCompleted = true;
            onComplete();
        } else {
        }

    }

    public void fail() {
        if (isActive) {
            isActive = false;
            onFail();
        }
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getProgressPercentage() {
        if (requiredAmount <= 0) return 100;
        return Math.min(100, (currentCount * 100) / requiredAmount);
    }

    public boolean canComplete() {
        return isActive();

       // return isActive() && getCurrentCount() >= getRequiredAmount();
    }

    public void updateProgress(Player player, Level level) {
        if (!isActive) return;

        currentCount = player.getInventory().countItem(requiredItem);

        updateCustomProgress(player, level);
    }
}