package com.j3d.engine.react.history;

import com.j3d.Static;
import com.j3d.engine.react.actions.Action;
import com.j3d.engine.react.actions.CleanableAction;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * History is a class that stores all actions that have been performed, allowing for undo and redo functionality.
 * Obviously.
 */
public class History extends ArrayList<Action<?>> {
    /**
     * Where actions that are undone are stored for redo functionality.
     */
    private final Backup backup = new Backup();

    public static final String logHead = "[HISTORY] ";

    /**
     * Serial version UID for serialization.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int MAX_HISTORY_SIZE = 50;

    public History() {
        super();
    }

    /**
     * Undoes the last action in the history.
     */
    public void undo() {
        if (this.isEmpty()) return;
        if (!this.getLast().isReversible()) {
            Static.getLog().println(logHead + "Attempt to undo: " + this.getLast().getDescription());
            return;
        };
        Action<?> action = this.removeLast();
        action.undo();
        backup.add(action);
        Static.getLog().println(logHead + "Undo -> " + action.getDescription());
    }

    /**
     * Removes and returns the last action in the history.
     */
    public void redo() {
        if (backup.isEmpty()) return;
        Action<?> action = backup.removeLast();
        action.run();
        bypassAdd(action);
        Static.getLog().println(logHead + "Redo -> " + action.getDescription());
    }

    /**
     * Adds an action to the history, bypassing the size limit and backup clearing.
     * This is used for redoing actions.
     * @param action The action to add.
     */
    private void bypassAdd(Action<?> action) {
        this.addAll(new ArrayList<>(List.of(action)));
    }

    @Override
    public boolean add(Action<?> action) {
        if (this.size() >= MAX_HISTORY_SIZE) {
            Action<?> a = this.remove(0);
            Static.getLog().println(logHead + "History head removed -> " + a.getDescription());
            if (a instanceof CleanableAction cl) {
                try {
                    cl.cleanup();
                    Static.getLog().println(logHead + "Cleaned up (as a result of being too old) -> " + a.getDescription());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        backup.clear();
        Static.getLog().println(logHead + "Add ["+
                (action.isReversible() ? "R" : "!R")
                +"] -> " + action.getDescription());
        return super.add(action);
    }

    @Override
    public void clear() {
        super.clear();
        backup.clear();
    }
}
