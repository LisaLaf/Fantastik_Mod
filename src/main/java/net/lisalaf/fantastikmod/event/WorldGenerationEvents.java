package net.lisalaf.fantastikmod.event;

import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = fantastikmod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WorldGenerationEvents {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Этот код выполнится когда все регистры уже будут готовы
            System.out.println("[Fantastikmod] Village modification setup complete");
        });
    }
}