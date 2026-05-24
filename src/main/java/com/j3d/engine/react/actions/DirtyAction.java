package com.j3d.engine.react.actions;

/**
 * An Action where the action returns meaningful output, but also requires clean-up when the action eventually gets deleted
 * from the history.
 * @see CleanableAction
 * @author Lehlogonolo Poole
 */
public interface DirtyAction<T> extends Action<T>, CleanableAction {
}
