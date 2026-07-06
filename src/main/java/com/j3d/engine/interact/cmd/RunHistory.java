package com.j3d.engine.interact.cmd;

import java.util.ArrayList;

/**
 * Manages the history of executed commands in the command palette.
 * <p>
 *     It provides functionality to navigate through the command history using {@link #up()} and {@link #down()} methods,
 *     and to commit new commands to the history using {@link #commit(String)}.
 * </p>
 * @author Lehlogonolo Poole
 * @see CommandParser
 */
public class RunHistory {
    private final ArrayList<String> history = new ArrayList<>();
    private final ArrayList<String> backup = new ArrayList<>();

    /**
     * Constructs a new {@code RunHistory}.
     */
    public RunHistory() {

    }

    /**
     * Commits a command line to the history.
     * <p>
     *     This clears the backup history and adds the new command to the main history.
     * </p>
     * @param line The command line to commit.
     */
    public void commit(String line) {
        backup.clear();
        history.add(line);
    }

    /**
     * Navigates up through the command history.
     * <p>
     *     Moves the last command from the main history to the backup history and returns it.
     * </p>
     * @return The previous command in the history, or an empty string if the history is empty.
     */
    public String up() {
        if (history.isEmpty()) return "";
        removeFirstIfFull(history);
        backup.add(history.getLast());
        return history.removeLast();
    }

    /**
     * Navigates down through the command history.
     * <p>
     *     Moves the last command from the backup history back to the main history and returns it.
     * </p>
     * @return The next command in the history, or an empty string if the backup history is empty.
     */
    public String down() {
        if (backup.isEmpty()) return "";
        removeFirstIfFull(backup);
        history.add(backup.getLast());
        return backup.removeLast();
    }

    /**
     * Removes the first element from the given ArrayList if its size reaches the maximum limit.
     * <p>
     *     This method ensures that the history lists do not grow indefinitely.
     * </p>
     * @param arr The ArrayList to check and modify.
     */
    private void removeFirstIfFull(ArrayList<String> arr) {
        int MAX = 50;
        if (arr.size() == MAX)
            arr.removeFirst();
    }
}
