package com.j3d.errors.severity;

/**
 * An interface representing a mild error.
 * <p>
 * Mild errors are generally non-critical and they might indicate minor issues
 * or deviations from expected behaviour.
 * </p>
 * @implSpec These types of errors aren't told to the user.
 * @author Lehlogonolo Poole
 * @see J3ErrSeverity
 */
public interface J3DMild extends J3ErrSeverity {
    @Override
    default String logHead() {
        return "[MILD]";
    }
}
