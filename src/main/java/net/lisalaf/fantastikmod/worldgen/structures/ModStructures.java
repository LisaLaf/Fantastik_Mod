package net.lisalaf.fantastikmod.worldgen.structures;

import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Map;

@SuppressWarnings("removal")
public class ModStructures {
    public static final ResourceKey<Structure> TEA_HOUSE = ResourceKey.create(Registries.STRUCTURE,
            new ResourceLocation(fantastikmod.MOD_ID, "tea_house"));

    public static final ResourceKey<StructureSet> TEA_HOUSE_SET = ResourceKey.create(Registries.STRUCTURE_SET,
            new ResourceLocation(fantastikmod.MOD_ID, "tea_house"));

    public static void bootstrap(BootstapContext<Structure> context) {
        context.register(TEA_HOUSE, new JigsawStructure(
                new Structure.StructureSettings(
                        context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                        Map.of(), // ← ДОБАВЬ java.util.Map.of()
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.BEARD_THIN // ← ИЗМЕНИ НА ЭТО
                ),
                context.lookup(Registries.TEMPLATE_POOL).getOrThrow(ModTemplatePools.TEA_HOUSE_START),
                7,
                ConstantHeight.of(VerticalAnchor.absolute(64)), // ← ИЗМЕНИ НА ЭТО
                true
        ));

    }

    public static void bootstrapSets(BootstapContext<StructureSet> context) {
        context.register(TEA_HOUSE_SET, new StructureSet(
                context.lookup(Registries.STRUCTURE).getOrThrow(TEA_HOUSE),
                new RandomSpreadStructurePlacement(
                        32, // расстояние между структурами
                        8,  // минимальное расстояние от спавна
                        RandomSpreadType.LINEAR,
                        123456789 // seed
                )
        ));
    }
}