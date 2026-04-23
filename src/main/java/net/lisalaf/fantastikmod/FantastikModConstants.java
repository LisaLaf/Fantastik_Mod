package net.lisalaf.fantastikmod;

import net.minecraft.core.Direction;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FantastikModConstants {

    // Статическое поле с уже собранным массивом направлений для предотвращения постоянного создания объектов
    public static final Direction[] DIRECTIONS = Direction.values();

    // Отдельный поток для диалогов, чтобы создавать задержку между сообщениями
    public static final ScheduledExecutorService DIALOG_SCHEDULER = Executors.newScheduledThreadPool(1, r -> {
        Thread thread = new Thread(r, "Dialog-Scheduler");
        thread.setDaemon(true);
        return thread;
    });
}
