package com.j3d.threads;

import javax.swing.*;

/**
 * Executes the same as {@link LongTask} but is simply for convention.
 * A FakeLongTask is to be used when you want to simulate something taking long
 * when in reality it wont.
 * <p>
 *     The primary use case is for when you open the engine to display the splash
 *     text while the actual app opens on the EDT.
 * </p>
 * @author Lehlogonolo Poole
 * @see LongTask
 */
public class FakeLongTask {
    private final Runnable task;
    private final Runnable taskFinished;
    private final Runnable onEDT;
    private final double EDTwaitSeconds;


    public FakeLongTask(Runnable task, Runnable taskFinished, Runnable onEDT, double EDTwaitSeconds) {
        this.task = task;
        this.taskFinished = taskFinished;
        this.onEDT = onEDT;
        this.EDTwaitSeconds = EDTwaitSeconds;
    }

    public void run() throws InterruptedException {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                task.run(); // background task
                return null;
            }

            @Override
            protected void done() {
                taskFinished.run(); // background finished callback
            }
        };

        // Start the background task
        worker.execute();

        // Schedule the EDT code to run after delay, without blocking
        Timer t = new Timer((int) (EDTwaitSeconds * 1000), e -> {
            onEDT.run();
        });
        t.setRepeats(false);
        t.start();
    }

    public void iAmImpatient() {
        onEDT.run();
    }
}
