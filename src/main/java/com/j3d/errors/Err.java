package com.j3d.errors;

import com.j3d.errors.severity.J3DFatal;

//todo: Test error, remove
public class Err extends J3DError implements J3DFatal {
    public Err(Throwable cause) {
        super("Something very wrong happened", cause);
    }
}
