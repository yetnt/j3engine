package com.j3d.threads;

import com.j3d.Static;
import com.j3d.engine.SceneManager;
import com.j3d.engine.interact.cmd.commands.measure.VolumeCmd;

import javax.swing.*;
import java.util.UUID;
import java.util.function.Consumer;

import static com.j3d.Static.sceneManager;

/**
 * A SwingWorker implementation designed to perform a blinking effect on a UI component.
 * It repeatedly executes a given {@link Runnable}, repainting the main panel,
 * with a specified delay between repetitions. After the blinking sequence completes,
 * it removes an overlap identified by a UUID from the scene manager.
 * @see VolumeCmd
 * @see SceneManager#scheduleOverlap(UUID, Consumer)
 * @author Lehlogonolo Poole
 */
public class BlinkerSwingWorker extends SwingWorker<Void, Void> {

    private final long waitMs;
    private final int repeatAmtTimes;
    private final Runnable runnable;
    private final UUID idToRemove;

    /**
     * Constructs a new BlinkerSwingWorker.
     *
     * @param waitMs The delay in milliseconds between each blink cycle (repaint, run, repaint).
     * @param repeatAmtTimes The number of times the blink cycle should be repeated.
     * @param set The {@link Runnable} to be executed during each blink cycle. This typically
     *            toggles the visibility or state of the element to be blinked.
     * @param uuid The {@link UUID} of the overlap to be removed from the scene manager
     *             once the blinking sequence is complete.
     */
    public BlinkerSwingWorker(long waitMs, int repeatAmtTimes, Runnable set, UUID uuid) {
        super();
        this.waitMs = waitMs;
        this.repeatAmtTimes = repeatAmtTimes;
        this.runnable = set;
        this.idToRemove = uuid;

    }

    /**
     * Executes the blinking sequence.
     * It loops `repeatAmtTimes` times, performing the following steps in each iteration:
     * <ol>
     *      <li>Sleeps for {@code waitMs} milliseconds.</li>
     *      <li>Executes the provided {@code runnable}.</li>
     *      <li>Repaints the {@code Static.mainPanel}.</li>
     * </ol>
     * If the thread is interrupted during sleep, the exception is caught and ignored.
     */
    private void repeat() {
        for (int i = 0; i < repeatAmtTimes; i++) {
            try {
                Thread.sleep(waitMs);
                runnable.run();
                Static.mainPanel.repaint();
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * The main task to be executed in the background by this SwingWorker.
     * It first calls the {@link #repeat()} method to perform the blinking,
     * and then removes the specified overlap from the scene manager.
     * @return {@code null} as this worker does not produce any intermediate results or a final result.
     */
    @Override
    protected Void doInBackground() throws Exception {
        repeat();
        sceneManager.removeOverlap(idToRemove);
        return null;
    }
}
