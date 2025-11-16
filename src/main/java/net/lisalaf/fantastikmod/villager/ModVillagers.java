package net.lisalaf.fantastikmod.villager;

import com.google.common.collect.ImmutableSet;
import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, fantastikmod.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, fantastikmod.MOD_ID);

    public static final RegistryObject<PoiType> DRYING_BASKET_POI = POI_TYPES.register("drying_basket_poi",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.DRYING_BASKET.get().getStateDefinition().getPossibleStates()), 1,1));

    public static final RegistryObject<VillagerProfession> TEA_MASTER =
            VILLAGER_PROFESSIONS.register("teamaster", () -> new VillagerProfession("teamaster",
                    holder -> holder.get() == DRYING_BASKET_POI.get(), holder -> holder.get() == DRYING_BASKET_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_ARMORER));





    public static void  register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
