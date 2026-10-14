package net.lisalaf.fantastikmod.datagen;

import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.block.custom.*;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@SuppressWarnings("removal")
public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, fantastikmod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        // ПРОСТЫЕ БЛОКИ
        simpleBlockWithItem(ModBlocks.ASH_BLOCK.get(), cubeAll(ModBlocks.ASH_BLOCK.get()));
        simpleBlockWithItem(ModBlocks.MOON_CRYSTAL_BLOCK.get(), cubeAll(ModBlocks.MOON_CRYSTAL_BLOCK.get()));
        simpleBlockWithItem(ModBlocks.AURIPIGMENT_BLOCK.get(), cubeAll(ModBlocks.AURIPIGMENT_BLOCK.get()));

        ModelFile idleModel = models().getExistingFile(modLoc("block/drying_basket"));
        ModelFile dryingModel = models().getExistingFile(modLoc("block/drying_basket_cooking"));

        getVariantBuilder(ModBlocks.DRYING_BASKET.get())
                .partialState().with(DryingBasketBlock.DRYING, false).modelForState().modelFile(idleModel).addModel()
                .partialState().with(DryingBasketBlock.DRYING, true).modelForState().modelFile(dryingModel).addModel();

        simpleBlockWithItem(ModBlocks.GEMKITSUNE_BLOCK.get(), cubeAll(ModBlocks.GEMKITSUNE_BLOCK.get()));
        simpleBlockWithItem(ModBlocks.MOON_PLANKS.get(), cubeAll(ModBlocks.MOON_PLANKS.get()));

        simpleBlockWithItem(ModBlocks.AURIPIGMENT_ORE_BLOCK.get(), cubeAll(ModBlocks.AURIPIGMENT_ORE_BLOCK.get()));
        simpleBlockWithItem(ModBlocks.MOON_ORE.get(), cubeAll(ModBlocks.MOON_ORE.get()));
        simpleBlockWithItem(ModBlocks.MOONSTONE.get(), cubeAll(ModBlocks.MOONSTONE.get()));
        simpleBlockWithItem(ModBlocks.SILVER_ORE.get(), cubeAll(ModBlocks.SILVER_ORE.get()));
        simpleBlockWithItem(ModBlocks.SILVER_BLOCK.get(), cubeAll(ModBlocks.SILVER_BLOCK.get()));
        simpleBlockWithItem(ModBlocks.MOON_GEM_BLOCK.get(), cubeAll(ModBlocks.MOON_GEM_BLOCK.get()));

        leavesBlock(ModBlocks.TREE_MOON_FOLIAGE_BLOCK);
        
        logBlock((RotatedPillarBlock) ModBlocks.TREE_MOON_LOG_BLOCK.get(),
                modLoc("block/tree_moon_log_block"),
                modLoc("block/tree_moon_log_block_top")
        );

        // ФУНКЦИОНАЛЬНЫЕ БЛОКИ
        // Лестница
        stairsBlock(((StairBlock) ModBlocks.MOON_PLANKS_STAIRS.get()),
                blockTexture(ModBlocks.MOON_PLANKS.get()));

        // Плита
        slabBlock(((SlabBlock) ModBlocks.MOON_PLANKS_SLAB.get()),
                blockTexture(ModBlocks.MOON_PLANKS.get()),
                blockTexture(ModBlocks.MOON_PLANKS.get()));

        // Забор
        fenceBlock(((FenceBlock) ModBlocks.MOON_PLANKS_FENCE.get()),
                blockTexture(ModBlocks.MOON_PLANKS.get()));

        // Калитка
        fenceGateBlock(((FenceGateBlock) ModBlocks.MOON_PLANKS_FENCE_GATE.get()),
                blockTexture(ModBlocks.MOON_PLANKS.get()));

        // Стена
        wallBlock(((WallBlock) ModBlocks.MOON_WALL.get()),
                blockTexture(ModBlocks.MOON_PLANKS.get()));

        // Кнопка
        buttonBlock(((ButtonBlock) ModBlocks.MOON_PLANKS_BUTTON.get()),
                blockTexture(ModBlocks.MOON_PLANKS.get()));

        // Нажимная плита
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.MOON_PLANKS_PLATE.get()),
                blockTexture(ModBlocks.MOON_PLANKS.get()));

        // Люк
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.MOON_TREE_TRAPDOOR.get()),
                modLoc("block/moon_tree_trapdoor"), true, "cutout");

        // Дверь
        doorBlockWithRenderType(((DoorBlock) ModBlocks.MOON_TREE_DOOR.get()),
                modLoc("block/moon_tree_door_bottom"),
                modLoc("block/moon_tree_door_top"), "cutout");

        makeStrawberryCrop((CropBlock) ModBlocks.STRAWBERRY_CROP.get(), "strawberry_stage", "strawberry_stage");
        makeTeaCrop((CropBlock) ModBlocks.TEA_CROP.get(), "tea_stage", "tea_stage");
        makeCrowberryShrub((CropBlock) ModBlocks.BUSH_CROWBERRY.get(), "shrub_crowberry_", "shrub_crowberry_");
        makeRiceCrop(((CropBlock) ModBlocks.RICE_CROP.get()), "rice_stage_", "rice_stage_");

        simpleBlockWithItem(ModBlocks.SPIDER_LILY.get(), models().cross(blockTexture(ModBlocks.SPIDER_LILY.get()).getPath(),
                blockTexture(ModBlocks.SPIDER_LILY.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_SPIDER_LILY.get(), models().singleTexture("potted_spider_lily", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.SPIDER_LILY.get())).renderType("cutout"));

        simpleBlockWithItem(ModBlocks.MOON_LILY.get(), models().cross(blockTexture(ModBlocks.MOON_LILY.get()).getPath(),
                blockTexture(ModBlocks.MOON_LILY.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_MOON_LILY.get(), models().singleTexture("potted_moon_lily", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.MOON_LILY.get())).renderType("cutout"));

        simpleBlockWithItem(ModBlocks.MOON_HEATHER.get(), models().cross(blockTexture(ModBlocks.MOON_HEATHER.get()).getPath(),
                blockTexture(ModBlocks.MOON_HEATHER.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_MOON_HEATHER.get(), models().singleTexture("potted_moon_heather", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.MOON_HEATHER.get())).renderType("cutout"));

        simpleBlockWithItem(ModBlocks.MOON_NORTHERN_BLUEBELL.get(), models().cross(blockTexture(ModBlocks.MOON_NORTHERN_BLUEBELL.get()).getPath(),
                blockTexture(ModBlocks.MOON_NORTHERN_BLUEBELL.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_MOON_NORTHERN_BLUEBELL.get(), models().singleTexture("potted_moon_northern_bluebell", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.MOON_NORTHERN_BLUEBELL.get())).renderType("cutout"));

        simpleBlockWithItem(ModBlocks.MOON_GRASS.get(),
                models().cross(blockTexture(ModBlocks.MOON_GRASS.get()).getPath(),
                                blockTexture(ModBlocks.MOON_GRASS.get()))
                        .renderType("cutout"));
        simpleBlockWithItem(ModBlocks.MOON_GRASS_1.get(),
                models().cross(blockTexture(ModBlocks.MOON_GRASS_1.get()).getPath(),
                                blockTexture(ModBlocks.MOON_GRASS_1.get()))
                        .renderType("cutout"));

        logBlock((RotatedPillarBlock) ModBlocks.TREE_MOON_WOOD.get(),
                modLoc("block/tree_moon_log_block"),
                modLoc("block/tree_moon_log_block"));

        saplingBlock(ModBlocks.MOON_SAPLING);

        ModelFile vineBodyModel = models().cross("moon_vine", modLoc("block/moon_vine")).renderType("cutout");
        ModelFile vineTipModel = models().cross("moon_vine_tip", modLoc("block/moon_vine_tip")).renderType("cutout");

        getVariantBuilder(ModBlocks.MOON_VINE.get())
                .partialState().with(MoonVineBlock.TIP, false).modelForState().modelFile(vineBodyModel).addModel()
                .partialState().with(MoonVineBlock.TIP, true).modelForState().modelFile(vineTipModel).addModel();


        simpleBlockWithItem(ModBlocks.MOON_CRYSTAL_GLASS.get(),
                models().cubeAll("moon_crystal_glass",
                                modLoc("block/moon_crystal_glass"))
                        .renderType("translucent"));

        ResourceLocation glassTexture = blockTexture(ModBlocks.MOON_CRYSTAL_GLASS.get());
        ModelFile post = models().withExistingParent("moon_crystal_glass_pane_post",
                        "block/template_glass_pane_post")
                .texture("pane", glassTexture)
                .texture("edge", glassTexture)
                .renderType("translucent");
        ModelFile side = models().withExistingParent("moon_crystal_glass_pane_side",
                        "block/template_glass_pane_side")
                .texture("pane", glassTexture)
                .texture("edge", glassTexture)
                .renderType("translucent");
        ModelFile sideAlt = models().withExistingParent("moon_crystal_glass_pane_side_alt",
                        "block/template_glass_pane_side_alt")
                .texture("pane", glassTexture)
                .texture("edge", glassTexture)
                .renderType("translucent");
        ModelFile noSide = models().withExistingParent("moon_crystal_glass_pane_noside",
                        "block/template_glass_pane_noside")
                .texture("pane", glassTexture)
                .texture("edge", glassTexture)
                .renderType("translucent");
        ModelFile noSideAlt = models().withExistingParent("moon_crystal_glass_pane_noside_alt",
                        "block/template_glass_pane_noside_alt")
                .texture("pane", glassTexture)
                .texture("edge", glassTexture)
                .renderType("translucent");
        paneBlock((IronBarsBlock) ModBlocks.MOON_CRYSTAL_GLASS_PANE.get(),
                post, side, sideAlt, noSide, noSideAlt);

        getVariantBuilder(ModBlocks.MOON_CRYSTAL.get()).forAllStates(state -> {
            Direction dir = state.getValue(AmethystClusterBlock.FACING);

            ModelFile modelFile = models().withExistingParent("moon_crystal_" + dir.getName(), "block/cross")
                    .texture("cross", modLoc("block/moon_crystal"))
                    .renderType("cutout");

            int rotationX = 0;
            int rotationY = 0;

            switch(dir) {
                case DOWN:
                    rotationX = 180;
                    rotationY = 0;
                    break;
                case UP:
                    rotationX = 0;
                    rotationY = 0;
                    break;
                case NORTH:
                    rotationX = 90;
                    rotationY = 0;
                    break;
                case SOUTH:
                    rotationX = 90;
                    rotationY = 180;
                    break;
                case WEST:
                    rotationX = 90;
                    rotationY = 270;
                    break;
                case EAST:
                    rotationX = 90;
                    rotationY = 90;
                    break;
            }

            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(rotationX)
                    .rotationY(rotationY)
                    .build();
        });
        simpleBlockItem(ModBlocks.MOON_CRYSTAL.get(),
                models().withExistingParent("moon_crystal", "item/generated")
                        .texture("layer0", modLoc("block/moon_crystal"))
                        .renderType("cutout"));

        simpleBlockWithItem(ModBlocks.FROST.get(),
                models().withExistingParent("frost", "block/snow_height2")
                        .texture("particle", modLoc("block/frost"))
                        .texture("texture", modLoc("block/frost"))
                        .renderType("translucent"));

        simpleBlockWithItem(ModBlocks.CATNIP_WINE.get(),
                models().getExistingFile(modLoc("block/catnip_wine")));

        getVariantBuilder(ModBlocks.KITSUNE_STATUE.get()).forAllStates(state -> {
            Direction dir = state.getValue(KitsuneStatueBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modLoc("block/kitsune_statue")))
                    .rotationY((int) dir.toYRot())
                    .build();
        });
        simpleBlockItem(ModBlocks.KITSUNE_STATUE.get(),
                models().getExistingFile(modLoc("block/kitsune_statue")));

        getVariantBuilder(ModBlocks.SAKE.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modLoc("block/sake")))
                    .build();
        });
        simpleBlockItem(ModBlocks.SAKE.get(),
                models().getExistingFile(modLoc("block/sake")));

        simpleBlockWithItem(ModBlocks.WILD_CATNIP.get(),
                models().cross("wild_catnip", modLoc("block/wild_catnip"))
                        .renderType("cutout"));

        makeCatnipCrop((CropBlock) ModBlocks.CATNIP_CROP.get(), "catnip_stage_", "catnip_stage_");

        // Бревно сирени
        logBlock((RotatedPillarBlock) ModBlocks.LILAC_LOG.get(),
                modLoc("block/lilac_log"),
                modLoc("block/lilac_log_top"));

        ModelFile lilacLeavesModel = models().singleTexture("lilac_leaves",
                        new ResourceLocation("minecraft:block/leaves"),
                        "all", modLoc("block/lilac_leaves"))
                .renderType("cutout_mipped");

        ModelFile floweringLilacLeavesModel = models().singleTexture("flowering_lilac_leaves",
                        new ResourceLocation("minecraft:block/leaves"),
                        "all", modLoc("block/flowering_lilac_leaves"))
                .renderType("cutout_mipped");

        getVariantBuilder(ModBlocks.LILAC_LEAVES.get())
                .partialState().with(LilacLeavesBlock.FLOWERING, false)
                .modelForState().modelFile(lilacLeavesModel).addModel()
                .partialState().with(LilacLeavesBlock.FLOWERING, true)
                .modelForState().modelFile(floweringLilacLeavesModel).addModel();

        saplingBlock(ModBlocks.LILAC_SAPLING);

        simpleBlockWithItem(ModBlocks.LILAC_PLANKS.get(), cubeAll(ModBlocks.LILAC_PLANKS.get()));
        logBlock((RotatedPillarBlock) ModBlocks.LILAC_WOOD.get(), modLoc("block/lilac_log"), modLoc("block/lilac_log"));
        logBlock((RotatedPillarBlock) ModBlocks.STRIPPED_LILAC_LOG.get(), modLoc("block/stripped_lilac_log"), modLoc("block/stripped_lilac_log_top"));
        logBlock((RotatedPillarBlock) ModBlocks.STRIPPED_LILAC_WOOD.get(), modLoc("block/stripped_lilac_log"), modLoc("block/stripped_lilac_log"));

        stairsBlock((StairBlock) ModBlocks.LILAC_STAIRS.get(), blockTexture(ModBlocks.LILAC_PLANKS.get()));
        slabBlock((SlabBlock) ModBlocks.LILAC_SLAB.get(), blockTexture(ModBlocks.LILAC_PLANKS.get()), blockTexture(ModBlocks.LILAC_PLANKS.get()));
        fenceBlock((FenceBlock) ModBlocks.LILAC_FENCE.get(), blockTexture(ModBlocks.LILAC_PLANKS.get()));
        fenceGateBlock((FenceGateBlock) ModBlocks.LILAC_FENCE_GATE.get(), blockTexture(ModBlocks.LILAC_PLANKS.get()));
        buttonBlock((ButtonBlock) ModBlocks.LILAC_BUTTON.get(), blockTexture(ModBlocks.LILAC_PLANKS.get()));
        pressurePlateBlock((PressurePlateBlock) ModBlocks.LILAC_PRESSURE_PLATE.get(), blockTexture(ModBlocks.LILAC_PLANKS.get()));
        doorBlockWithRenderType(((DoorBlock) ModBlocks.LILAC_DOOR.get()),
                modLoc("block/lilac_door_bottom"),
                modLoc("block/lilac_door_top"), "cutout");

        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.LILAC_TRAPDOOR.get()),
                modLoc("block/lilac_trapdoor"), true, "cutout");

        ModelFile gateClosed = models().getExistingFile(modLoc("block/big_city_gate"));
        ModelFile gateOpen = models().getExistingFile(modLoc("block/big_city_gate_open"));

        getVariantBuilder(ModBlocks.BIG_CITY_GATE.get()).forAllStates(state -> {
            boolean open = state.getValue(BigCityGateBlock.OPEN);
            Direction dir = state.getValue(BigCityGateBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(open ? gateOpen : gateClosed)
                    .rotationY((int) dir.toYRot())
                    .build();
        });

        simpleBlockItem(ModBlocks.BIG_CITY_GATE.get(), gateClosed);

        simpleBlock(ModBlocks.TAPESTRY.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_0.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_0.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_BURNED_0.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_BURNED_0.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_1.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_1.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_2.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_2.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_3.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_3.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_4.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_4.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_BURNED_1.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_BURNED_1.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_BURNED_2.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_BURNED_2.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_BURNED_3.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_BURNED_3.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlock(ModBlocks.TAPESTRY_CASTLE_BURNED_4.get(), models().getExistingFile(modLoc("block/tapestry")));
        simpleBlockItem(ModBlocks.TAPESTRY_CASTLE_BURNED_4.get(), models().getExistingFile(modLoc("block/tapestry")));

        simpleBlock(ModBlocks.ROYAL_BED.get(), models().getExistingFile(modLoc("block/royal_bed")));
        simpleBlockItem(ModBlocks.ROYAL_BED.get(), models().getExistingFile(modLoc("block/royal_bed")));

        ModelFile roofStraight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_clay_damaged"));
        ModelFile roofInnerLeft = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_clay_damaged_inner_left"));
        ModelFile roofInnerRight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_clay_damaged_inner_right"));
        ModelFile roofOuterLeft = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_clay_damaged_outer_left"));
        ModelFile roofOuterRight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_clay_damaged_outer_right"));

        getVariantBuilder(ModBlocks.ROOF_TILE_MEDIEVAL_CLAY_DAMAGED.get()).forAllStates(state -> {
            ModelFile model = switch (state.getValue(RoofTileBlock.SHAPE)) {
                case INNER_LEFT -> roofInnerLeft;
                case INNER_RIGHT -> roofInnerRight;
                case OUTER_LEFT -> roofOuterLeft;
                case OUTER_RIGHT -> roofOuterRight;
                default -> roofStraight;
            };
            int yRot = (int) state.getValue(RoofTileBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });

        ModelFile ironStraight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_iron_damaged"));
        ModelFile ironInnerLeft = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_iron_damaged_inner_left"));
        ModelFile ironInnerRight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_iron_damaged_inner_right"));
        ModelFile ironOuterLeft = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_iron_damaged_outer_left"));
        ModelFile ironOuterRight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_iron_damaged_outer_right"));

        getVariantBuilder(ModBlocks.ROOF_TILE_MEDIEVAL_IRON_DAMAGED.get()).forAllStates(state -> {
            ModelFile model = switch (state.getValue(RoofTileBlock.SHAPE)) {
                case INNER_LEFT -> ironInnerLeft;
                case INNER_RIGHT -> ironInnerRight;
                case OUTER_LEFT -> ironOuterLeft;
                case OUTER_RIGHT -> ironOuterRight;
                default -> ironStraight;
            };
            int yRot = (int) state.getValue(RoofTileBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });

        ModelFile copperStraight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_copper_damaged"));
        ModelFile copperInnerLeft = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_copper_damaged_inner_left"));
        ModelFile copperInnerRight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_copper_damaged_inner_right"));
        ModelFile copperOuterLeft = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_copper_damaged_outer_left"));
        ModelFile copperOuterRight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_copper_damaged_outer_right"));

        getVariantBuilder(ModBlocks.ROOF_TILE_MEDIEVAL_COPPER_DAMAGED.get()).forAllStates(state -> {
            ModelFile model = switch (state.getValue(RoofTileBlock.SHAPE)) {
                case INNER_LEFT -> copperInnerLeft;
                case INNER_RIGHT -> copperInnerRight;
                case OUTER_LEFT -> copperOuterLeft;
                case OUTER_RIGHT -> copperOuterRight;
                default -> copperStraight;
            };
            int yRot = (int) state.getValue(RoofTileBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });

        ModelFile largeTorchModel = models().getExistingFile(modLoc("block/large_torch"));
        getVariantBuilder(ModBlocks.LARGE_TORCH.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(largeTorchModel)
                    .rotationY((int) state.getValue(LargeTorchBlock.FACING).toYRot())
                    .build();
        });

        ModelFile denseWaterSurface = models().cubeAll("dense_water_surface", modLoc("block/dense_water_surface")).renderType("translucent");
        simpleBlockWithItem(ModBlocks.DENSE_WATER_SURFACE.get(), denseWaterSurface);

        ModelFile denseWaterMidHigh = models().cubeAll("dense_water_mid_high", modLoc("block/dense_water_mid_high")).renderType("translucent");
        simpleBlockWithItem(ModBlocks.DENSE_WATER_MID_HIGH.get(), denseWaterMidHigh);

        ModelFile denseWaterMidLow = models().cubeAll("dense_water_mid_low", modLoc("block/dense_water_mid_low")).renderType("translucent");
        simpleBlockWithItem(ModBlocks.DENSE_WATER_MID_LOW.get(), denseWaterMidLow);

        ModelFile denseWaterDeep = models().cubeAll("dense_water_deep", modLoc("block/dense_water_deep")).renderType("translucent");
        simpleBlockWithItem(ModBlocks.DENSE_WATER_DEEP.get(), denseWaterDeep);
        ModelFile capClay0 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_clay_damaged_0"));
        ModelFile capClay1 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_clay_damaged_1"));
        ModelFile capClay2 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_clay_damaged_2"));

        getVariantBuilder(ModBlocks.ROOF_CAP_MEDIEVAL_CLAY_DAMAGED.get()).forAllStates(state -> {
            ModelFile model = switch (state.getValue(RoofCapBlock.SHAPE)) {
                case 0 -> capClay0;
                case 1 -> capClay1;
                default -> capClay2;
            };
            int yRot = (int) state.getValue(RoofCapBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });
        ModelFile capIron0 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_iron_damaged_0"));
        ModelFile capIron1 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_iron_damaged_1"));
        ModelFile capIron2 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_iron_damaged_2"));

        getVariantBuilder(ModBlocks.ROOF_CAP_MEDIEVAL_IRON_DAMAGED.get()).forAllStates(state -> {
            ModelFile model = switch (state.getValue(RoofCapBlock.SHAPE)) {
                case 0 -> capIron0;
                case 1 -> capIron1;
                default -> capIron2;
            };
            int yRot = (int) state.getValue(RoofCapBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });
        ModelFile capCopper0 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_copper_damaged_0"));
        ModelFile capCopper1 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_copper_damaged_1"));
        ModelFile capCopper2 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_copper_damaged_2"));

        getVariantBuilder(ModBlocks.ROOF_CAP_MEDIEVAL_COPPER_DAMAGED.get()).forAllStates(state -> {
            ModelFile model = switch (state.getValue(RoofCapBlock.SHAPE)) {
                case 0 -> capCopper0;
                case 1 -> capCopper1;
                default -> capCopper2;
            };
            int yRot = (int) state.getValue(RoofCapBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });
        ModelFile throneModel = models().getExistingFile(modLoc("block/charred_throne"));
        getVariantBuilder(ModBlocks.CHARRED_THRONE.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(throneModel)
                    .rotationY((int) state.getValue(CharredThroneBlock.FACING).toYRot())
                    .build();
        });

        ModelFile paperLanternModel = models().getExistingFile(modLoc("block/paper_lantern"));
        getVariantBuilder(ModBlocks.PAPER_LANTERN.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(paperLanternModel)
                    .build();
        });

        ModelFile woodenChairModel = models().getExistingFile(modLoc("block/wooden_chair"));
        getVariantBuilder(ModBlocks.WOODEN_CHAIR.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(woodenChairModel)
                    .rotationY((int) state.getValue(WoodenChairBlock.FACING).toYRot())
                    .build();
        });

        ModelFile yukimiModel = models().getExistingFile(modLoc("block/yukimi_toro"));
        getVariantBuilder(ModBlocks.YUKIMI_TORO.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(yukimiModel)
                    .rotationY((int) state.getValue(YukimiToroBlock.FACING).toYRot())
                    .build();
        });

        ModelFile remainsModel = models().getExistingFile(modLoc("block/remains_guards"));
        getVariantBuilder(ModBlocks.REMAINS_GUARDS.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(remainsModel)
                    .rotationY((int) state.getValue(RemainsGuardsBlock.FACING).toYRot())
                    .build();
        });
        simpleBlockItem(ModBlocks.REMAINS_GUARDS.get(), remainsModel);

        ModelFile eggModel = models().getExistingFile(modLoc("block/petrified_large_egg"));
        simpleBlockWithItem(ModBlocks.PETRIFIED_LARGE_EGG.get(), eggModel);

        ModelFile tableclothModel = models().getExistingFile(modLoc("block/tablecloth_white"));
        simpleBlockWithItem(ModBlocks.TABLECLOTH_WHITE.get(), tableclothModel);

        ModelFile deepslateStraight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_deepslate_damaged"));
        ModelFile deepslateInnerLeft = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_deepslate_damaged_inner_left"));
        ModelFile deepslateInnerRight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_deepslate_damaged_inner_right"));
        ModelFile deepslateOuterLeft = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_deepslate_damaged_outer_left"));
        ModelFile deepslateOuterRight = models().getExistingFile(modLoc("block/roof_tile/roof_tile_medieval_deepslate_damaged_outer_right"));

        getVariantBuilder(ModBlocks.ROOF_TILE_MEDIEVAL_DEEPSLATE_DAMAGED.get()).forAllStates(state -> {
            ModelFile model = switch (state.getValue(RoofTileBlock.SHAPE)) {
                case INNER_LEFT -> deepslateInnerLeft;
                case INNER_RIGHT -> deepslateInnerRight;
                case OUTER_LEFT -> deepslateOuterLeft;
                case OUTER_RIGHT -> deepslateOuterRight;
                default -> deepslateStraight;
            };
            int yRot = (int) state.getValue(RoofTileBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });
        ModelFile capDeepslate0 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_deepslate_damaged_0"));
        ModelFile capDeepslate1 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_deepslate_damaged_1"));
        ModelFile capDeepslate2 = models().getExistingFile(modLoc("block/roof_tile/roof_cap_medieval_deepslate_damaged_2"));

        getVariantBuilder(ModBlocks.ROOF_CAP_MEDIEVAL_DEEPSLATE_DAMAGED.get()).forAllStates(state -> {
            ModelFile model = switch (state.getValue(RoofCapBlock.SHAPE)) {
                case 0 -> capDeepslate0;
                case 1 -> capDeepslate1;
                default -> capDeepslate2;
            };
            int yRot = (int) state.getValue(RoofCapBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        });
    }

    private void frostBlock(RegistryObject<Block> block) {
        models().withExistingParent(block.getId().getPath(), "block/snow_height2")
                .texture("particle", modLoc("block/frost"))
                .texture("texture", modLoc("block/frost"));
        getVariantBuilder(block.get()).forAllStates(state -> {
            int layers = state.getValue(SnowLayerBlock.LAYERS);
            return new ConfiguredModel[]{
                    new ConfiguredModel(models().getExistingFile(
                            modLoc("block/frost_height" + layers * 2)
                    ))
            };
        });}


    private void saplingBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }


    private void leavesBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(),
                                new ResourceLocation("minecraft:block/leaves"),
                                "all", blockTexture(blockRegistryObject.get()))
                        .renderType("cutout_mipped"));
    }
    private void logBlock(RotatedPillarBlock block, ResourceLocation sideTexture, ResourceLocation endTexture) {
        axisBlock(block, sideTexture, endTexture);
    }

    public void makeCrowberryShrub(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> crowberryStates(state, block, modelName, textureName);
        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] crowberryStates(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((CrowberryShrubBlock) block).getAgeProperty()),
                new ResourceLocation(fantastikmod.MOD_ID, "block/" + textureName + state.getValue(((CrowberryShrubBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    public void makeStrawberryCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> strawberryStates(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }


    private ConfiguredModel[] strawberryStates(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((StrawberryCropBlock) block).getAgeProperty()),
                new ResourceLocation(fantastikmod.MOD_ID, "block/" + textureName + state.getValue(((StrawberryCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    public void makeTeaCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> teaStates(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] teaStates(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((TeaCropBlock) block).getAgeProperty()),
                new ResourceLocation(fantastikmod.MOD_ID, "block/" + textureName + state.getValue(((TeaCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    public void makeRiceCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> riceStates(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] riceStates(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((RiceCropBlock) block).getAgeProperty()),
                new ResourceLocation(fantastikmod.MOD_ID, "block/" + textureName + state.getValue(((RiceCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    private int getXRotation(Direction dir) {
        return switch (dir) {
            case DOWN -> 180;
            case UP -> 0;
            default -> 90;
        };
    }

    private int getYRotation(Direction dir) {
        if (!dir.getAxis().isHorizontal()) return 0;
        return switch (dir) {
            case NORTH -> 0;
            case SOUTH -> 180;
            case WEST -> 270;
            case EAST -> 90;
            default -> 0;
        };
    }

    public void makeCatnipCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> catnipStates(state, block, modelName, textureName);
        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] catnipStates(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        int age = state.getValue(((CatnipCropBlock) block).getAgeProperty());
        models[0] = new ConfiguredModel(models().crop(modelName + age,
                new ResourceLocation(fantastikmod.MOD_ID, "block/" + textureName + age)).renderType("cutout"));
        return models;
    }

}