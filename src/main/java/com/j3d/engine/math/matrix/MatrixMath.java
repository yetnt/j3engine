package com.j3d.engine.math.matrix;

/**
 * Provides a collection of static utility methods for performing mathematical operations
 * on matrices that implement the {@link MatrixInterface}. This class includes methods
 * for matrix creation, validation, addition, subtraction, and multiplication.
 * <p>
 * This is an abstract utility class and cannot be instantiated.
 * <p>
 *     This also has its own custom exception wrapping ArithmeticException thrown
 *     when specifically matrix related errors occur.
 * </p>
 * @author Lehlogonolo Poole
 * @see MatrixInterface
 * @see MatrixMath.MatrixException
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
     * Creates a {@link MatrixInterface} instance from a 2D double array.
     * <p>
     * This factory method serves as a bridge between raw {@code double[][]} arrays and the
     * {@link MatrixInterface} ecosystem. It first validates the array to ensure it represents a
     * well-formed matrix.
     * <p>
     * Based on the dimensions of the input array, this method will return a specialized,
     * high-performance implementation (e.g., {@link Matrix3} for 3x3)
     * or a generic, anonymous implementation for other sizes.
     *
     * @param m The 2D double array to create the matrix from. A deep copy of this array is created
     *          to ensure the new matrix is independent of the original array.
     * @return A new {@link MatrixInterface} implementation containing a copy of the provided data.
     * @throws IllegalStateException if the array is not a valid matrix structure (e.g., jagged rows, NaN values).
     * @implNote <strong>Data Safety:</strong> This method creates a deep copy of the input {@code m} array.
     * This ensures that the returned matrix is completely independent of the original array. Modifications
     * to the original array will not affect the matrix, and modifications to the matrix will not
     * affect the original array.
     * @implNote <strong>Mutability:</strong> The mutability of the returned matrix depends on the
     * specific implementation returned. While {@code Matrix3} and {@code Matrix4} may be mutable,
     * the generic fallback wrapper is <strong>read-only</strong> as it does not implement the
     * {@code set} method.
     */
    public static MatrixInterface matrixOf(double[][] m) {
        validate(m);
        String size = "" + m.length + m[0].length;
        double[][] mCopy = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++) {
            System.arraycopy(m[i], 0, mCopy[i], 0, m[i].length);
        }
        return switch (size) {
            case "33" -> new Matrix3(mCopy);
            case "31" -> new Vector3(mCopy);
            default -> new MatrixInterface() {
                @Override
                public int rows() {
                    return mCopy.length;
                }

                @Override
                public int cols() {
                    return mCopy[0].length;
                }

                @Override
                public double get(int row, int col) {
                    return mCopy[row][col];
                }

                @Override
                public double[][] get() {
                    return mCopy;
                }

                @Override
                public MatrixInterface copy() {
                    return matrixOf(mCopy);
                }
            };
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
