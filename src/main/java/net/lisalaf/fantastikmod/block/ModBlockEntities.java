package net.lisalaf.fantastikmod.block;

import net.lisalaf.fantastikmod.block.custom.entity.DryingBasketBlockEntity;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, fantastikmod.MOD_ID);

    public static final RegistryObject<BlockEntityType<DryingBasketBlockEntity>> DRYING_BASKET =
            BLOCK_ENTITIES.register("drying_basket", () ->
                    BlockEntityType.Builder.of(DryingBasketBlockEntity::new,
                            ModBlocks.DRYING_BASKET.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
