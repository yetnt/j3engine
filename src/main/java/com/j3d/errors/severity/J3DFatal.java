package com.j3d.errors.severity;

/**
 * An interface representing a fatal error within the J3Engine.
 * <p>
 * Fatal errors are critical and typically indicate that the application cannot continue
 * its operation safely or correctly.
 * </p>
 * @author Lehlogonolo Poole
 * @see J3Err
 */
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
