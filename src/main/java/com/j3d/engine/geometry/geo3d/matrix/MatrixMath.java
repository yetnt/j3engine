package com.j3d.engine.geometry.geo3d.matrix;

/**
 * Provides a collection of static utility methods for performing mathematical operations
 * on matrices that implement the {@link MatrixInterface}. This class includes methods
 * for matrix creation, validation, addition, subtraction, and multiplication.
 * <p>
 * This is an abstract utility class and cannot be instantiated.
 */
public abstract class MatrixMath {

    /**
     * A specialized {@link ArithmeticException} thrown to indicate an error during a
     * matrix operation, such as a dimension mismatch in multiplication.
     */
    public static class MatrixException extends ArithmeticException {
        public MatrixException(String message) {
            super(message);
        }
    }

    /**
     * Validates the structure of a 2D double array to ensure it can represent a valid matrix.
     * This method checks for three conditions:
     * <ol>
     *     <li>The input is a 2D array.</li>
     *     <li>All rows have the same length.</li>
     *     <li>The array does not contain any {@code Double.NaN} values.</li>
     * </ol>
     *
     * @param m The 2D double array to validate.
     * @throws IllegalStateException if the array is not a valid matrix structure.
     */
    protected static void validate(double[][] m) {
        // 1. is an array with another array in it
        if (m == null || m.length == 0 || !m[0].getClass().isArray()) {
            throw new IllegalStateException("Input is not a valid 2D array.");
        }

        // 2. No NaN elements.
        // 3. Row lengths must match.
        int rowLength = m[0].length;
        for (double[] doubles : m) {
            if (doubles.length != rowLength) {
                throw new IllegalStateException("Row lengths do not match.");
            }
            for (double aDouble : doubles) {
                if (Double.isNaN(aDouble)) {
                    throw new IllegalStateException("Matrix contains NaN values.");
                }
            }
        }
    }

    /**
     * Creates a {@link MatrixInterface} wrapper around a 2D double array.
     *
     * @param m The 2D double array to wrap.
     * @return A {@link MatrixInterface} instance backed by the given array.
     * @throws IllegalStateException if the array is not a valid matrix structure.
     * @implNote The returned matrix is a direct wrapper around the input array.
     * Any modifications to the matrix will affect the original array, and vice-versa.
     * Furthermore, the {@link MatrixInterface#copy()} method on the returned instance
     * does *not* create a new copy but returns the same instance. This is for performance
     * reasons in internal operations. For a proper deep copy, create a new array manually.
     */
    public static MatrixInterface matrixOf(double[][] m) {
        validate(m);
        return new MatrixInterface() {
            @Override
            public int rows() {
                return m.length;
            }

            @Override
            public int cols() {
                return m[0].length;
            }

            @Override
            public double get(int row, int col) {
                return m[row][col];
            }

            @Override
            public void set(int row, int col, double val) {
                m[row][col] = val;
            }

            @Override
            public double[][] get() {
                return m;
            }

            @Override
            public MatrixInterface copy() {
                return this;
            }
        };
    }

    /**
     * Checks if two matrices have the same number of rows and columns.
     *
     * @param m1 The first matrix.
     * @param m2 The second matrix.
     * @return {@code true} if both matrices have identical dimensions, {@code false} otherwise.
     */
    public static boolean equalsRowsEqualCols(MatrixInterface m1, MatrixInterface m2) {
        return m1.rows() == m2.rows() && m1.cols() == m2.cols();
    }

    /**
     * Performs element-wise addition of two matrices.
     *
     * @param m1 The first matrix.
     * @param m2 The second matrix.
     * @return A new {@link MatrixInterface} containing the result of the addition.
     * @throws MatrixException if the matrices do not have the same dimensions.
     */
    public static MatrixInterface add(MatrixInterface m1, MatrixInterface m2) {
        if (!equalsRowsEqualCols(m1, m2)) throw new MatrixException("Matrices must have the same dimensions for addition.");
        final double[][] m = new double[m1.rows()][m2.cols()];

        for (int i = 0; i < m1.rows(); i++) {
            for (int j = 0; j < m2.cols(); j++) {
                m[i][j] = m1.get(i, j) + m2.get(i, j);
            }
        }

        return matrixOf(m);
    }

    /**
     * Performs element-wise subtraction of two matrices (m1 - m2).
     *
     * @param m1 The first matrix (minuend).
     * @param m2 The second matrix (subtrahend).
     * @return A new {@link MatrixInterface} containing the result of the subtraction.
     * @throws MatrixException if the matrices do not have the same dimensions.
     */
    public static MatrixInterface sub(MatrixInterface m1, MatrixInterface m2) {
        if (!equalsRowsEqualCols(m1, m2)) throw new MatrixException("Matrices must have the same dimensions for subtraction.");
        final double[][] m = new double[m1.rows()][m2.cols()];

        for (int i = 0; i < m1.rows(); i++) {
            for (int j = 0; j < m2.cols(); j++) {
                m[i][j] = m1.get(i, j) - m2.get(i, j);
            }
        }

        return matrixOf(m);
    }

    /**
     * Performs matrix multiplication (m1 * m2).
     * The number of columns in the first matrix must be equal to the number of rows in the second matrix.
     *
     * @param m1 The first matrix.
     * @param m2 The second matrix.
     * @return A new {@link MatrixInterface} containing the result of the multiplication.
     * @throws MatrixException if the number of columns in m1 does not equal the number of rows in m2.
     */
    public static MatrixInterface mult(MatrixInterface m1, MatrixInterface m2) {
        if (m1.cols() != m2.rows()) throw new MatrixException("Matrix multiplication is not defined for the 2 matrices");
        final double[][] m = new double[m1.rows()][m2.cols()];

        for (int i = 0; i < m1.rows(); i++) {
            for (int j = 0; j < m2.cols(); j++) {
                for (int k = 0; k < m1.cols(); k++) {
                    m[i][j] += m1.get(i, k) * m2.get(k, j);
                }
            }
        }

        return matrixOf(m);
    }
}
