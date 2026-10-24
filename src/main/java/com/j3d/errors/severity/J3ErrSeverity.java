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
public sealed interface J3ErrSeverity permits J3DFatal, J3DMild, J3DWarning, J3DInfo {
}
