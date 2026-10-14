package net.lisalaf.fantastikmod.entity;


import net.lisalaf.fantastikmod.entity.custom.*;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, fantastikmod.MOD_ID);

    public static final RegistryObject<EntityType<KitsuneLightEntity>> KITSUNE_LIGHT =
            ENTITY_TYPES.register("kitsune_light", () -> EntityType.Builder.of(KitsuneLightEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 1.4f).build("kitsune_light"));

    public static final RegistryObject<EntityType<BakenekoEntity>> BAKENEKO =
            ENTITY_TYPES.register("bakeneko", () -> EntityType.Builder.of(BakenekoEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 1.4f).build("bakeneko"));

    public static final RegistryObject<EntityType<IceDragonEntity>> ICE_DRAGON =
            ENTITY_TYPES.register("ice_dragon", () -> EntityType.Builder.of(IceDragonEntity::new, MobCategory.CREATURE)
                    .sized(6.0f, 12.0f)
                    .clientTrackingRange(32)
                    .build("ice_dragon"));

    public static final RegistryObject<EntityType<MoonDeerEntity>> MOON_DEER =
            ENTITY_TYPES.register("moon_deer", () -> EntityType.Builder.of(MoonDeerEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 1.7f).build("moon_deer"));

    public static final RegistryObject<EntityType<BlueButterflyEntity>> BLUE_BUTTERFLY =
            ENTITY_TYPES.register("blue_butterfly", () -> EntityType.Builder.of(BlueButterflyEntity::new, MobCategory.CREATURE)
                    .sized(0.2f, 0.2f)
                    .clientTrackingRange(10)
                    .build("blue_butterfly"));

    public static final RegistryObject<EntityType<WitchCatEntity>> WITCH_CAT =
            ENTITY_TYPES.register("witch_cat", () -> EntityType.Builder.of(WitchCatEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.7f).build("witch_cat"));

    public static final RegistryObject<EntityType<SeatEntity>> SEAT = ENTITY_TYPES.register("seat",
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.4f)
                    .clientTrackingRange(10)
                    .build("seat"));

    public static final RegistryObject<EntityType<KitsuneSoulPossessedEntity>> KITSUNE_SOUL_POSSESSED =
            ENTITY_TYPES.register("kitsune_soul_possessed",
                    () -> EntityType.Builder.of(KitsuneSoulPossessedEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.8f) // размеры игрока
                            .clientTrackingRange(32)
                            .build("kitsune_soul_possessed"));

    public static final RegistryObject<EntityType<GhostGuardsEntity>> GHOST_GUARDS =
            ENTITY_TYPES.register("ghost_guards",
                    () -> EntityType.Builder.<GhostGuardsEntity>of(GhostGuardsEntity::new, MobCategory.MONSTER)
                            .sized(0.7f, 1.8f)
                            .clientTrackingRange(10)
                            .build("ghost_guards"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
