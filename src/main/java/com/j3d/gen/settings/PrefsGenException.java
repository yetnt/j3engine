package com.j3d.gen.settings;

import com.j3d.errors.severity.J3DFatal;
import com.j3d.gen.GenException;

public class PrefsGenException extends GenException implements J3DFatal {
    public PrefsGenException(String message) {
        super(message);
    }

    public PrefsGenException(String message, Throwable cause) {
        super(message, cause);
    }
}
