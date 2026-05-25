package com.j3d.storage.files;

import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;

public class ProjectFileException extends J3DError implements J3DFatal {

    public ProjectFileException(String message) {
        super(message);
    }

    public ProjectFileException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public boolean terminate() {
        return false;
    }
}
