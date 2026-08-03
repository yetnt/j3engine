package com.j3d.engine.math;

import com.j3d.engine.EngineException;
import com.j3d.engine.scene.SceneException;

public class EngineMathException extends EngineException {
    public EngineMathException(String message) {
        super(message, 42);
    }
}
