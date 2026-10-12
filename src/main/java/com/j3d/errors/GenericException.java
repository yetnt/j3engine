package com.j3d.errors;


import com.j3d.errors.severity.J3DFatal;

public class GenericException extends J3DError implements J3DFatal {

    public GenericException(String message) {
        super(message, BaseErrorCodes.GENERATION.getBaseCode());
    }

    public GenericException(Throwable cause) {
        super(cause, BaseErrorCodes.GENERATION.getBaseCode());
        code(0);
    }
}

