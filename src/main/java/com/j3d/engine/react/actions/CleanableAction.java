package com.j3d.engine.react.actions;

/**
 * An implementation for Actions which provide a clean-up method to be called when the action is deleted from the history.
 * @see DirtyAction
 * @see DirtyVoidAction
 * @author Lehlogonolo Poole
 */
public interface CleanableAction {
    /**
     * Cleans up any resources associated with the action.
     * This method is called when the action is removed from the history,
     * for example, when the history size limit is exceeded or the action is undone and then overwritten.
     * @throws Exception if an error occurs during clean-up.
     */
    void cleanup() throws Exception;
}
