package com.j3d.gen.docs.reader;

import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;

/**
 *  An exception class specifically for errors encountered during the documentation generation process.
 * <p>
 * These exceptions are {@link J3DFatal} meaning the app cannot proceed when encountering such errors.
 * </p>
 * @author Lehlogonolo Poole
 * @see J3DError
 * @see J3DFatal

 */
public class DocsGenException extends J3DError implements J3DFatal {
    public DocsGenException(String message) {
        super(message);
    }
}
