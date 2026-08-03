package com.j3d.engine;

import com.j3d.errors.J3DError;

public class EngineException extends J3DError {

    public EngineException(String message) {
        super(message, 40);
    }

    public EngineException(String message, int baseCode) {
        super(message, baseCode);
    }

    public EngineException(String message, Throwable cause, int baseCode) {
        super(message, cause, baseCode);
    }
}
