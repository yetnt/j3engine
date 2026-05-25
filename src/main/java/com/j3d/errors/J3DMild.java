package com.j3d.errors;

public interface J3DMild extends J3Err {
    @Override
    default String logHead() {
        return "[MILD]";
    }
}
