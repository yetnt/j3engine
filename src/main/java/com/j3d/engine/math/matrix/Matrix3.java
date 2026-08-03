package com.j3d.engine.math.matrix;

/**
 * Represents a final, specialized 3x3 matrix.
 * <p>
 * This class provides a concrete and optimised implementation for a 3x3 matrix,
 * commonly used for 2D transformations (in homogeneous coordinates) or 3D rotations.
 * It implements the {@link MatrixInterface} and ensures that all instances are
 * correctly sized.
 * @author Lehlogonolo Poole
 * @see MatrixMath
 * @see MatrixInterface
 * @see Vector3
 * @see Matrix4
 */
public final class Matrix3 implements MatrixInterface {
    /**
     * The internal 2D array storing the matrix data. This field is final, but the
     * array elements themselves can be modified.
     */
    final public double[][] data;

    /**
     * Constructs a {@code Matrix3} by wrapping the given 2D array.
     *
     * @param data A 3x3 2D double array. The constructor assumes the array is correctly sized.
     */
    public Matrix3(double[][] data) {
        this.data = data;
    }

    /**
     * Creates a 3x3 identity matrix.
     *
     * @return A new {@code Matrix3} instance representing the identity matrix.
     */
    public static Matrix3 identity() {
        return new Matrix3(
                new double[][]{
                        {1, 0, 0},
                        {0, 1, 0},
                        {0, 0, 1}
                }
        );
    }

    /**
     * Creates a {@code Matrix3} from any {@link MatrixInterface} implementation.
     *
     * @param m The matrix to convert.
     * @return A new {@code Matrix3} instance with the same data.
     * @throws RuntimeException if the input matrix is not 3x3.
     */
    public static Matrix3 of(MatrixInterface m) {
        if (m.rows() != 3 || m.cols() != 3) throw new RuntimeException("Matrix must be 3x3");
        return new Matrix3(m.get());
    }

    /**
     * Transforms a {@link Vector3} by this matrix.
     *
     * @param v The vector to be transformed.
     * @return A new {@link Vector3} representing the result of the multiplication.
     */
    public Vector3 transform(Vector3 v) {
        return new Vector3(
                v.getX() * data[0][0] + v.getY() * data[0][1] + v.getZ() * data[0][2],
                v.getX() * data[1][0] + v.getY() * data[1][1] + v.getZ() * data[1][2],
                v.getX() * data[2][0] + v.getY() * data[2][1] + v.getZ() * data[2][2]
        );
    }

    @Override
    public int rows() {
        return 3;
    }

    @Override
    public int cols() {
        return 3;
    }

    @Override
    public double get(int row, int col) {
        return data[row][col];
    }

    @Override
    public double[][] get() {
        return data;
    }

    /**
     * Creates a deep copy of this matrix.
     *
     * @return A new {@code Matrix3} instance with the same values.
     */
    @Override
    public MatrixInterface copy() {
        double[][] newData = new double[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(data[i], 0, newData[i], 0, 3);
        }
        return new Matrix3(newData);
    }
}
