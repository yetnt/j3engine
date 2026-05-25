package com.j3d.errors;

public interface J3DFatal extends J3Err{
    @Override
    default String logHead() {
        return "[FATAL]";
    }
}
