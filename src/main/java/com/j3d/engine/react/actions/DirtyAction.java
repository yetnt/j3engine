package com.j3d.engine.react.actions;

/**
 * An Action where the action returns a meaningful value
 * and the action requires cleanup.
 */
public interface DirtyAction<T> extends Action<T>, CleanableAction {
}
