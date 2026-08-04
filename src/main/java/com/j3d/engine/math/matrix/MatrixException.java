package com.j3d.engine.math.matrix;

import com.j3d.engine.math.MathException;
import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;

public class MatrixException extends MathException implements J3DFatal {

    public MatrixException(String message) {
        super(message);
    }

    public MatrixException(String message, Throwable cause) {
        super(message, cause);
    }

    public static J3DError sameDimensionsException(String operation) {
        return new MatrixException("Matrices must have the same dimensions for " + operation)
                .code(104);
    }

    public static J3DError exactDimensionException(MatrixInterface m1, MatrixInterface m2) {
        String dim = m1.rows() + "x" + m1.cols();
        String dim2 = m2.rows() + "x" + m2.cols();
        String matrixName = m1.getClass().getSimpleName();
        return new MatrixException("Given input "+dim2+" is not a " + dim + " (" + matrixName + ") matrix.")
                .code(106);
    }

    public static J3DError indexOutOfBounds(MatrixInterface mi, String indexLabel, int index, int low, int high) {
        return new
                MatrixException(mi.getClass().getSimpleName() + " " + indexLabel + " expected the index to be between " + low + " and " + high + ", but got " + index)
                .code(107);
    }
}
