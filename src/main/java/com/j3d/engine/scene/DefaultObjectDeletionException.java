package com.j3d.engine.scene;

import com.j3d.errors.severity.J3DFatal;

/**
 * Exception thrown when a default object, essential for the engine's operation, is
 * removed from the scene. This indicates a critical error from which the engine cannot recover.
 */
public class DefaultObjectDeletionException extends SceneException implements J3DFatal {
    public DefaultObjectDeletionException(String propertyName, String property) {
        super(
                "The default object \"" + propertyName + "\" (" + property + ") was removed "
                + "from the scene. Engine cannot continue."
        );
    }
}
