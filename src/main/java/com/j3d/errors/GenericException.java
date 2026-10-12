package com.j3d.errors;
import com.j3d.errors.severity.J3DFatal;

public class GenericException extends J3DError implements J3DFatal {

    public GenericException(String message) {
        super(message, BaseErrorCodes.GENERIC.getBaseCode());
    }

    public GenericException(Throwable cause) {
        super(cause, BaseErrorCodes.GENERIC.getBaseCode());
        code(0);
    }
}

