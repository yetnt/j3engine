package com.j3d.engine;

import com.j3d.errors.severity.J3DFatal;

public class DefaultObjectDeletionException extends SceneException implements J3DFatal {
    public DefaultObjectDeletionException(String propertyName, String property) {
        super(
                "The default object \"" + propertyName + "\" (" + property + ") was removed "
                + "from the scene. Engine cannot continue."
        );
    }
}
