package net.lisalaf.fantastikmod.block.custom.entity;

import net.lisalaf.fantastikmod.block.ModBlockEntities;
import net.lisalaf.fantastikmod.block.custom.DryingBasketBlock;
import net.lisalaf.fantastikmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DryingBasketBlockEntity extends BlockEntity {

    /**
     * Константы для управления слотами и параметрами сушки
     */
    private static final int INPUT_SLOT = 0;        // Слот для входного сырья (чайные листья)
    private static final int OUTPUT_SLOT = 1;       // Слот для готового продукта (высушенный чай)
    private static final int MAX_INPUT_SIZE = 1;    // Максимальное количество листьев, которое можно положить за раз
    private static final int MAX_OUTPUT_SIZE = 64;  // Максимальный размер стака для готового продукта
    private static final int MAX_DRYING_TIME = 600; // Время сушки в тиках (600 тиков = 30 секунд при 20 TPS)

    /**
     * Обработчик инвентаря корзины.
     * Содержит 2 слота: вход и выход.
     */
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            // Помечаем BlockEntity как измененный для сохранения данных
            setChanged();
            if (level != null) {
                /*
                 * Обновляем состояние блока (визуальное отображение процесса сушки).
                 * Если входной слот не пуст, значит процесс идет.
                 */
                boolean isDrying = !getStackInSlot(INPUT_SLOT).isEmpty();
                BlockState state = level.getBlockState(worldPosition);
                // Меняем свойство DRYING только если оно действительно изменилось, чтобы избежать лишних перерисовок
                if (state.hasProperty(DryingBasketBlock.DRYING) && state.getValue(DryingBasketBlock.DRYING) != isDrying) {
                    level.setBlock(worldPosition, state.setValue(DryingBasketBlock.DRYING, isDrying), 3);
                }
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            /*
             * Валидация предметов для слотов:
             * В первый слот (INPUT) разрешаем класть только чайные листья.
             */
            if (slot == INPUT_SLOT) return stack.getItem() == ModItems.TEA_LEAF.get();

            /*
             * Во второй слот (OUTPUT) вручную или через автоматику (воронки) класть ничего нельзя.
             */
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            // Для входа лимит 1 предмет, для выхода стандартные 64
            return slot == INPUT_SLOT ? MAX_INPUT_SIZE : MAX_OUTPUT_SIZE;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            /*
             * Ограничение извлечения:
             * Запрещаем автоматике (например, воронкам снизу) забирать сырье из входного слота.
             * Можно забирать только готовый результат из выходного слота.
             */
            if (slot == INPUT_SLOT) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    };

    /**
     * Обертка инвентаря для Forge Capability системы.
     * Позволяет другим блокам/сущностям взаимодействовать с инвентарем.
     */
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventory);
    
    /**
     * Текущий прогресс сушки в тиках.
     */
    private int dryingProgress = 0;

    public DryingBasketBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_BASKET.get(), pos, state);
    }

    /**
     * Проверяет, пуста ли корзина (оба слота).
     *
     * @return true, если в корзине нет ни входных, ни выходных предметов.
     */
    public boolean isEmpty() {
        return inventory.getStackInSlot(INPUT_SLOT).isEmpty() && inventory.getStackInSlot(OUTPUT_SLOT).isEmpty();
    }

    /**
     * Извлекает предметы из входного слота.
     * Используется для логики, где нужно программно забрать сырье.
     *
     * @return ItemStack извлеченного предмета.
     */
    public ItemStack extractItem() {
        return this.inventory.extractItem(0, MAX_INPUT_SIZE, false);
    }

    /**
     * Основной метод обновления состояния на сервере. Вызывается каждый тик.
     *
     * @param level Уровень, в котором находится блок.
     * @param pos Позиция блока.
     * @param state Текущее состояние блока.
     * @param entity Экземпляр BlockEntity корзины.
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, DryingBasketBlockEntity entity) {
        ItemStack input = entity.inventory.getStackInSlot(INPUT_SLOT);

        // Если во входном слоте есть листья, увеличиваем прогресс
        if (!input.isEmpty()) {
            entity.dryingProgress++;
            // Если прогресс достиг максимума, превращаем листья в чай
            if (entity.dryingProgress >= MAX_DRYING_TIME) {
                entity.processDrying();
            }
        } else {
            // Если слот пуст, сбрасываем прогресс
            entity.dryingProgress = 0;
        }
    }

    /**
     * Логика превращения сырых листьев в готовый продукт.
     */
    private void processDrying() {
        ItemStack input = inventory.getStackInSlot(INPUT_SLOT);
        ItemStack currentOutput = inventory.getStackInSlot(OUTPUT_SLOT);

        /*
         * Случай 1: Выходной слот пуст.
         * Создаем новый стак готового чая в количестве, равном количеству входных листьев.
         */
        if (currentOutput.isEmpty()) {
            inventory.setStackInSlot(OUTPUT_SLOT, new ItemStack(ModItems.TEA_LEAFS_GREEN.get(), input.getCount()));
            inventory.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
            dryingProgress = 0;
            playSound();
        }
        /*
         * Случай 2: В выходном слоте уже есть такой же чай и есть место для новых предметов.
         */
        else if (currentOutput.getItem() == ModItems.TEA_LEAFS_GREEN.get() && currentOutput.getCount() + input.getCount() <= MAX_OUTPUT_SIZE) {
            currentOutput.grow(input.getCount()); // Увеличиваем размер существующего стака
            inventory.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY); // Очищаем вход
            dryingProgress = 0;
            playSound();
        }

        /*
         * Если выходной слот полон или занят другим предметом, процесс останавливается.
         * TODO: Реализовать выбрасывание излишков в мир, если выход забит.
         */
    }

    /**
     * Воспроизводит звук завершения процесса сушки.
     */
    private void playSound() {
        if (level != null) {
            level.playSound(
                    null,
                    worldPosition,
                    SoundEvents.BAMBOO_BREAK,
                    SoundSource.BLOCKS,
                    0.8f,
                    1.2f
            );
        }
    }

    /**
     * Обрабатывает взаимодействие игрока с корзиной для извлечения предметов.
     * Приоритет отдается готовому продукту.
     *
     * @return ItemStack, который получает игрок.
     */
    public ItemStack playerExtract() {
        // Сначала пытаемся отдать готовый чай
        ItemStack outputStack = inventory.getStackInSlot(OUTPUT_SLOT);
        if (!outputStack.isEmpty()) {
            ItemStack extracted = outputStack.copy();
            inventory.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
            return extracted;
        }

        // Если чая нет, забираем сырые листья (например, если игрок передумал сушить)
        ItemStack inputStack = inventory.getStackInSlot(INPUT_SLOT);
        if (!inputStack.isEmpty()) {
            ItemStack extracted = inputStack.copy();
            inventory.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
            return extracted;
        }

        return ItemStack.EMPTY;
    }

    /**
     * Обрабатывает вкладывание предмета игроком в корзину.
     *
     * @param stack Предмет в руке игрока.
     * @return Измененный ItemStack (с уменьшенным количеством).
     */
    public ItemStack playerInsert(ItemStack stack) {
        ItemStack toInsert = stack.copy();
        toInsert.setCount(MAX_INPUT_SIZE); // Ограничиваем вкладывание одной единицей

        ItemStack leftover = inventory.insertItem(INPUT_SLOT, toInsert, false);

        // Если предмет успешно вложен (нет остатка), уменьшаем стак в руке игрока
        if (leftover.isEmpty()) {
            stack.shrink(MAX_INPUT_SIZE);
        }
        return stack;
    }

    /**
     * @return Прямой доступ к инвентарю корзины.
     */
    public IItemHandler getInventory() {
        return inventory;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("progress", dryingProgress);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        dryingProgress = tag.getInt("progress");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return handler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }

}