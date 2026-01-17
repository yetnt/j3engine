package com.j3d.engine.react.history;

/**
 * Any Action whih needs to clean up resources should implement CloneableAction
 */
public interface CleanableAction {
    void cleanup() throws Exception;
}
