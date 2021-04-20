package me.akiradev.oasiscapes.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor {
    private static ExecutorService executor;

    public static void init() {
        executor = Executors.newSingleThreadExecutor();
    }

    public static void execute(Runnable task) {
        executor.execute(task);
    }
}
