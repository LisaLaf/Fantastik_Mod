package net.lisalaf.fantastikmod.worldgen.feature;

import com.mojang.serialization.Codec;
import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.entity.custom.IceDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DragonCaveFeature extends Feature<NoneFeatureConfiguration> {

    public DragonCaveFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();


        if (origin.getY() > 30) {
            return false;
        }

        if (level.getBlockState(origin).getBlock() == Blocks.WATER) {
            return false;
        }

        int radius = 50 + random.nextInt(25);
        int depth = 25 + random.nextInt(15);

        // ОТКРЫТАЯ пещера-провал
        // Сначала создаем пещеру
        int baseHeight = -9; // Опускаем на 10 блоков (1 - 10 = -9)
        int floorHeight = 8;
        int newDepth = depth + 10; // Увеличиваем высоту на 10

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < newDepth; y++) { // Используем новую высоту
                    BlockPos pos = origin.offset(x, baseHeight + y, z);
                    double distance = Math.sqrt(x * x + z * z);

                    if (distance <= radius) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 10);
                    }
                }
            }
        }


// Пол тоже поднимаем
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos bottomPos = origin.offset(x, floorHeight, z);
                double distance = Math.sqrt(x * x + z * z);

                if (distance <= radius && distance > 7) {
                    double normalizedDist = distance / radius;
                    int bowlDepth = (int)((1.0 - normalizedDist * normalizedDist) * 10);
                    int actualDepth = bowlDepth + random.nextInt(3) - 1;
                    BlockPos actualBottom = bottomPos.below(actualDepth);

                    // Создаем блок пола
                    level.setBlock(actualBottom, Blocks.STONE.defaultBlockState(), 3);

                    // Сразу заполняем вниз от этого блока
                    for (int y = 1; y <= 10; y++) {
                        BlockPos fillPos = actualBottom.below(y);
                        if (random.nextFloat() < 0.8f) {
                            level.setBlock(fillPos, Blocks.STONE.defaultBlockState(), 3);
                        }
                    }

                    if (distance > 5 && distance < radius - 2 && random.nextFloat() < 0.3f) {
                        level.setBlock(actualBottom, Blocks.ICE.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Затем заполняем ВНИЗ только под созданными блоками пола
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos bottomPos = origin.offset(x, floorHeight, z);
                double distance = Math.sqrt(x * x + z * z);

                if (distance <= radius && distance > 7) {
                    // Ищем самый нижний созданный блок пола в этом столбце
                    int lowestBlockY = floorHeight;
                    for (int y = floorHeight; y >= floorHeight - 15; y--) {
                        BlockPos checkPos = new BlockPos(bottomPos.getX(), y, bottomPos.getZ());
                        if (!level.getBlockState(checkPos).isAir()) {
                            lowestBlockY = y;
                            break;
                        }
                    }

                    // Заполняем вниз от самого нижнего блока пола
                    for (int y = lowestBlockY - 1; y >= baseHeight; y--) {
                        BlockPos fillPos = new BlockPos(bottomPos.getX(), y, bottomPos.getZ());
                        if (random.nextFloat() < 0.9f) { // 90% заполнения
                            level.setBlock(fillPos, Blocks.STONE.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }



// Гнездо (тоже поднимаем на floorHeight)
        int nestRadius = 19;
        for (int x = -nestRadius; x <= nestRadius; x++) {
            for (int z = -nestRadius; z <= nestRadius; z++) {
                BlockPos bottomPos = origin.offset(x, floorHeight, z);
                double distance = Math.sqrt(x * x + z * z);

                if (distance <= nestRadius) {
                    double normalizedDist = distance / nestRadius;
                    int bowlDepth = (int)((1.0 - normalizedDist * normalizedDist) * 8); // 0-8 блоков глубины кратера
                    int actualDepth = bowlDepth + random.nextInt(3) - 1;
                    BlockPos actualBottom = bottomPos.below(actualDepth);

                    // КРАТЕР: верхние слои - воздух
                    for (int y = 0; y < actualDepth; y++) {
                        level.setBlock(bottomPos.below(y), Blocks.AIR.defaultBlockState(), 3);
                    }

                    // ДНО кратера - снег
                    level.setBlock(actualBottom, Blocks.SNOW_BLOCK.defaultBlockState(), 3);

                    // ЗАПОЛНЕНИЕ ВНИЗ от дна кратера (как у пола)
                    for (int y = 1; y <= 10; y++) {
                        BlockPos fillPos = actualBottom.below(y);
                        if (random.nextFloat() < 0.8f) {
                            // 80% снега, 20% плотного льда
                            if (random.nextFloat() < 0.8f) {
                                level.setBlock(fillPos, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                            } else {
                                level.setBlock(fillPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
                            }
                        }
                    }

                    // Лёд на склонах кратера
                    if (distance > 3 && distance < nestRadius - 1 && random.nextFloat() < 0.4f) {
                        level.setBlock(actualBottom, Blocks.ICE.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Добавляем блоки иния на поверхность гнезда
        for (int x = -nestRadius; x <= nestRadius; x++) {for (int z = -nestRadius; z <= nestRadius; z++) {BlockPos surfacePos = origin.offset(x, floorHeight, z);double distance = Math.sqrt(x * x + z * z);

                if (distance <= nestRadius - 3) { if (level.getBlockState(surfacePos).isAir() && level.getBlockState(surfacePos.below()).getBlock() == Blocks.SNOW_BLOCK) {

                        // Ставим блок иния с шансом
                        if (random.nextFloat() < 0.7f) { level.setBlock(surfacePos.below(), ModBlocks.FROST.get().defaultBlockState(), 3);}}}}}


        // Ледяные стены


        for (int x = -radius - 20; x <= radius + 20; x++) {
            for (int z = -radius - 5; z <= radius + 5; z++) {
                for (int y = -5; y < depth + 15; y++) {
                    BlockPos pos = origin.offset(x, baseHeight + y, z);
                    double distance = Math.sqrt(x * x + z * z);

                    // Удаляем воду в радиусе пещеры + 5 блоков
                    if (distance <= radius + 3) {
                        if (level.getBlockState(pos).getBlock() == Blocks.WATER ||
                                level.getBlockState(pos).getBlock() == Blocks.WATER) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < newDepth; y++) { // ← ИСПРАВИЛ: newDepth вместо depth
                    BlockPos pos = origin.offset(x, baseHeight + y, z);
                    double distance = Math.sqrt(x * x + z * z);

                    if (distance >= radius - 2 && distance <= radius) {
                        if (random.nextFloat() < 0.4f) {
                            level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                        } else if (random.nextFloat() < 0.3f) {
                            level.setBlock(pos, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        if (!level.isClientSide()) {
            // Спавним дракона в центре гнезда
            BlockPos spawnPos = origin.above(floorHeight + 2); // На 2 блока выше пола гнезда

            // Проверяем что место для спавна свободно
            if (level.isEmptyBlock(spawnPos) && level.isEmptyBlock(spawnPos.above())) {

                // Создаем и спавним дракона
                IceDragonEntity dragon = new IceDragonEntity(ModEntities.ICE_DRAGON.get(), level.getLevel());
                dragon.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

                // Устанавливаем случайный поворот
                dragon.setYRot(random.nextFloat() * 360.0F);

                // Добавляем в мир
                level.addFreshEntity(dragon);

                System.out.println("Dragon spawned at: " + spawnPos);
            }
        }


        return true;
    }


}