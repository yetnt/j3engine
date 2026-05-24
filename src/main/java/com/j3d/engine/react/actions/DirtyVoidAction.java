package com.j3d.engine.react.actions;

/**
 * An Action where the action is purely side effects but requires clean-up when the action gets deleted.
 * @see CleanableAction
 * @see VoidAction
 * @author Lehlogonolo Poole
 */
public interface DirtyVoidAction extends VoidAction, CleanableAction {
}
