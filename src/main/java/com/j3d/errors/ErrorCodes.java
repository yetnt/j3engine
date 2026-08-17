package com.j3d.errors;

public enum ErrorCodes {

    GENERATION(10),
    DATABASE(20),
    THREADING(30),

    ENGINE_CORE(40),
    ENGINE_CORE_SCENE(41),
    ENGINE_CORE_MATH(42),
    ENGINE_CORE_INTERACT(43),

    IO(50),
    IO_PROJECT(51);

    ErrorCodes(int i) {
        baseCode = i;
    }
    private final int baseCode;

    public int getBaseCode() {
        return baseCode;
    }
}
