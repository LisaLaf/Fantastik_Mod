package net.lisalaf.fantastikmod;

import com.mojang.logging.LogUtils;
import net.lisalaf.fantastikmod.block.ModBlockEntities;
import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.datagen.loot.ModLootModfiers;
import net.lisalaf.fantastikmod.dialog.DialogSystem;
import net.lisalaf.fantastikmod.effect.ModEffects;
import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.event.ModEvent;
import net.lisalaf.fantastikmod.item.ModCreativeModTabs;
import net.lisalaf.fantastikmod.item.ModItems;
import net.lisalaf.fantastikmod.network.*;
import net.lisalaf.fantastikmod.particle.HeatParticle;
import net.lisalaf.fantastikmod.particle.LeavesParticle;
import net.lisalaf.fantastikmod.particle.ModParticles;
import net.lisalaf.fantastikmod.particle.WaterRippleParticle;
import net.lisalaf.fantastikmod.potion.ModPotions;
import net.lisalaf.fantastikmod.sound.ModSounds;
import net.lisalaf.fantastikmod.villager.ModVillagers;
import net.lisalaf.fantastikmod.worldgen.ModFeatures;
import net.lisalaf.fantastikmod.worldgen.biome.ModTerrablender;
import net.lisalaf.fantastikmod.worldgen.biome.sunface.ModSurfaceRules;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

import java.util.concurrent.CompletableFuture;

@Mod(fantastikmod.MOD_ID)
public class fantastikmod {
    public static final String MOD_ID = "fantastikmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "1";

    @SuppressWarnings("removal")
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );


    public fantastikmod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        DialogSystem.registerDialogs();

        ModCreativeModTabs.register(modEventBus);

        ModEntities.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModVillagers.register(modEventBus);

        ModSounds.register(modEventBus);

        ModLootModfiers.register(modEventBus);

        ModEffects.register(modEventBus);

        ModParticles.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(this::gatherData);

        ModPotions.register(modEventBus);

        modEventBus.addListener(this::registerPackets);

        ModFeatures.FEATURES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(ModEvent.class);

        modEventBus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::addFuel);

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::registerParticleProviders);
        }
    }

    private void registerPackets(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            int id = 0;
            NETWORK.registerMessage(id++, DialogPacket.class, DialogPacket::encode, DialogPacket::decode, DialogPacket::handle);
            NETWORK.registerMessage(id++, QuestPacket.class, QuestPacket::encode, QuestPacket::decode, QuestPacket::handle);
            NETWORK.registerMessage(id++, SyncQuestPacket.class, SyncQuestPacket::encode, SyncQuestPacket::decode, SyncQuestPacket::handle);
            NETWORK.registerMessage(id++, OpenMaskDialogPacket.class, OpenMaskDialogPacket::encode, OpenMaskDialogPacket::decode, OpenMaskDialogPacket::handle);
            NETWORK.registerMessage(id++, MaskGivePacket.class, MaskGivePacket::encode, MaskGivePacket::decode, MaskGivePacket::handle);
            LOGGER.info("Packets registered successfully");
        });
    }

    public void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.COLORED_LEAVES_PARTICLE.get(), LeavesParticle.Provider::new);
        event.registerSpriteSet(ModParticles.WATER_RIPPLE.get(), WaterRippleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.HEAT.get(), HeatParticle.Provider::new);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MOON_LILY.getId(), ModBlocks.POTTED_MOON_LILY);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MOON_NORTHERN_BLUEBELL.getId(), ModBlocks.POTTED_MOON_NORTHERN_BLUEBELL);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MOON_HEATHER.getId(), ModBlocks.POTTED_MOON_HEATHER);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.SPIDER_LILY.getId(), ModBlocks.POTTED_SPIDER_LILY);

            ModTerrablender.registerBiomes();

            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeRules());

            event.enqueueWork(() -> {
                BrewingRecipeRegistry.addRecipe(
                        new BrewingRecipe(
                                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.MUNDANE)),
                                Ingredient.of(ModItems.LILAC_BRANCH.get()),
                                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LILAC_BRANCH.get())
                        )
                );
                BrewingRecipeRegistry.addRecipe(
                        new BrewingRecipe(
                                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                                Ingredient.of(ModItems.LILAC_BRANCH.get()),
                                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LILAC_BRANCH.get())
                        )
                );
            });

        });
    }



    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
            event.accept(ModItems.EGG_KITSUNE_LIGHT);
            event.accept(ModItems.EGG_BLUE_BUTTERFLY);
            event.accept(ModItems.EGG_ICE_DRAGON);
            event.accept(ModItems.EGG_MOON_DEER);
        } else if (event.getTabKey().equals(CreativeModeTabs.FOOD_AND_DRINKS)) {
            event.accept(ModItems.TOFU);
            event.accept(ModItems.STRAWBERRY);
            event.accept(ModItems.RICE);
            event.accept(ModItems.STRAWBERRY_MOCHI);
            event.accept(ModItems.MATCHA_MOCHI);
            event.accept(ModItems.MOCHI);
        } else if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) {
            event.accept(ModItems.GEMKITSUNE);
            event.accept(ModItems.MOONMASCOT);
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
            event.setBurnTime(300);
        }
        if(event.getItemStack().getItem() == ModBlocks.TREE_MOON_LOG_BLOCK.get().asItem()) {
            event.setBurnTime(300);
        }
        if(event.getItemStack().getItem() == ModBlocks.TREE_MOON_WOOD.get().asItem()) {
            event.setBurnTime(300);
        }
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS_SLAB.get().asItem()) {
            event.setBurnTime(150); // 7.5 секунд
        }
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS_STAIRS.get().asItem()) {
            event.setBurnTime(300); // 15 секунд
        }
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS_FENCE.get().asItem()) {
            event.setBurnTime(300);
        }
        if(event.getItemStack().getItem() == ModBlocks.MOON_PLANKS_FENCE_GATE.get().asItem()) {
            event.setBurnTime(300);
        }
        if (event.getItemStack().getItem() == ModBlocks.LILAC_PLANKS.get().asItem()) event.setBurnTime(300);
        if (event.getItemStack().getItem() == ModBlocks.LILAC_LOG.get().asItem()) event.setBurnTime(300);
        if (event.getItemStack().getItem() == ModBlocks.LILAC_WOOD.get().asItem()) event.setBurnTime(300);
        if (event.getItemStack().getItem() == ModBlocks.LILAC_SLAB.get().asItem()) event.setBurnTime(150);
        if (event.getItemStack().getItem() == ModBlocks.LILAC_STAIRS.get().asItem()) event.setBurnTime(300);
        if (event.getItemStack().getItem() == ModBlocks.LILAC_FENCE.get().asItem()) event.setBurnTime(300);
        if (event.getItemStack().getItem() == ModBlocks.LILAC_FENCE_GATE.get().asItem()) event.setBurnTime(300);
        if (event.getItemStack().getItem() == ModBlocks.LILAC_DOOR.get().asItem()) event.setBurnTime(200);
        if (event.getItemStack().getItem() == ModBlocks.LILAC_TRAPDOOR.get().asItem()) event.setBurnTime(300);

    }
    public static void sendToServer(Object packet) {
        NETWORK.sendToServer(packet);
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        NETWORK.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

}
