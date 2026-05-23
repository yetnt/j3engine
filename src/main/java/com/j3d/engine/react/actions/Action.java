package com.j3d.engine.react.actions;

import com.j3d.ui.engine.popups.ActionPanel;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents an action that can be executed and potentially undone.
 * Implementations of the interface provide the logic for executing and undoing the action themselves.
 * @param <T> The type of the result returned by the run method.
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

    LocalTime getTime();

    default ActionPanel getPanel() {
        return new ActionPanel(this);
    }
}
