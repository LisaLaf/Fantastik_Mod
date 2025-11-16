package net.lisalaf.fantastikmod.worldgen.biome.sunface;

import net.lisalaf.fantastikmod.worldgen.biome.ModBiomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModSurfaceRules {

    public static SurfaceRules.RuleSource makeRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.BLUE_MOON_FOREST_BIOME),
                        SurfaceRules.sequence(
                                // Более простые правила - только верхний слой
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
                                                        SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState())),
                                                SurfaceRules.state(Blocks.DIRT.defaultBlockState())
                                        )
                                ),
                                // Подповерхностный слой
                                SurfaceRules.ifTrue(SurfaceRules.ON_CEILING,
                                        SurfaceRules.state(Blocks.STONE.defaultBlockState())
                                )
                        )
                )
        );
    }
}