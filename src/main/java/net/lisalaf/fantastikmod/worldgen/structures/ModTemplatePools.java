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

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle_plains/start")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_WALLS_RIGHT = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/walls/right")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_WALLS_LEFT = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/walls/left")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_TOWERS_RIGHT = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/towers/right")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_TOWERS_LEFT = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/towers/left")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_GREAT_HALL = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/rooms")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_HALLWAY_MAIN = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/hallway")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_HALLWAY_SERVANT_1 = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/hallway/servant_1")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_KITCHEN = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/rooms/kitchen")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_TOWER_GUARD_ROOM_RIGHT = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/towers/right/guard_room")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_TOWER_GUARD_ROOM_LEFT = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/towers/left/guard_room")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_SERVANTS_ROOM = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/rooms/servants")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_LAUNDRY = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/rooms/laundry")
    );
    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_HALLWAY_SERVANT_2 = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/hallway/servant_2")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_DINING = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/rooms/dining")
    );
    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_THRONE_ROOM = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/rooms/throne_room")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_PLAINS_BEDROOM = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle/plains/rooms/bedroom")
    );

    public static final ResourceKey<StructureTemplatePool> CASTLE_TEST_START = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            new ResourceLocation(fantastikmod.MOD_ID, "castle_test/start")
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

        context.register(CASTLE_PLAINS_START, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                                SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":castle/plains/main_gate",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_WALLS_RIGHT, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(
                                SinglePoolElement.single(
                                        fantastikmod.MOD_ID + ":castle/plains/walls/right/wall_straight",
                                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))
                                ),
                                1
                        )
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_WALLS_LEFT, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(
                                fantastikmod.MOD_ID + ":castle/plains/walls/left/straight",
                                processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_TOWERS_RIGHT, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/towers/right/base",
                                processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_TOWERS_LEFT, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(fantastikmod.MOD_ID + ":castle/plains/towers/left/base",
                                processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));
        context.register(CASTLE_PLAINS_GREAT_HALL, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(
                                fantastikmod.MOD_ID + ":castle/plains/rooms/great_hall",
                                processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_HALLWAY_MAIN, new StructureTemplatePool(
                emptyPool,
                List.of(
                        Pair.of(SinglePoolElement.single(
                                fantastikmod.MOD_ID + ":castle/plains/hallway/main",
                                processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_HALLWAY_SERVANT_1, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/hallway/servant_1/servant_1",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_KITCHEN, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/rooms/kitchen/kitchen",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_TOWER_GUARD_ROOM_LEFT, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/towers/left/guard_room/guard_room",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));
        context.register(CASTLE_PLAINS_TOWER_GUARD_ROOM_RIGHT, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/towers/right/guard_room/guard_room",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_SERVANTS_ROOM, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/rooms/servants/servants",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_LAUNDRY, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/rooms/laundry/laundry",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_THRONE_ROOM, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/rooms/throne_room/throne_room",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_HALLWAY_SERVANT_2, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/hallway/servant_2/servant_2",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_DINING, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/rooms/dining/dining",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_PLAINS_BEDROOM, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle/plains/rooms/bedroom/bedroom",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
                StructureTemplatePool.Projection.RIGID
        ));

        context.register(CASTLE_TEST_START, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(SinglePoolElement.single(
                        fantastikmod.MOD_ID + ":castle_test",
                        processorListRegistry.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("empty")))), 1)),
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