package net.lisalaf.fantastikmod.entity.phrases;

import net.minecraft.network.chat.Component;
import java.util.Random;

public class KitsunePhrases {
    private static final Random RANDOM = new Random();

    public static Component getFeedMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.feed." + RANDOM.nextInt(7));
    }

    public static Component getTamedMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.tamed." + RANDOM.nextInt(4));
    }

    public static Component getDeathMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.death." + RANDOM.nextInt(4));
    }

    public static Component getHurtMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.hurt." + RANDOM.nextInt(7));
    }

    public static Component getWakeUpMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.wakeup." + RANDOM.nextInt(8));
    }

    public static Component getAngryMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.angry." + RANDOM.nextInt(7));
    }

    public static Component getOwnerHurtMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.owner_hurt." + RANDOM.nextInt(5));
    }

    public static Component getAngryTofuMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.angry_tofu." + RANDOM.nextInt(5));
    }

    public static Component getForgivenessMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.forgive." + RANDOM.nextInt(6));
    }

    public static Component getNameTagRejectionTamed() {
        return Component.translatable("chat.fantastikmod.kitsune.name_reject_tamed." + RANDOM.nextInt(6));
    }

    public static Component getNameTagRejectionWild() {
        return Component.translatable("chat.fantastikmod.kitsune.name_reject_wild." + RANDOM.nextInt(3));
    }

    public static Component getIdleMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.idle." + RANDOM.nextInt(6));
    }

    public static Component getLaughMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.laugh." + RANDOM.nextInt(4));
    }

    public static Component getSleepMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.sleep." + RANDOM.nextInt(3));
    }
    public static Component getOwnerKillMessage() {
        return Component.translatable("chat.fantastikmod.kitsune.owner_kill." + RANDOM.nextInt(2));
    }
}