package com.j3d.storage.errs;

import com.j3d.storage.files.protocol.GenericFileProtocol;

/**
 * A generalisation of {@link GenericIOException} for {@link GenericFileProtocol}
 * @see GenericIOException
 * @see GenericFileProtocol
 * @author Lehlogonolo Poole
 */
public class J3DFileException extends GenericIOException {
    public J3DFileException(String message) {
        super(message);
    }

    public J3DFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
