package com.j3d.errors.severity;

import com.j3d.errors.ErrorHandler;
import com.j3d.errors.J3DError;

/**
 * Defines the contract for error severity levels within J3Engine
 * <p>
 *     Implementors are different error severities, and all {@link J3DError}
 *     are expected to implement one of these interfaces.
 * </p>
 * @see J3DMild
 * @see J3DWarning
 * @see J3DFatal
 * @see J3DError
 * @see ErrorHandler
 * @author Lehlogonolo Poole
 */
public interface J3ErrSeverity {
    /**
     * Returns a string representing the header to be used when logging
     * messages of this specific error severity.
     * For example, an implementation for a "WARNING" severity might return "[WARNING]".
     *
     * @return A {@code String} representing the log header for this severity.
     */
    String logHead();
}
