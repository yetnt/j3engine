package com.j3d.errors.severity;

public interface J3DWarning extends J3Err {
    @Override
    default String logHead() {
        return "[WARNING]";
    }
}
