package com.j3d.errors;

import com.j3d.StaticRefs;
import com.j3d.errors.severity.J3DFatal;
import com.j3d.errors.severity.J3DMild;
import com.j3d.errors.severity.J3DWarning;
import com.j3d.errors.severity.J3Err;

import javax.swing.*;
import java.util.ArrayList;

public class ErrorHandler {
    private final ArrayList<Class<?>> ignored = new ArrayList<>();
    public <T extends J3DError> void ignore(Class<T> err) {
        if (ignored.size() >= 10) {
            throw new RuntimeException(
                    "Too many exceptions are being ignored... rethink strategy."
            );
        }
        ignored.add(err);
        StaticRefs.getLog().println(
                "[IGNORE-ERR(registered)] The next "
                + err.getSimpleName() +
                " Error has been registered to be ignored."
        );
    }
    public void handle(J3DError err) {
        if (!(err instanceof J3Err j3err)) return;
        String logHead = " " + j3err.logHead() + " ";
//        String msg = logHead+ err.getMessage() + " - " +  ( err.cause != null ? err.cause.getMessage() : "");

        if (ignored.contains(err.getClass())) {
            ignored.remove(err.getClass());
            StaticRefs.getLog().println(
                    "[IGNORE-ERR(consumed)] " + err.getClass().getSimpleName()
                    + " has been consumed."
            );
            return;
        }
        switch (err) {
            case J3DMild j3m -> {
                // mild errors only get printed to the log as the user need not know of this.
                StaticRefs.getLog().error(err);
            }
            case J3DWarning j3w -> {
                // Warnings get printed to the log, user debug log and also a little box to the user.
                StaticRefs.getLog().error(err);
                JOptionPane.showMessageDialog(StaticRefs.getMainFrame(), err.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            }
            case J3DFatal j3f -> {
                // Fatal errors are unrecoverable.
                StaticRefs.getLog().error(err);
                JOptionPane.showMessageDialog(StaticRefs.getMainFrame(), err.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
                if (j3f.terminate()) {
                    JOptionPane.showMessageDialog(StaticRefs.getMainFrame(),
                            "Due to the nature of the previous error, the app cannot continue in this state and will shut down.",
                            "Fatal Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
//                    throw err;
                }
            }
            default ->  {
                StaticRefs.getLog().error(err);
            }
        }
    }
}
