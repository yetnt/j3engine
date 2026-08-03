package com.j3d.threads;

import com.j3d.errors.ErrorCodes;
import com.j3d.errors.J3DError;

public class ThreadsException extends J3DError {
    public ThreadsException(String message, Throwable cause) {
        super(message, cause, ErrorCodes.THREADING.getBaseCode());
    }
}
