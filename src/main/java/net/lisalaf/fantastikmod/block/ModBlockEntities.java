package net.lisalaf.fantastikmod.block;

import net.lisalaf.fantastikmod.block.custom.entity.*;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.lisalaf.fantastikmod.block.ModBlocks;

import javax.annotation.Nullable;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, fantastikmod.MOD_ID);

    public static final RegistryObject<BlockEntityType<DryingBasketBlockEntity>> DRYING_BASKET =
            BLOCK_ENTITIES.register("drying_basket", () ->
                    BlockEntityType.Builder.of(DryingBasketBlockEntity::new,
                            ModBlocks.DRYING_BASKET.get()).build(null));

    public static final RegistryObject<BlockEntityType<TapestryBlockEntity>> TAPESTRY =
            BLOCK_ENTITIES.register("tapestry", () ->
                    BlockEntityType.Builder.of(TapestryBlockEntity::new,
                            ModBlocks.TAPESTRY.get(),
                            ModBlocks.TAPESTRY_CASTLE_0.get(), ModBlocks.TAPESTRY_CASTLE_1.get(),
                            ModBlocks.TAPESTRY_CASTLE_2.get(), ModBlocks.TAPESTRY_CASTLE_3.get(),
                            ModBlocks.TAPESTRY_CASTLE_4.get(),
                            ModBlocks.TAPESTRY_CASTLE_BURNED_0.get(), ModBlocks.TAPESTRY_CASTLE_BURNED_1.get(),
                            ModBlocks.TAPESTRY_CASTLE_BURNED_2.get(), ModBlocks.TAPESTRY_CASTLE_BURNED_3.get(),
                            ModBlocks.TAPESTRY_CASTLE_BURNED_4.get()).build(null));

    public static final RegistryObject<BlockEntityType<RoyalBedBlockEntity>> ROYAL_BED =
            BLOCK_ENTITIES.register("royal_bed", () ->
                    BlockEntityType.Builder.of(RoyalBedBlockEntity::new, ModBlocks.ROYAL_BED.get()).build(null));

    public static final RegistryObject<BlockEntityType<CharredThroneBlockEntity>> CHARRED_THRONE =
            BLOCK_ENTITIES.register("charred_throne", () ->
                    BlockEntityType.Builder.of(CharredThroneBlockEntity::new, ModBlocks.CHARRED_THRONE.get()).build(null));

    public static final RegistryObject<BlockEntityType<RemainsGuardsBlockEntity>> REMAINS_GUARDS =
            BLOCK_ENTITIES.register("remains_guards", () ->
                    BlockEntityType.Builder.of(RemainsGuardsBlockEntity::new,
                            ModBlocks.REMAINS_GUARDS.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}