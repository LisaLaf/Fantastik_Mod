package net.lisalaf.fantastikmod.entity.phrases;

import net.minecraft.network.chat.Component;
import java.util.Random;

public class BakenekoPhrases {
    private static final Random RANDOM = new Random();

    public static Component getHurtMessage() {
        return Component.translatable("chat.fantastikmod.bakeneko.hurt." + RANDOM.nextInt(9));
    }

    public static Component getFeedMessage() {
        return Component.translatable("chat.fantastikmod.bakeneko.feed." + RANDOM.nextInt(4));
    }

    public static Component getStealFromPlayerMessage() {
        return Component.translatable("chat.fantastikmod.bakeneko.steal_from_player." + RANDOM.nextInt(7));
    }

    public static Component getStolenFromBakenekoMessage() {
        return Component.translatable("chat.fantastikmod.bakeneko.stolen_from_bakeneko." + RANDOM.nextInt(9));
    }
}