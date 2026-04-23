package net.lisalaf.fantastikmod.block.custom;

import net.lisalaf.fantastikmod.FantastikModConstants;
import net.lisalaf.fantastikmod.block.ModBlockEntities;
import net.lisalaf.fantastikmod.block.custom.entity.DryingBasketBlockEntity;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.lisalaf.fantastikmod.item.ModItems;
import net.lisalaf.fantastikmod.util.FantastikModUtils;
import net.lisalaf.fantastikmod.villager.ModVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class DryingBasketBlock extends BaseEntityBlock {
    /**
     * Свойство состояния блока, отвечающее за визуализацию процесса сушки.
     */
    public static final BooleanProperty DRYING = BooleanProperty.create("drying");
    
    /**
     * Радиус поиска сущностей для запуска диалога.
     */
    private static final int DIALOG_RADIUS = 6;

    public DryingBasketBlock(Properties properties) {
        super(properties);
        // По умолчанию корзина не сушит
        this.registerDefaultState(this.stateDefinition.any().setValue(DRYING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DRYING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        // Используем стандартную модель из json
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DryingBasketBlockEntity(pos, state);
    }

    /**
     * Обработка нажатия ПКМ по блоку.
     */
    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ) {
        // Логика выполняется только на сервере
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof DryingBasketBlockEntity basket)) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);

        /*
         * Логика извлечения предметов:
         * Если рука пуста ИЛИ в выходном слоте есть готовый продукт, пытаемся его забрать.
         */
        if (stack.isEmpty() || !basket.getInventory().getStackInSlot(1).isEmpty()) {
            if (!basket.isEmpty()) {
                ItemStack extracted = basket.playerExtract();
                // Если инвентарь игрока полон, выбрасываем предмет в мир
                if (!player.getInventory().add(extracted)) {
                    player.drop(extracted, false);
                }
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8f, 1.2f);
                return InteractionResult.SUCCESS;
            } else {
                // Если корзина пуста, пытаемся запустить диалог с ближайшим Мастером Чая или Кицунэ
                startDialog((ServerLevel) level, player, pos);
                return InteractionResult.SUCCESS;
            }
        }

        /*
         * Логика вкладывания предметов:
         * Если игрок держит чайный лист, пытаемся положить его в корзину.
         */
        if (stack.getItem() == ModItems.TEA_LEAF.get()) {
            int before = stack.getCount();
            ItemStack resultStack = basket.playerInsert(stack);

            // Если количество предметов в руке изменилось, значит вкладывание прошло успешно
            if (resultStack.getCount() != before) {
                level.playSound(null, pos, SoundEvents.BAMBOO_PLACE, SoundSource.BLOCKS, 0.8f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    /**
     * Ищет подходящих NPC в радиусе и запускает диалог.
     */
    private void startDialog(ServerLevel level, Player player, BlockPos pos) {
        AABB bounds = new AABB(pos).inflate(DIALOG_RADIUS);

        /*
            Ищем всех нужных существ за один проход. Это будет быстрее чем постоянно дёргать `level.getEntities`
         */
        final Entity[] findEntities = FantastikModUtils.findFirstOfClasses(
                level, bounds, Villager.class, KitsuneLightEntity.class, MoonDeerEntity.class
        );

        if(findEntities.length == 0) return;

        // Ищем Мастера Чая
        final Entity villager = findEntities[0];
        if(villager != null && ((Villager)villager).getVillagerData().getProfession() == ModVillagers.TEA_MASTER.get()) {
            playDelayedDialog(level, player, "dialog.fantastikmod.drying.teamaster", 2);
            return;
        }

        // Ищем прирученную Кицунэ
        final Entity kitsune = findEntities[1];
        if(kitsune != null && ((KitsuneLightEntity)kitsune).isTamed()) {
            playDelayedDialog(level, player, "dialog.fantastikmod.drying.kitsune", 4);
            return;
        }

        // Ищем прирученного Лунного Оленя
        final Entity moonDeer = findEntities[2];
        if(moonDeer != null && ((MoonDeerEntity)moonDeer).isTamed()) {
            playDelayedDialog(level, player, "dialog.fantastikmod.drying.moon_deer", 2);
        }
    }

    /**
     * Элегантный метод для воспроизведения многострочных диалогов с задержкой.
     * Использует фоновый планировщик, чтобы не блокировать основной поток сервера во время ожидания.
     *
     * @param baseKey Базовый ключ локализации (например: "dialog.fantastikmod.kitsune")
     * @param linesCount Общее количество строк диалога в файле локализации
     */
    private void playDelayedDialog(
            ServerLevel level, Player player, String baseKey, int linesCount
    ) {
        int delaySeconds = 4; // Интервал между сообщениями

        for (int i = 0; i < linesCount; i++) {
            if (!(player instanceof ServerPlayer serverPlayer)) continue;

            final int lineIndex = i + 1; // Индекс строки для ключа (.1, .2 и т.д.)
            int currentDelay = i * delaySeconds;

            /*
             * Первую строку отправляем мгновенно.
             * Остальные планируем через DIALOG_SCHEDULER.
             */
            if (currentDelay == 0) {
                serverPlayer.sendSystemMessage(Component.translatable(baseKey + "." + lineIndex));
            } else {
                FantastikModConstants.DIALOG_SCHEDULER.schedule(() -> {
                    /*
                     * Важно: отправка сообщения должна происходить в главном потоке сервера,
                     * поэтому используем level.getServer().execute().
                     */
                    level.getServer().execute(() -> {
                        /*
                         * Перед отправкой проверяем, жив ли еще игрок и находится ли он в сети,
                         * так как за время задержки ситуация могла измениться.
                         */
                        if (serverPlayer.isAlive() && !serverPlayer.hasDisconnected()) {
                            serverPlayer.sendSystemMessage(Component.translatable(baseKey + "." + lineIndex));
                        }
                    });
                }, currentDelay, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void onRemove(
            BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving
    ) {
        // Если блок действительно удален (а не просто изменилось состояние)
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DryingBasketBlockEntity basket) {
                // Выбрасываем содержимое инвентаря на землю
                FantastikModUtils.dropInventoryItems(level, pos, basket.getInventory());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    /**
     * Регистрирует тикер для обработки логики сушки на сервере.
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type
    ) {
        /*
         * Тикер работает только на серверной стороне.
         * Используем вспомогательный метод Forge для привязки тикера к нашему BlockEntity.
         */
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.DRYING_BASKET.get(), DryingBasketBlockEntity::serverTick);
    }
}