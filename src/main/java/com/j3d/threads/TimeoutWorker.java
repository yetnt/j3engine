package com.j3d.threads;

import javax.swing.SwingWorker;
import java.util.concurrent.TimeUnit;

/**
 * A SwingWorker that acts as a countdown timer, executing a callback upon completion.
 * <p>
 * This worker runs a countdown from a specified start time on a background thread.
 * If the timer completes without being cancelled, it executes a provided {@link Runnable}
 * on the Event Dispatch Thread (EDT).
 * </p>
 *
 * @author Lehlogonolo Poole
 * @see SwingWorker
 */
public class TimeoutWorker extends SwingWorker<Void, Void> {

    private final int startTimeInSeconds;
    private final Runnable onFinishedCallback;

    /**
     * Constructs a new TimeoutWorker.
     *
     * @param startTimeInSeconds The number of seconds to count down from.
     * @param onFinishedCallback The {@link Runnable} to execute on the EDT only if the timer finishes successfully.
     */
    public TimeoutWorker(int startTimeInSeconds, Runnable onFinishedCallback) {
        this.startTimeInSeconds = startTimeInSeconds;
        this.onFinishedCallback = onFinishedCallback;
    }

    /**
     * The main background task that performs the countdown.
     * <p>
     * This method runs on a worker thread. It sleeps for one-second intervals and
     * checks for cancellation on each iteration.
     * </p>
     * @return null, as this worker does not produce a final result.
     */
    @Override
    protected Void doInBackground() throws Exception {
        try {
            for (int i = startTimeInSeconds; i > 0; i--) {
                // Check if the task has been cancelled before sleeping.
                if (isCancelled()) {
                    return null;
                }
                // Wait for one second.
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return null;
    }

    /**
     * Called on the Event Dispatch Thread (EDT) after the background task finishes.
     * <p>
     * This method checks if the worker was cancelled. If it was not, it executes
     * the success callback.
     */
    @Override
    protected void done() {
        if (!isCancelled()) {
            onFinishedCallback.run();
        }
    }
}
