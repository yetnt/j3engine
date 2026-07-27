package com.j3d.gen.docs;

import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DMild;
import com.j3d.errors.severity.J3DWarning;
import com.j3d.gen.GenException;

/**
 * An exception class specifically for errors encountered during the image processing within the
 * documentation generation process.
 * <p>
 * These exceptions are {@link J3DWarning} meaning the app can proceed when encountering such errors.
 * </p>
 * @author Lehlogonolo Poole
 * @see J3DError
 * @see J3DMild
 */
public class ImgGenException extends GenException implements J3DWarning {
    public ImgGenException(String message) {
        super(message);
    }
}
