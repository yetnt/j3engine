package com.j3d.engine.react.history;

import java.util.ArrayList;

/**
 * History is a class that stores all actions that have been performed, allowing for undo and redo functionality.
 * Obviously.
 */
public class History extends ArrayList<Action<?>> {
    /**
     * Where actions that are undone are stored for redo functionality.
     */
    private ArrayList<Action<?>> backup = new ArrayList<>();

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    private static final int MAX_HISTORY_SIZE = 100;

    public History() {
        super();
    }

    /**
     * Undoes the last action in the history.
     */
    public void undo() {
        if (this.isEmpty()) return;
        Action<?> action = this.removeLast();
        action.undo();
        backup.add(action);
    }

    /**
     * Removes and returns the last action in the history.
     */
    public void redo() {
        if (backup.isEmpty()) return;
        Action<?> action = backup.removeLast();
        action.run();
        this.add(action);
    }

    @Override
    public boolean add(Action<?> action) {
        if (this.size() >= MAX_HISTORY_SIZE)
            this.remove(0);

        backup.clear();
        return super.add(action);
    }
}
