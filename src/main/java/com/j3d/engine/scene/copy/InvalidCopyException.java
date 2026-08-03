package com.j3d.engine.scene.copy;

import com.j3d.engine.scene.SceneException;
import com.j3d.errors.severity.J3DWarning;

/**
 * Exception thrown when an attempt to copy objects fails.
 * This typically indicates an issue during the deep copying process
 * of geometric objects or their properties.
 * @see CanCopy
 * @see Copy
 * @see CopyPropertiesBuilder
 * @see CopyProperties
 * @see InvalidCopyException
 */
public class InvalidCopyException extends SceneException implements J3DWarning {
    public InvalidCopyException(String message) {
        super("Could not copy objects: " + message);
    }

}
