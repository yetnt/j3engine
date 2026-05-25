package com.j3d.threads;

import com.j3d.errors.J3DError;
import com.j3d.errors.J3DFatal;

public class LongTaskException extends J3DError implements J3DFatal {

    public LongTaskException(String message, Throwable cause) {
        super(message, cause);
    }

}
