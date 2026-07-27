package com.j3d.errors.severity;

/**
 * An interface representing a warning.
 * <p>
 * Warnings are just certain errors which dont halt the app but should preferably be resolved when possible
 * </p>
 * @author Lehlogonolo Poole
 * @see J3Err
 */
public interface J3DWarning extends J3Err {
    @Override
    default String logHead() {
        return "[WARNING]";
    }
}
