package net.lisalaf.fantastikmod;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.datagen.ModWorldGenProvider;
import net.lisalaf.fantastikmod.datagen.loot.AddItemModifier;
import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.entity.client.BlueButterflyRenderer;
import net.lisalaf.fantastikmod.entity.client.IceDragonRenderer;
import net.lisalaf.fantastikmod.entity.client.KitsuneLightRenderer;
import net.lisalaf.fantastikmod.entity.client.MoonDeerRenderer;
import net.lisalaf.fantastikmod.entity.custom.BlueButterflyEntity;
import net.lisalaf.fantastikmod.event.ModEvent;
import net.lisalaf.fantastikmod.item.ModCreativeModTabs;
import net.lisalaf.fantastikmod.item.ModItems;
import net.lisalaf.fantastikmod.sound.ModSounds;
import net.lisalaf.fantastikmod.util.ModTags;
import net.lisalaf.fantastikmod.villager.ModVillagers;
import net.lisalaf.fantastikmod.worldgen.ModFeatures;
import net.lisalaf.fantastikmod.worldgen.biome.ModTerrablender;
import net.lisalaf.fantastikmod.worldgen.biome.sunface.ModSurfaceRules;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(fantastikmod.MOD_ID)
public class fantastikmod {
    public static final String MOD_ID = "fantastikmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public fantastikmod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        System.out.println("=== FANTASTIKMOD LOADING ===");
        System.out.println("MOD ID: " + MOD_ID);


        ModCreativeModTabs.register(modEventBus);

        ModEntities.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);



        ModVillagers.register(modEventBus);

        ModSounds.register(modEventBus);

        //ModTerrablender.registerBiomes();

        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(this::gatherData);


        ModFeatures.FEATURES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(ModEvent.class);

        modEventBus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::addFuel);
        LOOT_MODIFIERS.register(modEventBus);
       // MinecraftForge.EVENT_BUS.register(this);modEventBus.addListener(this::addCreative);


    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            System.out.println("=== FINAL STRUCTURE CHECK ===");


            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MOON_LILY.getId(), ModBlocks.POTTED_MOON_LILY);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MOON_NORTHERN_BLUEBELL.getId(), ModBlocks.POTTED_MOON_NORTHERN_BLUEBELL);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MOON_HEATHER.getId(), ModBlocks.POTTED_MOON_HEATHER);

            ModTerrablender.registerBiomes();

            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeRules());
        });
    }



    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
            event.accept(ModItems.EGG_KITSUNE_LIGHT);
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
            event.accept(ModItems.EGG_BLUE_BUTTERFLY);
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
            event.accept(ModItems.EGG_ICE_DRAGON);
        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
            event.accept(ModItems.EGG_MOON_DEER );
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
            event.accept(ModItems.TOFU);
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
            event.accept(ModItems.STRAWBERRY);
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
            event.accept(ModItems.RICE);
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
            event.accept(ModItems.STRAWBERRY_MOCHI);
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
            event.accept(ModItems.MATCHA_MOCHI);
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
            event.accept(ModItems.MOCHI);
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.GEMKITSUNE);

            event.accept(ModItems.MOONMASCOT);

                }

    }






    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.KITSUNE_LIGHT.get(), KitsuneLightRenderer::new);
            EntityRenderers.register(ModEntities.ICE_DRAGON.get(), IceDragonRenderer::new);
            EntityRenderers.register(ModEntities.MOON_DEER.get(), MoonDeerRenderer::new);
            EntityRenderers.register(ModEntities.BLUE_BUTTERFLY.get(), BlueButterflyRenderer::new);


        }
    }


    private void gatherData(GatherDataEvent event) {
        net.minecraft.data.DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
        }
    }
    public void addFuel(FurnaceFuelBurnTimeEvent event) {
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS.get().asItem()) {
            event.setBurnTime(300); // 15 секунд
        }
        if(event.getItemStack().getItem() == ModBlocks.TREE_MOON_LOG_BLOCK.get().asItem()) {
            event.setBurnTime(300); // 15 секунд
        }
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS_SLAB.get().asItem()) {
            event.setBurnTime(150); // 7.5 секунд
        }
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS_STAIRS.get().asItem()) {
            event.setBurnTime(300); // 15 секунд
        }
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS_FENCE.get().asItem()) {
            event.setBurnTime(300); // 15 секунд
        }
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS_FENCE_GATE.get().asItem()) {
            event.setBurnTime(300); // 15 секунд
        }
    }

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    public static final RegistryObject<Codec<AddItemModifier>> ADD_ITEM =
            LOOT_MODIFIERS.register("add_item", AddItemModifier.CODEC);
}
