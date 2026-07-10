package com.j3d.gen.docs;

import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;

public class DocsGenException extends J3DError implements J3DFatal {
    public DocsGenException(String message) {
        super(message);
    }
}
