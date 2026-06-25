package com.j3d.threads;

import com.j3d.Static;
import com.j3d.errors.ErrorHandler;
import com.j3d.ui.dialog.Spinner;
import com.j3d.utility.generic.TriConsumer;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *  This class is a wrapper around a {@link Consumer<Spinner>} task that is expected to take a long time
 *  to complete. It displays a {@link Spinner} while the task is running and executes a clean-up
 *  task afterwards. The task is executed in a separate thread using a {@link SwingWorker} to
 *  prevent blocking the Event Dispatch Thread (EDT).
 *
 * @param <T> The type of the result returned by the long-running task.
 * @author Lehlogonolo Poole
 */
public class LongTask<T> {
    private final Function<Spinner, T> task;
    private final TriConsumer<Spinner, T, Boolean> cleanup;
    private final Consumer<Exception> onErr;

     public LongTask(Function<Spinner, T> task, TriConsumer<Spinner, T, Boolean> cleanup) {
         this.task = task;
         this.cleanup = cleanup;
         this.onErr = e -> ErrorHandler.handle(
                 new LongTaskException("Task could not complete due to an unrecoverable error", e)
         );
     }

    public LongTask(Function<Spinner, T> task, TriConsumer<Spinner, T, Boolean> cleanup, Consumer<Exception> onErr) {
        this.task = task;
        this.cleanup = cleanup;
        this.onErr = onErr;
    }

     public void run() {
         Spinner throbber = new Spinner(Static.mainFrame, true);

         SwingWorker<Void, Void> worker = new SwingWorker<>() {
             T o;
             @Override
             protected Void doInBackground() throws Exception {
                 try {
                     o = task.apply(throbber);
                 } catch (Exception e) {
                     onErr.accept(e);
                 }

                 return null;
             }

             @Override
             protected void done() {
                 try {
                     cleanup.accept(throbber, o, isDone());
                 } catch (Exception e) {
                     ErrorHandler.handle(
                             new LongTaskException("Task could not complete due to an unrecoverable error", e)
                     );
                 }
                 throbber.dispose();
                 Static.mainFrame.repaint();
             }
         };

         worker.execute();
         throbber.setVisible(true);
     }
}
