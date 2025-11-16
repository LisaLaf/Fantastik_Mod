package net.lisalaf.fantastikmod.datagen;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.datagen.loot.ModBlockLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = fantastikmod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        System.out.println("=== DATA GENERATION STARTED ===");


        // 1. Block States (модели блоков)
         generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));

        // 2. Item Models (модели предметов)
          generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

        // 3. Loot Tables (дроп с блоков)
         generator.addProvider(event.includeServer(), new ModBlockLootTables(packOutput));

        // 4. Recipe Provider (рецепты) - если есть
         generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));

        // 5. Block Tags (теги блоков) - если есть
         generator.addProvider(event.includeServer(), new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModPoiTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));

        // 6. Item Tags (теги предметов) - если есть
         // generator.addProvider(event.includeServer(), new ModItemTagGenerator(packOutput, lookupProvider, existingFileHelper));

        System.out.println("=== ALL DATA PROVIDERS REGISTERED SUCCESSFULLY ===");
    }
}