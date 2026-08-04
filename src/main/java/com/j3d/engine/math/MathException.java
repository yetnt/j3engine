package com.j3d.engine.math;

import com.j3d.engine.EngineException;
import com.j3d.errors.ErrorCodes;

public class MathException extends EngineException {
    public MathException(String message) {
        super(message, ErrorCodes.ENGINE_CORE_MATH.getBaseCode());
    }

    public MathException(String message, Throwable cause) {
        super(message, cause, ErrorCodes.ENGINE_CORE_MATH.getBaseCode());
    }
}
