package net.lisalaf.fantastikmod.worldgen.feature;

import net.lisalaf.fantastikmod.FantastikModConstants;
import net.lisalaf.fantastikmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class MoonCrystalClusterFeature extends Feature<NoneFeatureConfiguration> {

    private final Block moonBlock;
    private final BlockState moonBlockState;

    public MoonCrystalClusterFeature() {
        super(NoneFeatureConfiguration.CODEC);
        this.moonBlock = ModBlocks.MOON_CRYSTAL_BLOCK.get();
        this.moonBlockState = moonBlock.defaultBlockState();
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
            int size = baseSize - (y * baseSize) / height;
            if (size < 1) size = 1;

            /*
                Заранее считаем квадрат порога для проверки расстояния
             */
            float threshold = (size - 0.5f) * (size - 0.5f);

            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    if (random.nextFloat() < 0.3f) continue;

                    int distSq = dx * dx + dz * dz;
                    if (distSq > threshold && random.nextFloat() < 0.5f) continue;

                    pos.set(startPos.getX() + dx, startPos.getY() + y, startPos.getZ() + dz);

                    if (level.isEmptyBlock(pos) || canReplace(level.getBlockState(pos))) {
                        level.setBlock(pos, moonBlockState, 2);
                    }
                }
            }
        }
    }

    private void fillGaps(WorldGenLevel level, BlockPos startPos, int height, int baseSize, RandomSource random) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        /*
            Заводим второй MutableBlockPos для проверок соседей, чтобы не создавать новые объекты
         */
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int y = -1; y >= -3; y--) {
            for (int dx = -baseSize - 1; dx <= baseSize + 1; dx++) {
                for (int dz = -baseSize - 1; dz <= baseSize + 1; dz++) {
                    pos.set(startPos.getX() + dx, startPos.getY() + y, startPos.getZ() + dz);

                    checkPos.setWithOffset(pos, 0, 1, 0); // Проверяем блок сверху
                    if (level.getBlockState(checkPos).is(moonBlock)) {
                        if (level.isEmptyBlock(pos) && random.nextFloat() < 0.7f) {
                            setBlock(level, pos, moonBlockState);
                        }
                    } else {
                        /*
                            Быстрая итерация по массиву без создания объекта Iterator
                         */
                        final Direction[] directions = FantastikModConstants.DIRECTIONS;
                        for (int i = 0; i < directions.length; i++) {
                            if (level.getBlockState(checkPos.setWithOffset(pos, directions[i])).isSolid()) {
                                if (random.nextFloat() < 0.3f) {
                                    setBlock(level, pos, moonBlockState);
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
            float chance = 0.2f + (y / (float) height) * 0.2f; // Больше шанс наверху

            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    pos.set(layerPos.getX() + dx, layerPos.getY(), layerPos.getZ() + dz);

                    if (level.getBlockState(pos).is(moonBlock)) {
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
        Direction dir = Direction.getRandom(random);
        if (dir == Direction.DOWN) return;

        BlockPos.MutableBlockPos crystalPos = blockPos.mutable().move(dir);

        if (level.isEmptyBlock(crystalPos) && !isWaterNearby(level, crystalPos)) {
            BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

            for (Direction checkDir : Direction.values()) {
                checkPos.setWithOffset(crystalPos, checkDir);
                if (level.getBlockState(checkPos).is(ModBlocks.MOON_CRYSTAL.get())) {
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
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        final Direction[] values = FantastikModConstants.DIRECTIONS;
        for (int i = 0; i < values.length; i++) {
            if (level.getBlockState(checkPos.setWithOffset(pos, values[i])).is(Blocks.WATER)) {
                return true;
            }
        }
        return false;
    }

    private boolean canReplace(BlockState state) {
        // Лучше использовать теги, чтобы мод был совместим с другими генераторами биомов
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