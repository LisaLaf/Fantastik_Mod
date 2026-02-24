package net.lisalaf.fantastikmod.worldgen.feature;

import com.mojang.serialization.Codec;
import net.lisalaf.fantastikmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class MoonCrystalClusterFeature extends Feature<NoneFeatureConfiguration> {

    public MoonCrystalClusterFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        BlockPos startPos = findGround(level, origin, 10);
        if (startPos == null) return false;

        int height = 5 + random.nextInt(6); // 5-10 блоков
        int baseSize = 2 + random.nextInt(3); // 2-4 блока радиус основания

        generateSpike(level, startPos, height, baseSize, random);
        fillGaps(level, startPos, height, baseSize, random);
        addCrystals(level, startPos, height, random);

        return true;
    }

    private BlockPos findGround(WorldGenLevel level, BlockPos origin, int maxChecks) {
        BlockPos.MutableBlockPos pos = origin.mutable();

        for (int i = 0; i < maxChecks; i++) {
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && !state.is(Blocks.WATER) && !state.is(Blocks.LAVA) &&
                    state.isSolid() && level.isEmptyBlock(pos.above())) {
                return pos.above().immutable();
            }
            pos.move(0, -1, 0);
        }
        return null;
    }

    private void generateSpike(WorldGenLevel level, BlockPos startPos, int height, int baseSize, RandomSource random) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int y = 0; y < height; y++) {
            // Размер уменьшается к верху
            int size = baseSize - (y * baseSize) / height;
            if (size < 1) size = 1;

            BlockPos layerPos = startPos.above(y);

            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    // Не заполняем полностью - оставляем дыры
                    if (random.nextFloat() < 0.3f) continue;

                    // Дальше от центра - меньше шанс
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist > size - 0.5 && random.nextFloat() < 0.5f) continue;

                    pos.set(layerPos.getX() + dx, layerPos.getY(), layerPos.getZ() + dz);

                    if (level.isEmptyBlock(pos) || canReplace(level.getBlockState(pos))) {
                        setBlock(level, pos, ModBlocks.MOON_CRYSTAL_BLOCK.get().defaultBlockState());
                    }
                }
            }
        }
    }

    private void fillGaps(WorldGenLevel level, BlockPos startPos, int height, int baseSize, RandomSource random) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Заполняем пустоты под кластером
        for (int y = -1; y >= -3; y--) {
            for (int dx = -baseSize-1; dx <= baseSize+1; dx++) {
                for (int dz = -baseSize-1; dz <= baseSize+1; dz++) {
                    pos.set(startPos.getX() + dx, startPos.getY() + y, startPos.getZ() + dz);

                    // Если сверху есть блок кластера
                    if (level.getBlockState(pos.above()).is(ModBlocks.MOON_CRYSTAL_BLOCK.get())) {
                        if (level.isEmptyBlock(pos) && random.nextFloat() < 0.7f) {
                            setBlock(level, pos, ModBlocks.MOON_CRYSTAL_BLOCK.get().defaultBlockState());
                        }
                    }
                    // Если рядом есть земля
                    else {
                        for (Direction dir : Direction.values()) {
                            if (level.getBlockState(pos.relative(dir)).isSolid()) {
                                if (random.nextFloat() < 0.3f) {
                                    setBlock(level, pos, ModBlocks.MOON_CRYSTAL_BLOCK.get().defaultBlockState());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void addCrystals(WorldGenLevel level, BlockPos startPos, int height, RandomSource random) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Кристаллы на основном пике
        for (int y = 0; y < height; y++) {
            BlockPos layerPos = startPos.above(y);
            float chance = 0.2f + (y / (float)height) * 0.2f; // Больше шанс наверху

            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    pos.set(layerPos.getX() + dx, layerPos.getY(), layerPos.getZ() + dz);

                    if (level.getBlockState(pos).is(ModBlocks.MOON_CRYSTAL_BLOCK.get())) {
                        if (random.nextFloat() < chance) {
                            tryPlaceCrystal(level, pos, random);
                        }
                    }
                }
            }
        }

        // Кристаллы вокруг основания
        BlockPos groundPos = startPos.below();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) continue;

                pos.set(groundPos.getX() + dx, groundPos.getY() + 1, groundPos.getZ() + dz);

                if (random.nextFloat() < 0.15f) {
                    if (level.isEmptyBlock(pos) && level.getBlockState(pos.below()).isSolid()) {
                        BlockState crystal = ModBlocks.MOON_CRYSTAL.get().defaultBlockState()
                                .setValue(net.minecraft.world.level.block.AmethystClusterBlock.FACING, Direction.UP)
                                .setValue(net.minecraft.world.level.block.AmethystClusterBlock.WATERLOGGED, false);
                        setBlock(level, pos, crystal);
                    }
                }
            }
        }
    }

    private void tryPlaceCrystal(WorldGenLevel level, BlockPos blockPos, RandomSource random) {
        Direction dir = Direction.values()[random.nextInt(6)];
        if (dir == Direction.DOWN) return;

        BlockPos crystalPos = blockPos.relative(dir);

        if (level.isEmptyBlock(crystalPos) && !isWaterNearby(level, crystalPos)) {
            // Простая проверка - не ставить если уже есть кристалл рядом
            for (Direction checkDir : Direction.values()) {
                if (level.getBlockState(crystalPos.relative(checkDir)).getBlock() == ModBlocks.MOON_CRYSTAL.get()) {
                    return;
                }
            }

            BlockState crystal = ModBlocks.MOON_CRYSTAL.get().defaultBlockState()
                    .setValue(net.minecraft.world.level.block.AmethystClusterBlock.FACING, dir)
                    .setValue(net.minecraft.world.level.block.AmethystClusterBlock.WATERLOGGED, false);
            setBlock(level, crystalPos, crystal);
        }
    }

    private boolean isWaterNearby(WorldGenLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(Blocks.WATER)) {
                return true;
            }
        }
        return false;
    }

    private boolean canReplace(BlockState state) {
        return state.isAir() ||
                state.is(Blocks.GRASS) ||
                state.is(Blocks.TALL_GRASS) ||
                state.is(Blocks.SNOW) ||
                state.is(Blocks.STONE) ||
                state.is(Blocks.DIRT) ||
                state.is(Blocks.GRAVEL) ||
                state.is(Blocks.SAND);
    }
}