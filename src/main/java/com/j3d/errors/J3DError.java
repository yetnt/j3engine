package com.j3d.errors;

public class J3DError extends RuntimeException {
    String message;
    Throwable cause = null;

    public J3DError(String message) {
        super(message);
        this.message = message;
    }

    public J3DError(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

}
