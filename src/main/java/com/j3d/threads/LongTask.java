package com.j3d.threads;

import com.j3d.Static;
import com.j3d.ui.util.Throbber;

import javax.swing.*;
import java.util.function.Consumer;

/**
 *  This class is a wrapper around a {@link Consumer<Throbber>} task that is expected to take a long time
 *  to complete. It displays a {@link Throbber} while the task is running and executes a clean-up
 *  task afterwards. The task is executed in a separate thread using a {@link SwingWorker} to
 *  prevent blocking the Event Dispatch Thread (EDT).
 */
public class LongTask {
    private final Consumer<Throbber> task;
    private final Consumer<Throbber> cleanup;


     public LongTask(Consumer<Throbber> task, Consumer<Throbber> cleanup) {
         this.task = task;
         this.cleanup = cleanup;
     }

     public void run() {
         Throbber throbber = new Throbber(Static.mainFrame, true);

         SwingWorker<Void, Void> worker = new SwingWorker<>() {
             @Override
             protected Void doInBackground() throws Exception {
                 task.accept(throbber);
                 return null;
             }

             @Override
             protected void done() {
                 try {
                     cleanup.accept(throbber);
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
