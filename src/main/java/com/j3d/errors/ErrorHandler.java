package com.j3d.errors;

import com.j3d.Static;
import com.j3d.errors.severity.J3DFatal;
import com.j3d.errors.severity.J3DMild;
import com.j3d.errors.severity.J3DWarning;
import com.j3d.errors.severity.J3Err;

import javax.swing.*;

public class ErrorHandler {
    public static void handle(J3DError err) {
        if (!(err instanceof J3Err j3err)) return;
        String logHead = " " + j3err.logHead() + " ";
//        String msg = logHead+ err.getMessage() + " - " +  ( err.cause != null ? err.cause.getMessage() : "");

        switch (err) {
            case J3DMild j3m -> {
                // mild errors only get printed to the log as the user need not know of this.
                Static.getLog().error(err);
            }
            case J3DWarning j3w -> {
                // Warnings get printed to the log, user debug log and also a little box to the user.
                Static.getLog().error(err);
                JOptionPane.showMessageDialog(Static.mainFrame, err.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            }
            case J3DFatal j3f -> {
                // Fatal errors are unrecoverable.
                Static.getLog().error(err);
                JOptionPane.showMessageDialog(Static.mainFrame, err.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
                if (j3f.terminate()) {
                    throw err;
                }
            }
            default ->  {
                Static.getLog().error(err);
            }
        }
    }
}
