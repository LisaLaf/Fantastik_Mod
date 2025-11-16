package net.lisalaf.fantastikmod.worldgen;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.worldgen.feature.DragonCaveFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, fantastikmod.MOD_ID);

   // public static final RegistryObject<Feature<NoneFeatureConfiguration>> DRAGON_CAVE = FEATURES.register("dragon_cave", () -> new DragonCaveFeature(NoneFeatureConfiguration.CODEC));
}