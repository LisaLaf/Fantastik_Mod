package net.lisalaf.fantastikmod.worldgen.structures;

import com.mojang.datafixers.util.Pair;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;

@SuppressWarnings("removal")
public class ModTemplatePools {
    public static final ResourceKey<StructureTemplatePool> TEA_HOUSE_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "tea_house/start")
    );

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        // Получаем регистры
        HolderGetter<StructureTemplatePool> templatePoolRegistry = context.lookup(Registries.TEMPLATE_POOL);
        // 🔑 Ключевое исправление: получаем регистр для списков процессоров
        HolderGetter<StructureProcessorList> processorListRegistry = context.lookup(Registries.PROCESSOR_LIST);

        // Получаем пустой пул
        Holder<StructureTemplatePool> emptyPool = templatePoolRegistry.getOrThrow(
                ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("empty"))
        );

        context.register(TEA_HOUSE_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                              SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":tea_house/tea_house",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));
    }
}