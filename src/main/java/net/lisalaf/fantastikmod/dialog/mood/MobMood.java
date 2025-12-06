package net.lisalaf.fantastikmod.dialog.mood;

public interface MobMood {
    MoodSystem getMoodSystem();
    String getMobTypeName();
    boolean isTamed();
    String getOwnerName();
}