package com.j3d.storage.errs;

import com.j3d.errors.ErrorCodes;
import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;
import com.j3d.storage.files.FilesUtility;

/**
 * Any exception caught by any IO operation. This is usually only {@link FilesUtility}.
 * Any IO Operation is considered fatal as there is no way to recover from it.
 * @see FilesUtility
 * @see J3DError
 * @see J3DFatal
 * @author Lehlogonolo Poole
 */
public class GenericIOException extends J3DError implements J3DFatal {
    public GenericIOException(String message) {
        super(message, ErrorCodes.IO.getBaseCode());
    }

    public GenericIOException(String message, Throwable cause) {
        super(message, cause, ErrorCodes.IO.getBaseCode());
    }
}
