package net.lisalaf.fantastikmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class FantastikModUtils {

    public static void dropInventoryItems(Level level, BlockPos pos, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    /**
     *
     * <p> Версия без {@code Predicate<Entity>} <br>
     * Возвращает массив сущностей, где индекс совпадает с индексом запрошенного класса. <br>
     * Если сущность не найдена, в ячейке будет null. </p>
     */
    @SafeVarargs
    public static Entity[] findFirstOfClasses(Level level, AABB bounds, Class<? extends Entity>... targetClasses) {
        Entity[] results = new Entity[targetClasses.length];
        final int[] foundCount = {0}; // Счетчик найденных

        if(!(level instanceof ServerLevel serverLevel)) return results;

        final LevelEntityGetter<Entity> entitiesMap = serverLevel.getEntities();
        entitiesMap.get(EntityTypeTest.forClass(Entity.class), bounds, entity -> {
            for (int i = 0; i < targetClasses.length; i++) {
                if (results[i] == null && targetClasses[i].isInstance(entity)) {
                    results[i] = entity;
                    foundCount[0]++;

                    if (foundCount[0] == targetClasses.length) {
                        return AbortableIterationConsumer.Continuation.ABORT;
                    }
                }
            }


            return AbortableIterationConsumer.Continuation.CONTINUE;
        });

        return results;
    }

    /**
     * Возвращает массив сущностей, где индекс совпадает с индексом запрошенного класса.
     * Если сущность не найдена, в ячейке будет null.
     */
    @SafeVarargs
    public static Entity[] findFirstOfClasses(Level level, AABB bounds, Predicate<Entity> predicate, Class<? extends Entity>... targetClasses) {
        Entity[] results = new Entity[targetClasses.length];
        final int[] foundCount = {0}; // Счетчик найденных

        if(!(level instanceof ServerLevel serverLevel)) return results;

        final LevelEntityGetter<Entity> entitiesMap = serverLevel.getEntities();
        entitiesMap.get(EntityTypeTest.forClass(Entity.class), bounds, entity -> {
            for (int i = 0; i < targetClasses.length; i++) {
                if (results[i] == null && targetClasses[i].isInstance(entity)) {
                    if (predicate.test(entity)) {
                        results[i] = entity;
                        foundCount[0]++;

                        if (foundCount[0] == targetClasses.length) {
                            return AbortableIterationConsumer.Continuation.ABORT;
                        }
                    }
                    break;
                }
            }


            return AbortableIterationConsumer.Continuation.CONTINUE;
        });

        return results;
    }
}
