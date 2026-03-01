package com.j3d.engine.geometry.geo3d;

public final class Matrix4 {
    public final double[][] m;

    public Matrix4(double[][] vs) {
        m = vs;
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

    public Matrix4 multiply(Matrix4 o) {
        double[][] r = new double[4][4];

        for (int row=0; row<4; row++) {
            for (int col=0; col<4; col++) {
                for (int i=0; i<4; i++) {
                    r[row][col] += m[row][i] * o.m[i][col];
                }
            }
        }

        return new Matrix4(r);
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

    /**
     * Creates a translation matrix from a given vector.
     * @param v The vector to translate by.
     * @return A new translation matrix.
     */
    public static Matrix4 translation(Vector3 v) {
        double x = v.getX(), y = v.getY(), z = v.getZ();

        return new Matrix4(
                new double[][]{
                        {1, 0, 0, x},
                        {0, 1, 0, y},
                        {0, 0, 1, z},
                        {0, 0, 0, 1}
                }
        );
    }

    public static Matrix4 rotX(double rad) {
        double
                c = Math.cos(rad), s = Math.sin(rad);
        return new Matrix4(
                new double[][]{
                        {c, 0, s, 0},
                        {0, 1, 0, 0},
                        {-s, 0, c, 0},
                        {0, 0, 0, 1}
                }
        );
    }

    public static Matrix4 rotZ(double rad) {
        double
                c = Math.cos(rad), s = Math.sin(rad);
        return new Matrix4(
                new double[][]{
                        {c, -s, 0, 0},
                        {s, c, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                }
        );
    }

    public static Matrix4 rotY(double rad) {
        double
                c = Math.cos(rad), s = Math.sin(rad);
        return new Matrix4(
                new double[][]{
                        {c, 0, s, 0},
                        {0, 1, 0, 0},
                        {-s, 0, c, 0},
                        {0, 0, 0, 1}
                }
        );
    }
}
