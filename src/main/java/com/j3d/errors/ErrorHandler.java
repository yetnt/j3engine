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

    /**
     * Registers a specific {@link J3DError} class to be ignored by the error handler.
     * When an error of the registered class type is encountered by the {@link #handle(J3DError)} method,
     * it will be consumed (removed from the ignore list) and no further action will be taken.
     * This effectively makes the next occurrence of that specific error type to be silently handled.
     * <p>
     * A maximum of 10 error classes can be registered for ignoring at any given time.
     * Attempting to register more will result in a {@link RuntimeException}.
     *
     * @param <T> The type of {@link J3DError} to ignore.
     * @param err The {@code Class} object of the {@link J3DError} to be ignored.
     * @throws RuntimeException if more than 10 error classes are attempted to be ignored.
     */
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

    /**
     * Handles a given {@link J3DError}, processing it based on its type and severity.
     * This method first checks if the error is an instance of {@link J3Err} (which provides logging information).
     * If it is not, the method returns immediately without processing.
     * <p>
     * If the error is an instance of {@link J3Err}, it then checks if the error's exact class
     * is registered in the internal ignore list.
     * If ignored, the error is consumed (removed from the ignore list, implying a one-time ignore)
     * and no further action is taken.
     * <p>
     * Otherwise, the error is processed based on its specific type:
     * <ul>
     *     <li>{@link J3DMild}: The error is printed to the application's log.</li>
     *     <li>{@link J3DWarning}: The error is printed to the application's log, and a warning message
     *         dialog is displayed to the user.</li>
     *     <li>{@link J3DFatal}: The error is printed to the application's log, and a fatal error message
     *         dialog is displayed to the user. If the fatal error's {@code terminate()} method returns true,
     *         an additional message is shown, and the application is terminated via {@code System.exit(1)}.</li>
     *     <li>Any other {@link J3DError} type (that is also an instance of {@link J3Err}):
     *         The error is simply printed to the application's log.</li>
     * </ul>
     *
     * @param err The {@link J3DError} instance to be handled.
     */
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
                JOptionPane.showMessageDialog(StaticRefs.getMainFrame(), err.getMessage(), "Fatal Error " + err.errorCode(), JOptionPane.ERROR_MESSAGE);
                if (j3f.terminate()) {
                    JOptionPane.showMessageDialog(StaticRefs.getMainFrame(),
                            "Due to the nature of the previous error, the app cannot continue in this state and will shut down.",
                            "Fatal Error " + err.errorCode(), JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
//                    throw err;
                }
            }
            default ->  {
                StaticRefs.getLog().error(err);
                StaticRefs.getLog().error(
                        "An error was found and handled (" + err.getClass() + ") but it did not implement"
                        + " any of the severity interfaces (J3DMild, J3DWarning, J3DFatal)."
                );
            }
        }
    }

    /**
     * Handles a given {@link J3DError} using the {@link #handle(J3DError)} method
     * and then re-throws it.
     *
     * @implSpec Only use if its confirmed something up the stack will definitely catch this error.
     *
     * @param err The {@link J3DError} instance to be handled and thrown.
     * @throws J3DError The same {@link J3DError} instance that was passed in,
     *                   after it has been processed by the {@link #handle(J3DError)} method.
     */
    public void handleThenThrow(J3DError err) throws J3DError {
        handle(err);
        throw err;
    }
}
