package com.j3d.engine.react.actions;

import com.j3d.engine.react.history.History;

/**
 * What must happen if a {@link CleanableAction} is being discarded bny {@link History}
 * due to being too old
 * @see CleanableAction
 * @see History
 * @author Lehlogonolo Poole
 */
public enum LifetimePolicy {
    /**
     * Calls {@link CleanableAction#cleanup()} when this action is being deleted
     */
    CLEANUP,
    /**
     * Do nothing and conitnue removing the action.
     */
    NONE
}
