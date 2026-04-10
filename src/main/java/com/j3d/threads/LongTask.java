package com.j3d.threads;

import com.j3d.Static;
import com.j3d.ui.dialog.Throbber;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *  This class is a wrapper around a {@link Consumer<Throbber>} task that is expected to take a long time
 *  to complete. It displays a {@link Throbber} while the task is running and executes a clean-up
 *  task afterwards. The task is executed in a separate thread using a {@link SwingWorker} to
 *  prevent blocking the Event Dispatch Thread (EDT).
 *
 * @param <T> The type of the result returned by the long-running task.
 * @author Lehlogonolo Poole
 */
public class LongTask<T> {
    private final Function<Throbber, T> task;
    private final BiConsumer<Throbber, T> cleanup;

     public LongTask(Function<Throbber, T> task, BiConsumer<Throbber, T> cleanup) {
         this.task = task;
         this.cleanup = cleanup;
     }

     public void run() {
         Throbber throbber = new Throbber(Static.mainFrame, true);

         SwingWorker<Void, Void> worker = new SwingWorker<>() {
             T o;
             @Override
             protected Void doInBackground() throws Exception {
                 o = task.apply(throbber);

                 return null;
             }

             @Override
             protected void done() {
                 try {
                     cleanup.accept(throbber, o);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
                 throbber.dispose();
                 Static.mainFrame.repaint();
             }
         };

         worker.execute();
         throbber.setVisible(true);
     }
}
