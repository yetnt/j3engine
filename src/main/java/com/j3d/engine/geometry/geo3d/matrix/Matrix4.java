package com.j3d.engine.geometry.geo3d.matrix;

/**
 * A 4x4 Matrix.
 * @deprecated Honestly, while it can be useful for putting rotations and transformations
 * into one, its confusing for me who is still fairly new to amtrices.
 * So it may be removed in the near future. No docs.
 * @author Lehlogonolo Poole
 */
public final class Matrix4 implements MatrixInterface {
    public final double[][] m;

    public Matrix4(double[][] vs) {
        m = vs;
    }

    public static Matrix4 of(MatrixInterface m) {
        // Assume m is a valid matrix.
        // 1. rows and cols should be 4
        if (m.rows() != 4 || m.cols() != 4) throw new RuntimeException("Matrix must be 4x4");
        return new Matrix4(m.get());
    }

    public static Matrix4 identity() {
        return new Matrix4(
                new double[][]{
                        {1, 0, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                }
        );
    }

    public static Matrix4 mNull() {
        return new Matrix4(
                new double[][]{
                        {0, 0, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                }
        );
    }

    public Matrix4 multiply(Matrix4 other) {
        // Result double array that represents a 4x4 matrix
        double[][] result = new double[4][4];

        for (int row=0; row<4; row++) {
            for (int col=0; col<4; col++) {
                for (int i=0; i<4; i++) {
                    /*
                    result[m][n] += this matrix's [m][i] * other matrix's [i][n]
                     */
                    result[row][col] += this.m[row][i] * other.m[i][col];
                }
            }
        }

        // Return the result as a new matrix
        return new Matrix4(result);
    }

    public Vector3 transform(Vector3 v) {
        double x  =
                v.getX() * m[0][0] + v.getY() * m[0][1] + v.getZ() * m[0][2] + m[0][3];
        double y =
                v.getX() * m[1][0] + v.getY() * m[1][1] + v.getZ() * m[1][2] + m[1][3];
        double z =
                v.getX() * m[2][0] + v.getY() * m[2][1] + v.getZ() * m[2][2] + m[2][3];

        return new Vector3(x, y, z);
    }

    @Override
    public String toString() {
        return "Matrix4\n" + toMatrixString();
    }

    @Override
    public int rows() {
        return 4;
    }

    @Override
    public int cols() {
        return 4;
    }

    @Override
    public double get(int row, int col) {
        return m[row][col];
    }

    @Override
    public double[][] get() {
        return m.clone();
    }

    @Override
    public Matrix4 copy() {
        return new Matrix4(new double[][]{
                {m[0][0], m[0][1], m[0][2], m[0][3]},
                {m[1][0], m[1][1], m[1][2], m[1][3]},
                {m[2][0], m[2][1], m[2][2], m[2][3]},
                {m[3][0], m[3][1], m[3][2], m[3][3]}
        });
    }
}
