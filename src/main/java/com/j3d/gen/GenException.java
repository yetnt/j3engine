package com.j3d.gen;

import com.j3d.errors.ErrorCodes;
import com.j3d.errors.J3DError;


public abstract class GenException extends J3DError {

    public GenException(String message) {
        super(message, ErrorCodes.GENERATION.getBaseCode());
    }

    public GenException(String message, Throwable cause) {
        super(message, cause, ErrorCodes.GENERATION.getBaseCode());
    }
}
