package com.j3d.engine.scene;

import com.j3d.engine.EngineException;
import com.j3d.errors.ErrorCodes;

/**
 * Represents an exception that occurs within the 3D scene or engine.
 */
public class SceneException extends EngineException {
    public SceneException(String message) {
        super(message, ErrorCodes.ENGINE_CORE_SCENE.getBaseCode());
    }
    public SceneException(String message, Throwable cause) {
        super(message, cause, ErrorCodes.ENGINE_CORE_SCENE.getBaseCode());
    }
}
