package com.j3d.threads;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A background thread that collects and reports statistics to the Swing Event Dispatch Thread (EDT) once per second.
 * <p>
 * This class uses a {@link SwingWorker} to perform a long-running task (collecting stats) on a background
 * thread, preventing the UI from freezing. It safely publishes the collected data to the EDT for UI updates.
 * <p>
 * The statistics are stored as thread-safe {@link AtomicInteger}s, allowing any outside class
 * to increment them at any time without causing concurrency issues.
 *
 * @see SwingWorker
 * @see AtomicInteger
 */
public class StatisticsThread extends SwingWorker<Void, Map<UUID, Integer>> {

    /**
     * A thread-safe map to store the named counters. Outside classes can access these counters
     * to increment them from any thread.
     */
    private final Map<UUID, AtomicInteger> stats = new HashMap<>();

    /**
     * The consumer that will receive the statistics map on the EDT for UI updates.
     */
    private final Consumer<Map<UUID, Integer>> uiUpdater;

    /**
     * Constructs a new StatisticsThread.
     *
     * @param uiUpdater A {@link Consumer} that will be executed on the EDT each second,
     *                  receiving a map of the latest statistics to update the UI.
     */
    public StatisticsThread(Consumer<Map<UUID, Integer>> uiUpdater) {
        this.uiUpdater = uiUpdater;
    }

    /**
     * Registers a new statistic to be tracked.
     *
     * @return The stat's UUID.
     */
    public UUID registerStatistic() {
        UUID id = UUID.randomUUID();
        stats.put(id, new AtomicInteger(0));
        return id;
    }

    /**
     * Registers a new statistic to be tracked.
     *
     * @param id The id of the new statistic.
     */
    public void registerStatistic(UUID id) {
        stats.put(id, new AtomicInteger(0));
    }

    /**
     * Atomically increments the value of a registered statistic by one.
     * This method is thread-safe and can be called from any thread.
     *
     * @param id The id of the statistic to increment.
     */
    public void increment(UUID id) {
        AtomicInteger counter = stats.get(id);
        if (counter != null) {
            counter.incrementAndGet();
        }
    }

    /**
     * The main background task. This method runs on a worker thread, not the EDT.
     * <p>
     * It enters a loop that, once per second, captures the current value of all statistic
     * counters, resets them to zero, and then {@code publish}es the captured values
     * to the {@link #process} method.
     *
     * @return null, as this worker does not return a final result.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {
            // Sleep for 1 second
            Thread.sleep(1000);

            // Atomically get the current value and reset each counter to 0
            Map<UUID, Integer> currentStats = stats.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().getAndSet(0)
                    ));

            // Publish the captured data to the EDT
            publish(currentStats);
        }
        return null;
    }

    /**
     * Receives data chunks from the {@code publish} method and executes on the EDT.
     * <p>
     * This method is guaranteed to run on the Event Dispatch Thread, making it safe
     * to update Swing components. It calls the UI updater with the latest statistics.
     *
     * @param chunks A list of statistics maps published from the background thread.
     *               In this implementation, it will typically contain one map per second.
     */
    @Override
    protected void process(List<Map<UUID, Integer>> chunks) {
        // Get the latest map from the chunk
        if (!chunks.isEmpty()) {
            Map<UUID, Integer> latestStats = chunks.get(chunks.size() - 1);
            uiUpdater.accept(latestStats);
        }
    }

    public enum IdEnum {

        REPAINTS_PER_SECOND(UUID.randomUUID());

        private final UUID id;
        IdEnum(UUID id) {
            this.id = id;
        }
        public UUID getId() {
            return id;
        }
    }
}
