package com.j3d.errors.severity;

public interface J3DFatal extends J3Err{
    @Override
    default String logHead() {
        return "[FATAL]";
    }

    /**
     * Specifies whether the application should terminate after a fatal error.
     * @return {@code true} if the application should terminate, {@code false} otherwise.
     */
    default boolean terminate() {
        return true;
    }
}
