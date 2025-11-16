package net.lisalaf.fantastikmod.worldgen;

import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

@SuppressWarnings("removal")
public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> SILVER_ORE_PLACED_KEI =registerKey("silver_ore_placed");
    public static final ResourceKey<PlacedFeature> AURIPIGMENT_ORE_PLACED_KEI =registerKey("auripigment_ore_placed");
    public static final ResourceKey<PlacedFeature> MOON_ORE_PLACED_KEY = registerKey("moon_ore_placed");

    public static final ResourceKey<PlacedFeature> MOON_TREE_PLACED_KEY = registerKey("moon_tree_placed");

    public static final ResourceKey<PlacedFeature> MOON_LILY_PLACED_KEY = registerKey("moon_lily_placed");
    public static final ResourceKey<PlacedFeature> MOON_HEATHER_PLACED_KEY = registerKey("moon_heather_placed");
    public static final ResourceKey<PlacedFeature> MOON_NORTHERN_BLUEBELL_PLACED_KEY = registerKey("moon_northern_bluebell_placed");
    public static final ResourceKey<PlacedFeature> MOON_GRASS_PLACED_KEY = registerKey("moon_grass_placed");
    public static final ResourceKey<PlacedFeature> MOON_GRASS_1_PLACED_KEY = registerKey("moon_grass_1_placed");

    public static final ResourceKey<PlacedFeature> MOON_VINE_UNDER_TREES = registerKey("moon_vine_under_trees");

    public static final ResourceKey<PlacedFeature> DRAGON_CAVE_PLACED_KEY = registerKey("dragon_cave_placed");


    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, SILVER_ORE_PLACED_KEI, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_SILVER_ORE_KEY),
                List.of(
                        CountPlacement.of(6), // 6 жил на чанк (железо - 9, уголь - 20)
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(
                                VerticalAnchor.absolute(-16), // Не появляется в глубоком слайме
                                VerticalAnchor.absolute(88)   // Пик на уровне моря
                        ),
                        BiomeFilter.biome()
                ));

        register(context, AURIPIGMENT_ORE_PLACED_KEI, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_AURIPIGMENT_ORE_KEY),
                List.of(
                        CountPlacement.of(6), // 6 жил на чанк (железо - 9, уголь - 20)
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(
                                VerticalAnchor.absolute(-10), // Не появляется в глубоком слайме
                                VerticalAnchor.absolute(88)   // Пик на уровне моря
                        ),
                        BiomeFilter.biome()
                ));

        register(context, MOON_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOON_ORE_KEY),
                List.of(
                        CountPlacement.of(2), // 1 жила на чанк
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(20),  // От высоты 50
                                VerticalAnchor.absolute(256)  // До верха мира
                        ),
                        BiomeFilter.biome()
                ));

        register(context, MOON_TREE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOON_KEY),
                List.of(
                        PlacementUtils.countExtra(10, 0.2f, 6), // 1 дерево + случайное доп
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        PlacementUtils.filteredByBlockSurvival(ModBlocks.MOON_SAPLING.get()), // или ваш саженец
                        BiomeFilter.biome()
                ));

        register(context, MOON_LILY_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOON_LILY_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(3), // Частота спавна (меньше = чаще)
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, MOON_NORTHERN_BLUEBELL_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOON_NORTHERN_BLUEBELL_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(1), // Частота спавна (меньше = чаще)
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, MOON_HEATHER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOON_HEATHER_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(2), // Частота спавна (меньше = чаще)
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, MOON_GRASS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOON_GRASS_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(1), // Частота спавна (меньше = чаще)
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, MOON_GRASS_1_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOON_GRASS_1_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(1), // Частота спавна (меньше = чаще)
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));


        register(context, MOON_VINE_UNDER_TREES,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.MOON_VINE_KEY),
                List.of(
                        CountPlacement.of(8), // Количество попыток спавна
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        EnvironmentScanPlacement.scanningFor(
                                Direction.UP,
                                BlockPredicate.matchesBlocks(ModBlocks.TREE_MOON_FOLIAGE_BLOCK.get()), // Ищем под листвой
                                BlockPredicate.ONLY_IN_AIR_PREDICATE, // Ставим только в воздух
                                12 // Максимальная дистанция поиска
                        ),
                        BiomeFilter.biome()
                ));

        //register(context, DRAGON_CAVE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.DRAGON_CAVE_KEY), List.of(
                       // RarityFilter.onAverageOnceEvery(1000),
                       // InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(20), VerticalAnchor.absolute(60)), BiomeFilter.biome() ));



    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(fantastikmod.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

}
