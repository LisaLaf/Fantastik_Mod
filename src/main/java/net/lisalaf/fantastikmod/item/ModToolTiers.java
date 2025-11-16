package net.lisalaf.fantastikmod.item;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.util.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

@SuppressWarnings("removal")
public class ModToolTiers {
    @SuppressWarnings("removal")
    public static final Tier AURIPIGMENT = TierSortingRegistry.registerTier(
            new ForgeTier(2, 280, 6.0f, 2.0f, 14, // Похоже на железо
                    ModTags.Blocks.NEEDS_AURIPIGMENT_TOOL, () -> Ingredient.of(ModItems.AURIPIGMENT.get())),
            new ResourceLocation(fantastikmod.MOD_ID, "auripigment"), List.of(Tiers.IRON), List.of());

    public static final ForgeTier SILVER = new ForgeTier(
            2,                      // Уровень (железо = 2)
            220,                    // Прочность (железо = 250) - чуть больше
            4.0f,                   // Скорость копания (железо = 6.0) - чуть быстрее
            2.0f,                   // Базовый урон (железо = 2.0) - повышен против нежити
            18,                     // Зачаровываемость
            ModTags.Blocks.NEEDS_SILVER_TOOL, // Тэг для блоков
            () -> Ingredient.of(ModItems.SILVER_INGOT.get()));

    public static final Tier GEM_MOON = TierSortingRegistry.registerTier(
            new ForgeTier(3, 1861, 8.0f, 4.0f, 15, // Уровень выше алмаза (3 вместо 3, но с лучшими параметрами)
                    ModTags.Blocks.NEEDS_GEM_MOON_TOOL, () -> Ingredient.of(ModItems.GEM_MOON.get())),
            new ResourceLocation(fantastikmod.MOD_ID, "gem_moon"),
            List.of(Tiers.DIAMOND),
            List.of(Tiers.NETHERITE));

}
