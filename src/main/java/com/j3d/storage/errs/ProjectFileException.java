package com.j3d.storage.errs;

import com.j3d.storage.files.protocol.proj.ProjectFileV1;

/**
 * An exception that occurs during the reading or writing of a project file (e.g., .j3p).
 * @see J3DFileException
 * @see ProjectFileV1
 * @author Lehlogonolo Poole
 */
public class ProjectFileException extends J3DFileException {

    public ProjectFileException(String message) {
        super(message);
    }

    public ProjectFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
