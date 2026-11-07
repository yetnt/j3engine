package com.j3d.engine.react.events.payloads;

import com.j3d.engine.react.events.EventPayload;
import com.j3d.errors.ErrorHandler;
import com.j3d.errors.J3DError;

public class EmittedErrorEventPayload extends EventPayload<ErrorHandler> {
    private final J3DError error;

    public EmittedErrorEventPayload(ErrorHandler errorHandler, J3DError error) {
        super(errorHandler);
        this.error = error;
    }

    public J3DError getError() {
        return error;
    }
}
