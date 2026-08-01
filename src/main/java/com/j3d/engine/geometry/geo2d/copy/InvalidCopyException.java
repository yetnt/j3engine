package com.j3d.engine.geometry.geo2d.copy;

import com.j3d.engine.SceneException;
import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DWarning;

public class InvalidCopyException extends SceneException implements J3DWarning {
    public InvalidCopyException(String message) {
        super("Could not copy objects: " + message);
    }

}
