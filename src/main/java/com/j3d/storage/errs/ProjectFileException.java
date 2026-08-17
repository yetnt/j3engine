package com.j3d.storage.errs;

import com.j3d.errors.ErrorCodes;
import com.j3d.storage.files.protocol.proj.PF1;

/**
 * An exception that occurs during the reading or writing of a project file (e.g., .j3p).
 * @see J3DFileException
 * @see PF1
 * @author Lehlogonolo Poole
 */
public class ProjectFileException extends J3DFileException {

    public ProjectFileException(String message) {
        super(message, ErrorCodes.IO_PROJECT.getBaseCode());
    }

    public ProjectFileException(String message, Throwable cause) {
        super(message, cause, ErrorCodes.IO_PROJECT.getBaseCode());
    }

    public static ProjectFileException corrupted(String message) {
        return new ProjectFileException("[Corrupted File] " + message);
    }
}
