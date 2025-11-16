package net.lisalaf.fantastikmod.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

@SuppressWarnings("removal")
public class ModOverworldRegion extends Region {

    public ModOverworldRegion() {
        super(new ResourceLocation(fantastikmod.MOD_ID, "overworld"), RegionType.OVERWORLD, 10); // weight = 10
    }

    public ModOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Просто добавляем наш биом без замены ванильных
        addBiomeSimilar(mapper, Biomes.FOREST, ModBiomes.BLUE_MOON_FOREST_BIOME);
    }
}