package com.j3d.utility;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

/**
 * Utility class for interacting with the system clipboard.
 * Provides methods to copy text to the clipboard.
 * @author Lehlogonolo Poole
 */
public class ClipboardUtil {

    /**
     * Copies the given text to the system clipboard.
     *
     * @param text The string to be copied to the clipboard.
     */
    public static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }
}