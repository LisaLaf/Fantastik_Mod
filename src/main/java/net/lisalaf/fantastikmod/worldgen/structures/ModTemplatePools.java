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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;

@SuppressWarnings("removal")
public class ModTemplatePools {
    public static final ResourceKey<StructureTemplatePool> TEA_HOUSE_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "tea_house/start")
    );

    public static final ResourceKey<StructureTemplatePool> MOON_RUIN_HOUSE_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "moon_ruin_house/start")
    );

    public static final ResourceKey<StructureTemplatePool> TOWN_CENTER_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "town_center/start")
    );

    public static final ResourceKey<StructureTemplatePool> MOON_RUIN_CHURCH_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "moon_ruin_church/start")
    );

    public static final ResourceKey<StructureTemplatePool> INARI_SMALL_SHRINE_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "inari_small_shrine/start")
    );

    public static final ResourceKey<StructureTemplatePool> ONSEN_SMALL_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "onsen_small/start")
    );

    public static final ResourceKey<StructureTemplatePool> INARI_SHRINE_VILLAGE_PLAINS = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "inari_shrine_village_plains")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_0_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle_plains_0/start")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_CATACOMBS_ROOMS = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/catacombs/rooms")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_CATACOMBS_HALLWAYS = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/catacombs/hallways")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_CATACOMBS_TREASURY = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/catacombs/treasury")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_CATACOMBS_STAIRS = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/catacombs/stairs")
    );

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> templatePoolRegistry = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<StructureProcessorList> processorListRegistry = context.lookup(Registries.PROCESSOR_LIST);

        Holder<StructureTemplatePool> emptyPool = templatePoolRegistry.getOrThrow(
                ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("empty"))
        );

        context.register(TEA_HOUSE_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                                SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":tea_house",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST,new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(MOON_RUIN_HOUSE_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                                SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":moon_ruin_house",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(TOWN_CENTER_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                                SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":town_center",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(MOON_RUIN_CHURCH_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                                SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":moon_ruin_church",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(INARI_SMALL_SHRINE_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                                SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":inari_small_shrine",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(ONSEN_SMALL_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                                SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":onsen_small",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(INARI_SHRINE_VILLAGE_PLAINS, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(
                                fantastikmod.MOD_ID + ":inari_shrine_village_plains_0",
                                processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1),
                        Pair.of(SinglePoolElement.single(
                                fantastikmod.MOD_ID + ":inari_shrine_village_plains_1",
                                processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 2)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_0_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(
                                fantastikmod.MOD_ID + ":castle/plains/castle_plains_0",
                                processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_CATACOMBS_ROOMS, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/rooms/room_1", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1),
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/rooms/room_2", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1),
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/rooms/room_3", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1),
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/rooms/room_4", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1),
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/rooms/room_5", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_CATACOMBS_HALLWAYS, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/hallways/hallway_1", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1),
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/hallways/hallway_2", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1),
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/hallways/hallway_3", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_CATACOMBS_TREASURY, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/treasury/treasury", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_CATACOMBS_STAIRS, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/catacombs/stairs/catacombs_stairs", processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));
    }
}