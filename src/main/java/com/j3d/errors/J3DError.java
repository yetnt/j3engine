package com.j3d.errors;

public class J3DError extends RuntimeException {
    String message;
    Throwable cause = null;
    int code = 0;
    private int baseCode = 0;

    public J3DError(String message, int baseCode) {
        super(message);
        this.message = message;
        this.baseCode = baseCode;
    }

    public J3DError(String message, Throwable cause, int baseCode) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
        this.baseCode = baseCode;
    }

    public J3DError code(int value) {
        this.code = value;
        return this;
    }

    public int getCode() {
        return code;
    }

    public int getBaseCode() {
        return baseCode;
    }

    public int buildCode() {
        return baseCode * 1000 + code;
    }

    public String codeToStr() {
        return String.format("%05d", buildCode());
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

}
