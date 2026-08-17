package com.j3d.engine.interact;

import com.j3d.errors.BaseErrorCodes;
import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;

public class InteractionException extends J3DError implements J3DFatal {
    public InteractionException(String message) {
        super(message, BaseErrorCodes.ENGINE_CORE_INTERACT.getBaseCode());
    }

    public InteractionException(String message, Throwable cause) {
        super(message, cause, BaseErrorCodes.ENGINE_CORE_INTERACT.getBaseCode());
    }
}
