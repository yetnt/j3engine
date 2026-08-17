package com.j3d.gen;

import com.j3d.errors.BaseErrorCodes;
import com.j3d.errors.J3DError;


public abstract class GenException extends J3DError {

    public GenException(String message) {
        super(message, BaseErrorCodes.GENERATION.getBaseCode());
    }

    public GenException(String message, Throwable cause) {
        super(message, cause, BaseErrorCodes.GENERATION.getBaseCode());
    }
}
