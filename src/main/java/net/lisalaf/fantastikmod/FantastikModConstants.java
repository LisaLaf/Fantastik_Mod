package net.lisalaf.fantastikmod;

import net.minecraft.core.Direction;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FantastikModConstants {
    public static final Direction[] DIRECTIONS = Direction.values();

    public static final ScheduledExecutorService DIALOG_SCHEDULER = Executors.newScheduledThreadPool(1, r -> {
        Thread thread = new Thread(r, "Dialog-Scheduler");
        thread.setDaemon(true);
        return thread;
    });
}
