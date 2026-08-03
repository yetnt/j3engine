package com.j3d.engine.scene;

import com.j3d.engine.EngineException;

/**
 * Represents an exception that occurs within the 3D scene or engine.
 */
public class SceneException extends EngineException {
    public SceneException(String message) {
        super(message, 41);
    }
}
