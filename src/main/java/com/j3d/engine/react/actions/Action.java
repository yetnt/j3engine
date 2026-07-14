package com.j3d.engine.react.actions;

import com.j3d.ui.engine.floating.ActionPanel;

import java.time.LocalTime;

/**
 * Represents an action that can be executed and undone by either the app or the user affecting the scene.
 * Implementations of the interface provide the logic for executing and undoing the action themselves.
 * <p>
 *     This interface has multiple other versions for return type clarity.
 * </p>
 * @param <T> The type of the result returned by the run method.
 * @see VoidAction
 * @see DirtyAction
 * @see DirtyVoidAction
 * @see CleanableAction
 * @see ConstructorAction
 * @see AbstractAction
 * @see ActionPanel
 * @author Lehlogonolo Poole
 */
public interface Action<T> {
    /**
     * Executes the action.
     * @return The result of the action.
     */
    T run();
    /**
     * Undoes the action.
     */
    void undo();
    /**
     * Checks if the action is reversible.
     * @return true if the action can be undone, false otherwise.
     */
    boolean isReversible();
    /**
     * Provides a description of the action. This description is printed to both the
     * debug console and the history log (UI).
     * @return A string describing the action.
     */
    String getDescription();

    /**
     * Returns the time at which the action was performed.
     * @return The time of the action.
     */
    LocalTime getTime();

    /**
     * Provides a default implementation for getting an {@link ActionPanel} associated with this action.
     * @return An {@link ActionPanel} instance.
     */
    default ActionPanel getPanel() {
        return new ActionPanel(this);
    }
}
