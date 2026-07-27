package com.j3d.engine;

import com.j3d.errors.J3DError;

/**
 * Represents an exception that occurs within the 3D scene or engine.
 */
public class SceneException extends J3DError {
    public SceneException(String message) {
        super(message, 4);
    }
}
