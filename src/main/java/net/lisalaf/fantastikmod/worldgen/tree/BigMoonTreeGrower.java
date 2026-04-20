package net.lisalaf.fantastikmod.worldgen.tree;

import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.block.custom.MoonSaplingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Класс, отвечающий за генерацию и выращивание Большого Лунного Дерева.
 * Содержит логику построения сложной структуры: ствола, корней, ветвей и листвы. <br>
 * TODO: Стоит оптимизировать аллокацию памяти из-за BlockPos замена на MutableBlockPos или перейти на примитивы
 */
public class BigMoonTreeGrower {

    /**
     * Безопасно устанавливает блок в мире.
     * Использует флаг 19 (1 | 2 | 16), который:
     * 1 - вызывает обновление блока
     * 2 - отправляет изменения игрокам (клиенту)
     * 16 - предотвращает лишние обновления соседей
     * Это важно для сохранения производительности сервера.
     *
     * @param level Уровень (мир), в котором ставим блок
     * @param pos Координаты установки
     * @param state Тип и состояние устанавливаемого блока
     */
    private static void setTreeBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 19);
    }

    /**
     * Пытается вырастить дерево из саженцев (вызывается при клике костной мукой или случайном росте).
     * Проверяет условия: нет ли воды под ногами, стоят ли саженцы 2x2 и есть ли место для роста.
     *
     * @param level Мир игры
     * @param pos Позиция саженца
     * @param random Источник случайности
     * @return true, если дерево успешно выросло
     */
    public static boolean growBigMoonTree(LevelAccessor level, BlockPos pos, RandomSource random) {
        if (!checkWaterBelow(level, pos)) return false;
        if (!checkSaplings(level, pos)) return false;

        int height = 32 + random.nextInt(8);

        if (!checkSpace(level, pos, height)) {
            return false;
        }

        // Очищаем саженцы только когда убедились, что есть место
        removeSaplings(level, pos);

        // Строим дерево
        buildTree(level, pos, height, random);
        return true;
    }

    /**
     * Генерирует дерево при создании нового мира.
     * В отличие от роста из саженцев, здесь не проверяется наличие самих саженцев.
     *
     * @param level Мир при генерации
     * @param pos Позиция генерации
     * @param random Источник случайности
     * @return true, если дерево успешно создано
     */
    public static boolean generateBigMoonTree(WorldGenLevel level, BlockPos pos, RandomSource random) {
        if (!checkWaterBelow(level, pos)) return false;

        int height = 32 + random.nextInt(8);

        if (!checkSpaceForWorldGen(level, pos, height)) {
            return false;
        }

        // Строим дерево
        buildTree(level, pos, height, random);
        return true;
    }

    /**
     * Основной "сборочный" метод. Последовательно строит все части дерева.
     *
     * @param level Мир игры
     * @param pos Базовая позиция (основание ствола)
     * @param height Высота основного ствола
     * @param random Источник случайности
     */
    private static void buildTree(LevelAccessor level, BlockPos pos, int height, RandomSource random) {
        generateTrunk(level, pos, height, random);
        generateRoots(level, pos, random);
        generateBranches(level, pos, height, random);
        generateVerticalBranches(level, pos, height, random);
        generateCanopy(level, pos, height, random);
    }

    /**
     * Проверяет, нет ли воды под областью 2x2, где должно стоять дерево.
     * Лунные деревья не любят расти прямо в воде.
     *
     * @param level Мир игры
     * @param pos Позиция основания
     * @return true, если воды под деревом нет
     */
    private static boolean checkWaterBelow(LevelAccessor level, BlockPos pos) {
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                if (level.getFluidState(pos.offset(x, -1, z)).is(FluidTags.WATER)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Проверяет, стоят ли в области 2x2 именно наши Лунные Саженцы.
     *
     * @param level Мир игры
     * @param pos Позиция (один из углов квадрата 2x2)
     * @return true, если все 4 блока — подходящие саженцы
     */
    private static boolean checkSaplings(LevelAccessor level, BlockPos pos) {
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                if (!(level.getBlockState(pos.offset(x, 0, z)).getBlock() instanceof MoonSaplingBlock)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Удаляет саженцы перед тем, как на их месте вырастет ствол.
     *
     * @param level Мир игры
     * @param pos Позиция основания
     */
    private static void removeSaplings(LevelAccessor level, BlockPos pos) {
        final BlockState airBlockState = Blocks.AIR.defaultBlockState();
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                setTreeBlock(level, pos.offset(x, 0, z), airBlockState);
            }
        }
    }

    /**
     * Упрощенная проверка свободного места для генератора мира.
     * Проверяет вертикальную зону, где будет основной ствол.
     *
     * @param level Мир при генерации
     * @param pos Позиция основания
     * @param height Высота дерева
     * @return true, если место достаточно свободно
     */
    private static boolean checkSpaceForWorldGen(WorldGenLevel level, BlockPos pos, int height) {
        for (int y = 0; y <= height + 3; y++) {
            for (int x = -1; x <= 2; x++) {
                for (int z = -1; z <= 2; z++) {
                    BlockState state = level.getBlockState(pos.offset(x, y, z));
                    if (!state.isAir() && !state.canBeReplaced()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Тщательная проверка свободного места при росте из саженцев.
     * Проверяет как ствол, так и зону будущей кроны, чтобы дерево не "врезалось" в горы или постройки.
     *
     * @param level Мир игры
     * @param pos Позиция основания
     * @param height Планируемая высота
     * @return true, если места достаточно
     */
    private static boolean checkSpace(LevelAccessor level, BlockPos pos, int height) {
        final Block moonSapling = ModBlocks.MOON_SAPLING.get();
        for (int y = 0; y <= height + 3; y++) {
            for (int x = -1; x <= 2; x++) {
                for (int z = -1; z <= 2; z++) {
                    BlockState state = level.getBlockState(pos.offset(x, y, z));
                    if (!state.isAir() && !state.is(moonSapling) && !state.canBeReplaced()) {
                        return false;
                    }
                }
            }
        }

        int crownStart = height - 12;
        int radius = 8;
        for (int y = crownStart; y <= height + 4; y++) {
            for (int x = -radius; x <= radius + 2; x++) {
                for (int z = -radius; z <= radius + 2; z++) {
                    if (!level.getBlockState(pos.offset(x, y, z)).isAir() && y > height - 3) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Создает ствол дерева.
     * Ствол состоит из основной колонны 2x2 и небольшого утолщения у земли.
     *
     * @param level Мир игры
     * @param pos Позиция основания
     * @param height Высота ствола
     * @param random Источник случайности
     */
    private static void generateTrunk(LevelAccessor level, BlockPos pos, int height, RandomSource random) {
        final BlockState logState = ModBlocks.TREE_MOON_LOG_BLOCK.get().defaultBlockState();
        final Block moonSapling = ModBlocks.MOON_SAPLING.get();

        // Цикл по высоте дерева
        for (int y = 0; y <= height; y++) {
            // Ствол 2x2 блока
            for (int x = 0; x < 2; x++) {
                for (int z = 0; z < 2; z++) {
                    BlockPos trunkPos = pos.offset(x, y, z);
                    BlockState state = level.getBlockState(trunkPos);
                    // Заменяем только воздух или саженцы
                    if (state.isAir() || state.is(moonSapling)) {
                        setTreeBlock(level, trunkPos, logState);
                    }
                }
            }
        }

        // Создаем утолщение у основания (юбка дерева)
        for (int y = -2; y <= 3; y++) {
            for (int x = -1; x <= 2; x++) {
                for (int z = -1; z <= 2; z++) {
                    // Пропускаем углы, чтобы утолщение было более округлым
                    if ((x == -1 || x == 2) && (z == -1 || z == 2)) continue;
                    BlockPos thickPos = pos.offset(x, y, z);
                    if (level.getBlockState(thickPos).isAir()) {
                        setTreeBlock(level, thickPos, logState);
                    }
                }
            }
        }
        // Небольшой шпиль на самой вершине ствола
        for (int y = height + 1; y <= height + 2; y++) {
            BlockPos topPos = pos.offset(0, y, 0);
            if (level.getBlockState(topPos).isAir()) {
                setTreeBlock(level, topPos, logState);
            }
        }
    }

    /**
     * Генерирует корни дерева, расходящиеся в разные стороны.
     * Корни могут заменять землю и траву.
     *
     * @param level Мир игры
     * @param pos Позиция основания
     * @param random Источник случайности
     */
    private static void generateRoots(LevelAccessor level, BlockPos pos, RandomSource random) {
        BlockState woodState = ModBlocks.TREE_MOON_WOOD.get().defaultBlockState();
        int rootLength = 6 + random.nextInt(4);
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

        // Основные корни в 4 стороны (север, юг, запад, восток)
        for (Direction dir : directions) {
            for (int i = 1; i <= rootLength; i++) {
                // Плавное понижение корня по мере удаления от ствола
                int yOffset = (i == 1) ? 0 : (i >= 6) ? -3 : (i >= 4) ? -2 : -1;

                BlockPos rootPos = pos.offset(dir.getStepX() * i, yOffset, dir.getStepZ() * i);
                BlockState state = level.getBlockState(rootPos);
                // Корень может расти сквозь землю или воздух
                if (state.isAir() || state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) {
                    setTreeBlock(level, rootPos, woodState);
                }

                // Утолщение корня в самом начале (возле ствола)
                if (i == 1) {
                    for (int w = -1; w <= 1; w++) {
                        for (int h = -1; h <= 0; h++) {
                            if (w == 0 && h == 0) continue;
                            BlockPos thickPos = rootPos.offset(w, h, w);
                            if (level.getBlockState(thickPos).isAir()) {
                                setTreeBlock(level, thickPos, woodState);
                            }
                        }
                    }
                }
            }

            // Дополнительные маленькие ответвления от корней в стороны
            for (int i = 2; i <= rootLength - 1; i++) {
                if (random.nextFloat() < 0.6f) {
                    Direction sideDir = random.nextBoolean() ? dir.getClockWise() : dir.getCounterClockWise();
                    for (int s = 1; s <= 2; s++) {
                        BlockPos sideRoot = pos.offset(dir.getStepX() * i + sideDir.getStepX() * s, -1 - (s/2), dir.getStepZ() * i + sideDir.getStepZ() * s);
                        if (level.getBlockState(sideRoot).isAir()) {
                            setTreeBlock(level, sideRoot, woodState);
                        }
                    }
                }
            }
        }

        // Диагональные корни
        int[] diagX = {1, 1, -1, -1};
        int[] diagZ = {1, -1, 1, -1};

        for (int d = 0; d < 4; d++) {
            for (int i = 1; i <= 5; i++) {
                int yOffset = (i == 1) ? 0 : (i >= 4) ? -2 : -1;
                BlockPos rootPos = pos.offset(diagX[d] * i, yOffset, diagZ[d] * i);
                BlockState state = level.getBlockState(rootPos);
                if (state.isAir() || state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) {
                    setTreeBlock(level, rootPos, woodState);
                }
            }
        }

        // Вертикальные стержневые корни (уходят прямо вниз под стволом)
        for (int x = -2; x <= 3; x++) {
            for (int z = -2; z <= 3; z++) {
                if (random.nextFloat() < 0.5f) {
                    int depth = 2 + random.nextInt(4);
                    for (int d = 1; d <= depth; d++) {
                        BlockPos rootPos = pos.offset(x, -d, z);
                        BlockState state = level.getBlockState(rootPos);
                        if (state.isAir() || state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) {
                            setTreeBlock(level, rootPos, woodState);
                        }
                    }
                }
            }
        }
    }

    /**
     * Создает большие горизонтальные ветви, отходящие от ствола.
     * Каждая ветка заканчивается пышным скоплением листьев.
     *
     * @param level Мир игры
     * @param pos Позиция основания
     * @param height Высота дерева
     * @param random Источник случайности
     */
    private static void generateBranches(LevelAccessor level, BlockPos pos, int height, RandomSource random) {
        BlockState logState = ModBlocks.TREE_MOON_LOG_BLOCK.get().defaultBlockState();
        int branchCount = 14 + random.nextInt(8);
        int branchStartY = height - 14;

        for (int i = 0; i < branchCount; i++) {
            int branchHeight = branchStartY + random.nextInt(12);
            Direction branchDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int branchLength = 5 + random.nextInt(5);

            if (branchHeight < height - 14) continue;

            BlockPos startPos = pos.offset(0, branchHeight, 0);

            for (int l = 1; l <= branchLength; l++) {
                int yOffset = (l > 5) ? -2 : (l > 3) ? -1 : 0;

                BlockPos branchPos = startPos.offset(branchDir.getStepX() * l, yOffset + (random.nextInt(2) - 1), branchDir.getStepZ() * l);
                if (level.getBlockState(branchPos).isAir()) {
                    setTreeBlock(level, branchPos, logState);
                }

                if (l > 2 && random.nextFloat() < 0.5f) {
                    Direction sideDir = random.nextBoolean() ? branchDir.getClockWise() : branchDir.getCounterClockWise();
                    int subLength = 2 + random.nextInt(3);
                    for (int s = 1; s <= subLength; s++) {
                        BlockPos subPos = branchPos.offset(sideDir.getStepX() * s, (s > 1 ? -1 : 0), sideDir.getStepZ() * s);
                        if (level.getBlockState(subPos).isAir()) {
                            setTreeBlock(level, subPos, logState);
                            if (s == subLength) placeLeafCluster(level, subPos, random);
                        }
                    }
                }
            }

            BlockPos endPos = startPos.offset(branchDir.getStepX() * branchLength, -1, branchDir.getStepZ() * branchLength);
            placeLeafCluster(level, endPos, random);
        }
    }

    /**
     * Генерирует вертикальные и диагональные ветви по бокам ствола.
     * Это придает дереву более естественный и объемный вид.
     *
     * @param level Мир игры
     * @param pos Позиция основания
     * @param height Высота дерева
     * @param random Источник случайности
     */
    private static void generateVerticalBranches(LevelAccessor level, BlockPos pos, int height, RandomSource random) {
        BlockState logState = ModBlocks.TREE_MOON_LOG_BLOCK.get().defaultBlockState();
        int verticalBranchCount = 6 + random.nextInt(5);
        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

        for (int i = 0; i < verticalBranchCount; i++) {
            Direction startDir = dirs[random.nextInt(dirs.length)];
            int startY = 6 + random.nextInt(15);
            int endY = height - 10 + random.nextInt(8);

            if (startY >= endY) continue;

            BlockPos currentPos = pos.offset(startDir.getStepX(), startY, startDir.getStepZ());

            // Если место занято (например, основным стволом), пропускаем
            if (!level.getBlockState(currentPos).isAir()) continue;

            // Растем вверх, иногда немного смещаясь в стороны
            for (int y = startY; y <= endY; y++) {
                int xOffset = startDir.getStepX();
                int zOffset = startDir.getStepZ();

                if (random.nextFloat() < 0.3f) {
                    Direction sideDir = random.nextBoolean() ? startDir.getClockWise() : startDir.getCounterClockWise();
                    xOffset += sideDir.getStepX();
                    zOffset += sideDir.getStepZ();
                }

                BlockPos branchPos = pos.offset(xOffset, y, zOffset);
                if (level.getBlockState(branchPos).isAir()) {
                    setTreeBlock(level, branchPos, logState);
                    currentPos = branchPos;

                    // На кончике ветки вешаем листву
                    if (y > endY - 4 && random.nextFloat() < 0.6f) {
                        placeSmallLeafCluster(level, branchPos, random);
                    }
                } else {
                    break;
                }
            }

            placeSmallLeafCluster(level, currentPos, random);
        }

        // Диагональные ветки, растущие под углом вверх
        int diagonalBranchCount = 8 + random.nextInt(6);

        for (int i = 0; i < diagonalBranchCount; i++) {
            int startY = 8 + random.nextInt(18);
            if (startY > height - 15) continue;

            Direction branchDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int branchLength = 4 + random.nextInt(5);

            BlockPos startPos = pos.offset(0, startY, 0);

            for (int l = 1; l <= branchLength; l++) {
                // Плавный подъем по Y (на 1 блок вверх каждые 2 блока длины)
                int yOffset = 1 + (l / 2);
                BlockPos branchPos = startPos.offset(branchDir.getStepX() * l, yOffset, branchDir.getStepZ() * l);
                if (level.getBlockState(branchPos).isAir()) {
                    setTreeBlock(level, branchPos, logState);

                    // Иногда ветка раздваивается
                    if (random.nextFloat() < 0.4f && l > 2) {
                        Direction sideDir = random.nextBoolean() ? branchDir.getClockWise() : branchDir.getCounterClockWise();
                        BlockPos sidePos = branchPos.offset(sideDir.getStepX(), 0, sideDir.getStepZ());
                        if (level.getBlockState(sidePos).isAir()) {
                            setTreeBlock(level, sidePos, logState);
                        }
                    }
                }
            }

            BlockPos endPos = startPos.offset(branchDir.getStepX() * branchLength, branchLength / 2, branchDir.getStepZ() * branchLength);
            placeSmallLeafCluster(level, endPos, random);
        }
    }

    /**
     * Строит основную массивную крону из листьев в верхней части дерева.
     * Использует математическую проверку расстояния для создания округлой формы.
     *
     * @param level Мир игры
     * @param pos Позиция основания
     * @param height Высота дерева
     * @param random Источник случайности
     */
    private static void generateCanopy(LevelAccessor level, BlockPos pos, int height, RandomSource random) {
        BlockState foliageState = ModBlocks.TREE_MOON_FOLIAGE_BLOCK.get().defaultBlockState();
        int crownStart = height - 12;
        int maxRadius = 7;

        for (int y = crownStart; y <= height + 2; y++) {
            // Радиус кроны уменьшается к верхушке (форма купола)
            int radius = (y <= height - 3) ? maxRadius : (y <= height) ? maxRadius - 1 : (y == height + 1) ? maxRadius - 2 : maxRadius - 3;
            if (radius < 1) radius = 1;

            double maxDistSq = (radius + 0.5) * (radius + 0.5);

            for (int x = -radius; x <= radius + 1; x++) {
                for (int z = -radius; z <= radius + 1; z++) {
                    BlockPos leafPos = pos.offset(x, y, z);

                    // Пропускаем место, где проходит основной ствол
                    if (x >= 0 && x <= 1 && z >= 0 && z <= 1 && y <= height) {
                        continue;
                    }

                    // Проверка расстояния от центра для создания круга
                    int distSq = x * x + z * z;
                    if (distSq <= maxDistSq) {
                        // Края кроны делаем менее плотными для естественности
                        float density = (Math.abs(x) > radius - 1 || Math.abs(z) > radius - 1) ? 0.6f : 0.85f;
                        if (random.nextFloat() < density && level.getBlockState(leafPos).isAir()) {
                            setTreeBlock(level, leafPos, foliageState);
                        }
                    }
                }
            }
        }

        // Самая верхняя "шапка" листвы
        for (int y = height + 1; y <= height + 4; y++) {
            int radius = Math.max(0, 4 - (y - height - 1));
            double maxDistSq = (radius + 0.5) * (radius + 0.5);

            for (int x = -radius; x <= radius + 1; x++) {
                for (int z = -radius; z <= radius + 1; z++) {
                    int distSq = x * x + z * z;
                    if (distSq <= maxDistSq) {
                        BlockPos leafPos = pos.offset(x, y, z);
                        if (level.getBlockState(leafPos).isAir()) {
                            setTreeBlock(level, leafPos, foliageState);
                        }
                    }
                }
            }
        }

        // Добавляем "свисающие" клочья листвы по краям нижней части кроны
        double minOuterDistSq = (maxRadius - 0.5) * (maxRadius - 0.5);
        double maxOuterDistSq = (maxRadius + 0.5) * (maxRadius + 0.5);

        for (int y = crownStart - 1; y <= crownStart + 2; y++) {
            for (int x = -maxRadius - 1; x <= maxRadius + 2; x++) {
                for (int z = -maxRadius - 1; z <= maxRadius + 2; z++) {
                    int distSq = x * x + z * z;
                    if (distSq >= minOuterDistSq && distSq <= maxOuterDistSq) {
                        if (random.nextFloat() < 0.45f) {
                            BlockPos leafPos = pos.offset(x, y, z);
                            if (level.getBlockState(leafPos).isAir()) {
                                setTreeBlock(level, leafPos, foliageState);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Создает "шар" из листьев вокруг указанной точки.
     * Используется для оформления концов веток.
     *
     * @param level Мир игры
     * @param pos Центр скопления листьев
     * @param random Источник случайности
     */
    private static void placeLeafCluster(LevelAccessor level, BlockPos pos, RandomSource random) {
        BlockState foliageState = ModBlocks.TREE_MOON_FOLIAGE_BLOCK.get().defaultBlockState();
        int clusterSize = 3;
        double maxDistSq = (clusterSize + 0.5) * (clusterSize + 0.5);

        for (int x = -clusterSize; x <= clusterSize; x++) {
            for (int z = -clusterSize; z <= clusterSize; z++) {
                for (int y = -2; y <= 2; y++) {
                    // Замена Math.sqrt
                    int distSq = x * x + y * y + z * z;
                    if (distSq <= maxDistSq && random.nextFloat() < 0.65f) {
                        BlockPos leafPos = pos.offset(x, y, z);
                        if (level.getBlockState(leafPos).isAir()) {
                            setTreeBlock(level, leafPos, foliageState);
                        }
                    }
                }
            }
        }
    }

    /**
     * Создает маленькое скопление листьев.
     * Используется для тонких вертикальных веточек.
     *
     * @param level Мир игры
     * @param pos Центр скопления
     * @param random Источник случайности
     */
    private static void placeSmallLeafCluster(LevelAccessor level, BlockPos pos, RandomSource random) {
        BlockState foliageState = ModBlocks.TREE_MOON_FOLIAGE_BLOCK.get().defaultBlockState();
        int clusterSize = 2;
        double maxDistSq = (clusterSize + 0.5) * (clusterSize + 0.5);

        for (int x = -clusterSize; x <= clusterSize; x++) {
            for (int z = -clusterSize; z <= clusterSize; z++) {
                for (int y = -1; y <= 1; y++) {
                    // Замена Math.sqrt
                    int distSq = x * x + y * y + z * z;
                    if (distSq <= maxDistSq && random.nextFloat() < 0.7f) {
                        BlockPos leafPos = pos.offset(x, y, z);
                        if (level.getBlockState(leafPos).isAir()) {
                            setTreeBlock(level, leafPos, foliageState);
                        }
                    }
                }
            }
        }
    }
}