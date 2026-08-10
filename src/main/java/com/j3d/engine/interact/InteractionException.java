package com.j3d.engine.interact;

import com.j3d.errors.ErrorCodes;
import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;

public class InteractionException extends J3DError implements J3DFatal {
    public InteractionException(String message) {
        super(message, ErrorCodes.ENGINE_CORE_INTERACT.getBaseCode());
    }

    public InteractionException(String message, Throwable cause) {
        super(message, cause, ErrorCodes.ENGINE_CORE_INTERACT.getBaseCode());
    }
}
