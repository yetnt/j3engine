package com.j3d.engine.math;

import com.j3d.engine.EngineException;
import com.j3d.engine.scene.SceneException;
import com.j3d.errors.ErrorCodes;

public class EngineMathException extends EngineException {
    public EngineMathException(String message) {
        super(message, ErrorCodes.ENGINE_CORE_MATH.getBaseCode());
    }

    public EngineMathException(String message, Throwable cause) {
        super(message, cause, ErrorCodes.ENGINE_CORE_MATH.getBaseCode());
    }
}
