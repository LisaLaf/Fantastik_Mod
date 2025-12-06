package net.lisalaf.fantastikmod.dialog.mood;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class MoodSystem {
    protected int moodValue = 0;
    protected final int MAX_MOOD = 100;
    protected final int MIN_MOOD = -100;

    public abstract void updateMood(Level level, LivingEntity entity);

    public int getMood() {
        return moodValue;
    }

    public void addMood(int amount) {
        moodValue = Math.min(MAX_MOOD, Math.max(MIN_MOOD, moodValue + amount));
    }

    public void setMood(int value) {
        moodValue = Math.min(MAX_MOOD, Math.max(MIN_MOOD, value));
    }

    public float getMoodPercentage() {
        return (float)(moodValue - MIN_MOOD) / (MAX_MOOD - MIN_MOOD);
    }

    public String getMoodTextKey() {
        if (moodValue >= 80) return "mood.excellent";
        if (moodValue >= 50) return "mood.good";
        if (moodValue >= 0) return "mood.neutral";
        if (moodValue >= -10) return "mood.slightly_bad";
        if (moodValue >= -40) return "mood.bad";
        if (moodValue >= -80) return "mood.angry";
        return "mood.hostile";
    }

    public int getMoodColor() {
        if (moodValue >= 60) return 0xFF4CAF50;
        if (moodValue >= 30) return 0xFFFFC107;
        if (moodValue >= 0) return 0xFFFF9800;
        if (moodValue >= -30) return 0xFFF44336;
        if (moodValue >= -60) return 0xFF9C27B0;
        return 0xFF673AB7;
    }
}