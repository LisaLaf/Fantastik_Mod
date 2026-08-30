package net.lisalaf.fantastikmod.item;

import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.C;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, fantastikmod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FANTASTIK_TAB = CREATIVE_MODE_TABS.register("fantastik_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MOONMASCOT.get()))
                    .title(Component.translatable("cretivetab.fantastik_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                       // output.accept(ModItems.SOUL_TAMED_KITSUNE.get());
                       // output.accept(ModItems.LIVELY_SOUL_TAMED_KITSUNE.get());
// Яйца спавна
                        output.accept(ModItems.EGG_KITSUNE_LIGHT.get());
                        output.accept(ModItems.EGG_ICE_DRAGON.get());
                        output.accept(ModItems.EGG_MOON_DEER.get());
                        output.accept(ModItems.EGG_BLUE_BUTTERFLY.get());
                        output.accept(ModItems.EGG_BAKENEKO.get());
//Разное
                        output.accept(ModItems.TEA_CEREMONY_MUSIC_DISC.get());

// Материалы и ресурсы
                        output.accept(ModItems.AURIPIGMENT.get());
                        output.accept(ModItems.SILVER_RAW.get());
                        output.accept(ModItems.GEM_MOON.get());
                        output.accept(ModItems.SILVER_INGOT.get());
                        output.accept(ModItems.MOON_DUST.get());
                        output.accept(ModItems.FUR_ICE_DRAGON.get());
                        output.accept(ModItems.HEART_ICE_DRAGON.get());
                        output.accept(ModItems.SILVER_STICK.get());
                        output.accept(ModItems.ECHO_SHARD.get());
                        output.accept(ModItems.TOTEM_BLUEPRINT.get());
                        output.accept(ModItems.GEMKITSUNE.get());
                        output.accept(ModItems.MOONMASCOT.get());
                        output.accept(ModItems.WITHERED_SCALES_FIRE_DRAGON.get());

// Еда и напитки
                        output.accept(ModItems.TOFU.get());
                        output.accept(ModItems.RICE.get());
                        output.accept(ModItems.STRAWBERRY.get());
                        output.accept(ModItems.TEA_LEAF.get());
                        output.accept(ModItems.MOON_CROWBERRY.get());
                        output.accept(ModItems.CATNIP.get());
                        output.accept(ModItems.DRIED_CATNIP.get());
                        output.accept(ModItems.MATCHA_MOCHI.get());
                        output.accept(ModItems.STRAWBERRY_MOCHI.get());
                        output.accept(ModItems.MOCHI.get());
                        output.accept(ModItems.DOUGH_MOCHI.get());
                        output.accept(ModItems.MATCHA_POWDER.get());
                        output.accept(ModItems.TEA_LEAFS_GREEN.get());
                        output.accept(ModItems.TEA_LEAFS_BLACK.get());
                        output.accept(ModItems.GREEN_TEA.get());
                        output.accept(ModItems.BLACK_TEA.get());
                        output.accept(ModItems.MATCHA_TEA.get());
                        output.accept(ModItems.SAKE_DRINK.get());
                        output.accept(ModBlocks.CATNIP_WINE.get());

// Семена
                        output.accept(ModItems.STRAWBERRY_SEEDS.get());
                        output.accept(ModItems.TEA_SEEDS.get());
                        output.accept(ModItems.CATNIP_SEEDS.get());

// Инструменты
                        output.accept(ModItems.MOON_AXE.get());
                        output.accept(ModItems.MOON_PICKAXE.get());
                        output.accept(ModItems.MOON_SHOVEL.get());
                        output.accept(ModItems.MOON_SWORD.get());
                        output.accept(ModItems.SWORD_AURIPIGMENT.get());
                        output.accept(ModItems.SILVER_HOE.get());
                        output.accept(ModItems.SILVER_AXE.get());
                        output.accept(ModItems.SILVER_SWORD.get());
                        output.accept(ModItems.SILVER_PICKAXE.get());
                        output.accept(ModItems.SILVER_SHOVEL.get());
                        output.accept(ModItems.BUILDERS_WAND.get());

// Броня
                        output.accept(ModItems.AURIPIGMENT_BOOTS.get());
                        output.accept(ModItems.AURIPIGMENT_LEGGINGS.get());
                        output.accept(ModItems.AURIPIGMENT_CHESTPLATE.get());
                        output.accept(ModItems.AURIPIGMENT_HELMET.get());

                        output.accept(ModItems.MOON_BOOTS.get());
                        output.accept(ModItems.MOON_LEGGINGS.get());
                        output.accept(ModItems.MOON_CHESTPLATE.get());
                        output.accept(ModItems.MOON_HELMET.get());

                        output.accept(ModItems.SILVER_HELMET.get());
                        output.accept(ModItems.SILVER_CHESTPLATE.get());
                        output.accept(ModItems.SILVER_LEGGINGS.get());
                        output.accept(ModItems.SILVER_BOOTS.get());

                        output.accept(ModItems.BOOTS_FUR_ICE_DRAGON.get());
                        output.accept(ModItems.LEGGINGS_FUR_ICE_DRAGON.get());
                        output.accept(ModItems.CHESTPLATE_FUR_ICE_DRAGON.get());
                        output.accept(ModItems.HELMET_FUR_ICE_DRAGON.get());

                        output.accept(ModItems.RUSTY_HELMET.get());
                        output.accept(ModItems.RUSTY_CHESTPLATE.get());
                        output.accept(ModItems.RUSTY_LEGGINGS.get());
                        output.accept(ModItems.RUSTY_BOOTS.get());

                        output.accept(ModItems.WITHERED_SCALES_FIRE_DRAGON_HELMET.get());
                        output.accept(ModItems.WITHERED_SCALES_FIRE_DRAGON_CHESTPLATE.get());
                        output.accept(ModItems.WITHERED_SCALES_FIRE_DRAGON_LEGGINGS.get());
                        output.accept(ModItems.WITHERED_SCALES_FIRE_DRAGON_BOOTS.get());

// Аксессуары
                        output.accept(ModItems.WINGS.get());
                        output.accept(ModItems.KITSUNE_MASK_0.get());
                        output.accept(ModItems.KITSUNE_MASK_1.get());
                        output.accept(ModItems.KITSUNE_MASK_2.get());
                        output.accept(ModItems.KITSUNE_SOUL_MASK_0.get());
                        output.accept(ModItems.KITSUNE_SOUL_MASK_1.get());
                        output.accept(ModItems.KITSUNE_SOUL_MASK_2.get());

// Декорации и свет
                        output.accept(ModItems.MOON_LAMP.get());
                        output.accept(ModBlocks.DRYING_BASKET.get());
                        output.accept(ModBlocks.KITSUNE_STATUE.get());
                        output.accept(ModBlocks.SAKE.get());
                        output.accept(ModBlocks.TAPESTRY.get());
                        output.accept(ModBlocks.BIG_CITY_GATE.get());
                        //output.accept(ModBlocks.TAPESTRY_CASTLE_0.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_BURNED_0.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_1.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_2.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_3.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_4.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_BURNED_1.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_BURNED_2.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_BURNED_3.get());
                        output.accept(ModBlocks.TAPESTRY_CASTLE_BURNED_4.get());
                        output.accept(ModBlocks.ROYAL_BED.get());
                        output.accept(ModBlocks.ROOF_TILE_MEDIEVAL_CLAY_DAMAGED.get());
                        output.accept(ModBlocks.ROOF_TILE_MEDIEVAL_IRON_DAMAGED.get());
                        output.accept(ModBlocks.ROOF_TILE_MEDIEVAL_COPPER_DAMAGED.get());
                        output.accept(ModBlocks.ROOF_TILE_MEDIEVAL_DEEPSLATE_DAMAGED.get());
                        output.accept(ModBlocks.ROOF_CAP_MEDIEVAL_CLAY_DAMAGED.get());
                        output.accept(ModBlocks.ROOF_CAP_MEDIEVAL_IRON_DAMAGED.get());
                        output.accept(ModBlocks.ROOF_CAP_MEDIEVAL_COPPER_DAMAGED.get());
                        output.accept(ModBlocks.ROOF_CAP_MEDIEVAL_DEEPSLATE_DAMAGED.get());
                        output.accept(ModBlocks.LARGE_TORCH.get());
                        output.accept(ModBlocks.CHARRED_THRONE.get());
                        output.accept(ModBlocks.WOODEN_CHAIR.get());
                        output.accept(ModBlocks.TABLECLOTH_WHITE.get());
                        output.accept(ModBlocks.PAPER_LANTERN.get());
                        output.accept(ModBlocks.YUKIMI_TORO.get());
                        output.accept(ModBlocks.PETRIFIED_LARGE_EGG.get());

// Природные блоки
                        output.accept(ModBlocks.ASH_BLOCK.get());
                        output.accept(ModBlocks.MOONSTONE.get());
                        output.accept(ModBlocks.MOON_ORE.get());
                        output.accept(ModBlocks.MOON_GEM_BLOCK.get());
                        output.accept(ModBlocks.MOON_CRYSTAL_BLOCK.get());
                        output.accept(ModBlocks.MOON_CRYSTAL.get());
                        output.accept(ModBlocks.MOON_CRYSTAL_GLASS.get());
                        output.accept(ModBlocks.MOON_CRYSTAL_GLASS_PANE.get());
                        output.accept(ModBlocks.SILVER_ORE.get());
                        output.accept(ModBlocks.SILVER_BLOCK.get());
                        output.accept(ModBlocks.AURIPIGMENT_ORE_BLOCK.get());
                        output.accept(ModBlocks.AURIPIGMENT_BLOCK.get());
                        output.accept(ModBlocks.GEMKITSUNE_BLOCK.get());
                        output.accept(ModBlocks.TREE_MOON_FOLIAGE_BLOCK.get());
                        output.accept(ModBlocks.FROST.get());
                        output.accept(ModBlocks.DENSE_WATER_SURFACE.get());
                        output.accept(ModBlocks.DENSE_WATER_MID_HIGH.get());
                        output.accept(ModBlocks.DENSE_WATER_MID_LOW.get());
                        output.accept(ModBlocks.DENSE_WATER_DEEP.get());

// Растения
                        output.accept(ModBlocks.SPIDER_LILY.get());
                        output.accept(ModBlocks.MOON_LILY.get());
                        output.accept(ModBlocks.MOON_NORTHERN_BLUEBELL.get());
                        output.accept(ModBlocks.MOON_HEATHER.get());
                        output.accept(ModBlocks.MOON_GRASS.get());
                        output.accept(ModBlocks.MOON_GRASS_1.get());
                        output.accept(ModBlocks.WILD_CATNIP.get());
                        output.accept(ModBlocks.MOON_VINE.get());
                        output.accept(ModBlocks.MOON_SAPLING.get());
                        output.accept(ModBlocks.LILAC_SAPLING.get());
                        output.accept(ModItems.LILAC_BRANCH.get());

// Дерево — Лунное
                        output.accept(ModBlocks.TREE_MOON_LOG_BLOCK.get());
                        output.accept(ModBlocks.TREE_MOON_WOOD.get());
                        output.accept(ModBlocks.MOON_PLANKS.get());
                        output.accept(ModBlocks.MOON_PLANKS_STAIRS.get());
                        output.accept(ModBlocks.MOON_PLANKS_SLAB.get());
                        output.accept(ModBlocks.MOON_PLANKS_FENCE.get());
                        output.accept(ModBlocks.MOON_PLANKS_FENCE_GATE.get());
                        output.accept(ModBlocks.MOON_PLANKS_BUTTON.get());
                        output.accept(ModBlocks.MOON_PLANKS_PLATE.get());
                        output.accept(ModBlocks.MOON_WALL.get());
                        output.accept(ModBlocks.MOON_TREE_DOOR.get());
                        output.accept(ModBlocks.MOON_TREE_TRAPDOOR.get());

// Дерево — Сирень
                        output.accept(ModBlocks.LILAC_LOG.get());
                        output.accept(ModBlocks.LILAC_WOOD.get());
                        output.accept(ModBlocks.STRIPPED_LILAC_LOG.get());
                        output.accept(ModBlocks.STRIPPED_LILAC_WOOD.get());
                        output.accept(ModBlocks.LILAC_PLANKS.get());
                        output.accept(ModBlocks.LILAC_STAIRS.get());
                        output.accept(ModBlocks.LILAC_SLAB.get());
                        output.accept(ModBlocks.LILAC_FENCE.get());
                        output.accept(ModBlocks.LILAC_FENCE_GATE.get());
                        output.accept(ModBlocks.LILAC_BUTTON.get());
                        output.accept(ModBlocks.LILAC_PRESSURE_PLATE.get());
                        output.accept(ModBlocks.LILAC_DOOR.get());
                        output.accept(ModBlocks.LILAC_TRAPDOOR.get());
                        output.accept(ModBlocks.LILAC_LEAVES.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
