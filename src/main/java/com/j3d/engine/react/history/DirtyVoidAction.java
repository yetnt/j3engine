package com.j3d.engine.react.history;

/**
 * An Action where the action itself
 * does not return a meaningful value, and the action requires cleanup.
 */
public interface DirtyVoidAction extends VoidAction, CleanableAction {
}
