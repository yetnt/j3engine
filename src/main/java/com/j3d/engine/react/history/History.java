package com.j3d.engine.react.history;

import com.j3d.J3DSettings;
import com.j3d.engine.react.actions.Action;
import com.j3d.engine.react.actions.CleanableAction;

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

    /**
     * Serial version UID for serialization.
     */
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
            J3DSettings.log.println("Attempt to undo: " + this.getLast().getDescription());
            return;
        };
        Action<?> action = this.removeLast();
        action.undo();
        backup.add(action);
        J3DSettings.log.println("Undo -> " + action.getDescription());
    }

    /**
     * Removes and returns the last action in the history.
     */
    public void redo() {
        if (backup.isEmpty()) return;
        Action<?> action = backup.removeLast();
        action.run();
        bypassAdd(action);
        J3DSettings.log.println("Redo -> " + action.getDescription());
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
            if (a instanceof CleanableAction cl) {
                try {
                    cl.cleanup();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        backup.clear();
        return super.add(action);
    }
}
