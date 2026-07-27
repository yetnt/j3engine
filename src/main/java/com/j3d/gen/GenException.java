package com.j3d.gen;

import com.j3d.errors.J3DError;


public abstract class GenException extends J3DError {

    public GenException(String message) {
        super(message, 1);
    }

    public GenException(String message, Throwable cause) {
        super(message, cause, 1);
    }
}
