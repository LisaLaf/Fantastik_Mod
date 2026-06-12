package net.lisalaf.fantastikmod.entity.phrases;

import net.minecraft.network.chat.Component;
import java.util.Random;

public class MoonDeerPhrases {
    private static final Random RANDOM = new Random();

    public static Component getWildFeedingMessage() {
        return Component.translatable("chat.fantastikmod.moon_deer.wild_feed." + RANDOM.nextInt(7));
    }

    public static Component getTamedHealingMessage() {
        return Component.translatable("chat.fantastikmod.moon_deer.tamed_heal." + RANDOM.nextInt(8));
    }

    public static Component getFullHealthMessage() {
        return Component.translatable("chat.fantastikmod.moon_deer.full_health." + RANDOM.nextInt(4));
    }

    public static Component getTamedMessage() {
        return Component.translatable("chat.fantastikmod.moon_deer.tamed." + RANDOM.nextInt(6));
    }

    public static Component getDeathMessage() {
        return Component.translatable("chat.fantastikmod.moon_deer.death." + RANDOM.nextInt(4));
    }

    public static Component getHurtWildMessage() {
        return Component.translatable("chat.fantastikmod.moon_deer.hurt_wild." + RANDOM.nextInt(8));
    }

    public static Component getHurtTamedMessage() {
        return Component.translatable("chat.fantastikmod.moon_deer.hurt_tamed." + RANDOM.nextInt(6));
    }

    public static Component getNamingMessage(String name) {
        return Component.translatable("chat.fantastikmod.moon_deer.naming." + RANDOM.nextInt(8), name);
    }
}