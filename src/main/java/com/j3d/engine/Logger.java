package com.j3d.engine;

import com.j3d.Static;

import javax.swing.*;
import java.time.format.DateTimeFormatter;

/**
 * The Logger class provides a simple logging utility.
 * It supports logging messages to both the console and the {@link com.j3d.ui.engine.DebugPanel#logTextArea}
 * This class includes methods for standard logging, error logging, and clearing the log area.
 * @author Lehlogonolo Poole
 * @see com.j3d.storage.files.engine.EngineFiles#logFile
 * @see com.j3d.ui.engine.DebugPanel#logTextArea
 * @see Static
 */
public class Logger {
    // Prefix for standard log messages
    private static final String PREFIX = "[J3D] ";
    // Prefix for error log messages
    private static final String ERROR_PREFIX = "!![J3D-ERR] ";
    // The JTextArea where log messages will be displayed (if provided)
    private JTextArea logArea = null;

    /**
     * Constructs a Logger instance with the specified JTextArea.
     */
    public Logger() {
    }

    public void setLogArea(JTextArea logArea) {
        this.logArea = logArea;
    }

    /**
     * Gets the current timestamp in the format &lt;HH:MM:SS&gt;.
     *
     * @return The current timestamp as a string.
     */
    private String getTimestamp() {
        return "<" + java.time.LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + ">";
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
        Static.getEngineFiles().logFile.writeLn(getTimestamp() + " " + PREFIX + message);
        System.out.print(logMessage);
        if (logArea != null) {
            logArea.append(logMessage + getTimestamp() + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    public void uiPrintLn(String message) {
        println("[UI] " + message);
    }

    public void cmdPrintln(String s) {
        println("[CMD] " + s);
    }

    public void stPrintln(String s) {
        println("[START-UP] Quick Access Panel was used to open via: " + s);
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
        Static.getEngineFiles().logFile.writeLn(getTimestamp() + " " + ERROR_PREFIX + message);
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