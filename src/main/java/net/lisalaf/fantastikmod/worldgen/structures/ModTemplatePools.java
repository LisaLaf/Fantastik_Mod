package net.lisalaf.fantastikmod.worldgen.structures;

import com.mojang.datafixers.util.Pair;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;

import static net.lisalaf.fantastikmod.worldgen.structures.ModStructures.TEA_HOUSE;

@SuppressWarnings("removal")
public class ModTemplatePools {
    public static final ResourceKey<StructureTemplatePool> TEA_HOUSE_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "tea_house/start")
    );

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        ResourceKey<StructureTemplatePool> emptyKey = ResourceKey.create(
                Registries.TEMPLATE_POOL,
                new ResourceLocation("empty")
        );


        context.register(TEA_HOUSE_START, new StructureTemplatePool(
                context.lookup(Registries.TEMPLATE_POOL).getOrThrow(emptyKey), java.util.List.of(
                        Pair.of(
                                SinglePoolElement.single(fantastikmod.MOD_ID + ":tea_house"), 1)),
                StructureTemplatePool.Projection.RIGID));



    }
}