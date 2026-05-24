package com.j3d.engine.react.history;

import com.j3d.Static;
import com.j3d.engine.react.actions.Action;
import com.j3d.engine.react.actions.CleanableAction;
import com.j3d.ui.engine.popups.HistoryPanel;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


/**
 * Manages a history of actions, allowing for undo and redo functionality.
 * It extends {@link ArrayList} to store {@link Action} objects.
 * @see Action
 * @see Backup
 * @see HistoryPanel
 * @author Lehlogonolo Poole
 */
public class History extends ArrayList<Action<?>> {
    /**
     * Where actions that are undone are stored for redo functionality.
     */
    private final Backup backup = new Backup();

    public static final String logHead = "[HISTORY] ";

    public static HistoryPanel panel;

    /**
     * Serial version UID for serialization.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int MAX_HISTORY_SIZE = 50;

    public History() {
        super();
        panel = new HistoryPanel();
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
        updateHistory();
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
        updateHistory();
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
        boolean a = super.add(action);
        updateHistory();
        return a;
    }

    @Override
    public void clear() {
        super.clear();
        backup.clear();
    }

    /**
     * Updates the history panel with the current actions in the history and backup.
     * This method clears the existing panels and repopulates them.
     */
    private void updateHistory() {
        panel.actionsPanel.removeAll();
        panel.repaint();
        this.forEach(a -> panel.addPanel(a.getPanel(), false));
        backup.reversed().forEach(a -> panel.addPanel(a.getPanel(), true));
        panel.repaint();
    }

    /**
     * Applies a given action by either redoing it from the backup or undoing actions
     * from the history until the target action is reached.
     * @param action The action to apply.
     */
    public void apply(Action action) {
        // Find the action in the history and backup.
        int index = this.indexOf(action);
        int backupIndex = backup.indexOf(action);

        // If in the backup,traverse the backup in reverse until we redo the action.
        if (backupIndex != -1) {
            for (int i = backup.size() - 1; i >= backupIndex; i--) {
                Action<?> a = backup.get(i);
                redo();
            }
        } else if (index != -1) {
            // If in the history, traverse in reverse until we undo the action.
            for (int i = this.size() - 1; i >= index; i--) {
                Action<?> a = this.get(i);
                if (!a.isReversible()) throw new RuntimeException("Attempt to undo an irreversible action + " + a.getDescription());
                undo();
            }
        }

        Static.mainPanel.repaint();
    }
}
