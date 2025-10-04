package com.j3d.engine;

import javax.swing.*;

/**
 * The `Logger` class provides a simple logging utility for applications.
 * It supports logging messages to both the console and a `JTextArea` component.
 * This class includes methods for standard logging, error logging, and clearing the log area.
 */
public class Logger {
    // Prefix for standard log messages
    private static final String PREFIX = "[J3D] ";
    // Prefix for error log messages
    private static final String ERROR_PREFIX = "!![J3D-ERR] ";
    // The JTextArea where log messages will be displayed (if provided)
    private JTextArea logArea;

    /**
     * Constructs a Logger instance with the specified JTextArea.
     *
     * @param logArea The JTextArea where log messages will be displayed.
     *                If null, messages will only be printed to the console.
     */
    public Logger(JTextArea logArea) {
        this.logArea = logArea;
    }

    /**
     * Gets the current timestamp in the format &lt;HH:MM:SS&gt;.
     *
     * @return The current timestamp as a string.
     */
    private String getTimestamp() {
        return "<" + java.time.LocalTime.now().withNano(0) + ">";
    }

    /**
     * Logs a standard message. The message is prefixed with {@code [J3D]} and
     * printed to the console. If a JTextArea is provided, the message
     * is also appended to it.
     *
     * @param message The message to log.
     */
    public void println(String message) {
        String logMessage = PREFIX + message + "\n";
        System.out.print(logMessage);
        if (logArea != null) {
            logArea.append(logMessage + getTimestamp() + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    /**
     * Logs an error message. The message is prefixed with {@code [J3D-ERR]} and
     * printed to the error console. If a JTextArea is provided, the message
     * is also appended to it.
     *
     * @param message The error message to log.
     */
    public void error(String message) {
        String errorMessage = ERROR_PREFIX + message + "\n";
        System.err.print(errorMessage);
        if (logArea != null) {
            logArea.append(errorMessage + getTimestamp() + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    /**
     * Clears the log area. If a `JTextArea` is provided, its content is cleared.
     */
    public void clear() {
        if (logArea != null) {
            logArea.setText("");
        }
    }
}