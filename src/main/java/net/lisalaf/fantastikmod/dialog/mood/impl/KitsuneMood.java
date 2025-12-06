package net.lisalaf.fantastikmod.dialog.mood.impl;

import net.lisalaf.fantastikmod.dialog.mood.MoodSystem;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class KitsuneMood extends MoodSystem {
    private long lastSitTime = 0;
    private int wanderingTime = 0;
    private long lastGiftTime = 0;
    private long lastUpdateTime = 0;
    private boolean wasSitting = false;

    @Override
    public void updateMood(Level level, LivingEntity entity) {
        if (!(entity instanceof KitsuneLightEntity kitsune)) return;

        long currentTime = level.getGameTime();

        // Обновляем не чаще чем раз в 20 тиков (1 секунда)
        if (currentTime - lastUpdateTime < 20) return;
        lastUpdateTime = currentTime;

        // Обработка сидения
        if (kitsune.isSitting()) {
            if (!wasSitting) {
                lastSitTime = currentTime;
                wasSitting = true;
            }

            // Каждые 30 секунд (600 тиков) в сидячем положении -1 к настроению
            if (currentTime - lastSitTime > 600) {
                addMood(-1);
                lastSitTime = currentTime;
            }
        } else {
            wasSitting = false;
            lastSitTime = 0;
        }

        // Обработка брожения
        boolean isMoving = kitsune.getDeltaMovement().horizontalDistanceSqr() > 0.001D;
        if (isMoving && !kitsune.isSitting() && !kitsune.isSleeping()) {
            wanderingTime++;
            // Каждые 30 секунд (600 тиков) брожения +2 к настроению
            if (wanderingTime >= 600) {
                addMood(2);
                wanderingTime = 0;
            }
        } else {
            wanderingTime = Math.max(0, wanderingTime - 1);
        }

        // Автоматическое восстановление настроения со временем
        if (currentTime % 1200 == 0) { // Каждую минуту
            if (moodValue < 0) {
                addMood(1); // Медленное восстановление
            }
        }
    }

    @Override
    public void addMood(int amount) {
        int oldValue = moodValue;
        super.addMood(amount);

        // Логирование для отладки
        if (Math.abs(amount) > 0) {
            System.out.println("[KitsuneMood] Changed from " + oldValue + " to " + moodValue + " (delta: " + amount + ")");
        }
    }

    @Override
    public void setMood(int value) {
        int oldValue = moodValue;
        super.setMood(value);
        System.out.println("[KitsuneMood] Set from " + oldValue + " to " + moodValue);
    }
}