package com.j3d.engine.geometry.geo3d.matrix;

import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.engine.geometry.geo3d.Camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Represents a 3D vector with X, Y, and Z components.
 */

public class Vector3 implements MatrixInterface {
    private final double X;
    private final double Y;
    private final double Z;

    public boolean isNotEmpty() {
        return X != Double.MAX_VALUE || Y != Double.MAX_VALUE || Z != Double.MAX_VALUE;
    }

    public Vector3() {
        X = 0;
        Y = 0;
        Z = 0;
    }

    public static Vector3 of(MatrixInterface m) {
        // Assume valid matrix
        // rows must be 1 and cols must be 3
        if (m.rows() != 1 || m.cols() != 3) throw new RuntimeException("Matrix must be 1x3");
        return new Vector3(m.get()[0][0], m.get()[0][1], m.get()[0][2]);
    }

    public Vector3(double x, double y, double z) {
        X = x;
        Y = y;
        Z = z;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getZ() {
        return Z;
    }

    /**
     * Calculates the dot product of this vector and another vector.
     * @param v The other vector.
     * @return The dot product.
     */
    public double dot(Vector3 v) {
        return X * v.getX() + Y * v.getY() + Z * v.getZ();
    }
    /**
     * Normalizes the vector
     * @return A new normalized Vector3.
     */
    public Vector3 normalize() {
        double magnitude = Math.sqrt(X * X + Y * Y + Z * Z);
        return new Vector3(X / magnitude, Y / magnitude, Z / magnitude);
    }

    /**
     * Adds two vectors together.
     * @param v The vector to add.
     * @return A new vector
     */
    public Vector3 add(Vector3 v) {
        return new Vector3(this.X + v.X, this.Y + v.Y, this.Z + v.Z);
    }

    /**
     * Calculates the magnitude (length) of the vector.
     * @return The magnitude of the vector.
     */
    public double magnitude() {
        return Math.sqrt(this.X * this.X + this.Y * this.Y + this.Z * this.Z);
    }

    /**
     * Multiplies the vector by a scalar value.
     * @param scalar The scalar value to multiply by.
     * @return A new Vector3 with the scaled values.
     */
    public Vector3 mult(double scalar) {
        return new Vector3(this.X * scalar, this.Y * scalar, this.Z * scalar);
    }

    /**
     * Multiplies this vector by another vector component-wise.
     * @param B The vector to multiply by.
     * @return A new Vector3 with the component-wise product.
     */
    public Vector3 mult(Vector3 B) {
        return new Vector3(this.X * B.getX(), this.Y * B.getY(), this.Z * B.getZ());
    }

    /**
     * Divides the vector by a scalar value.
     * @param scalar The scalar value to divide by.
     * @return A new Vector3 with the divided values.
     */
    public Vector3 div(double scalar) {
        return new Vector3(this.X / scalar, this.Y / scalar, this.Z / scalar);
    }

    /**
     * Calculates the cross product of this vector and another vector.
     * @param B The other vector.
     * @return A new Vector3 representing the cross product.
     */
    public Vector3 cross(Vector3 B) {
        return new Vector3(
                Y * B.getZ() - Z * B.getY(),
                Z * B.getX() - X * B.getZ(),
                X * B.getY() - Y * B.getX()
        );
    }

    /**
     * Subtracts another vector from this vector.
     * @param B The vector to subtract.
     * @return A new Vector3 representing the result of the subtraction.
     */
    public Vector3 sub(Vector3 B) {
        return new Vector3(this.X - B.X, this.Y - B.Y, this.Z - B.Z);
    }

    /**
     * Calculates the Euclidean distance between this vector and another vector.
     * @param B The other vector.
     * @return The distance between the two vectors.
     */
    public double distance(Vector3 B) {
        return Math.sqrt(Math.pow(X - B.getX(), 2) + Math.pow(Y - B.getY(), 2) + Math.pow(Z - B.getZ(), 2));
    }

    /**
     * Generates a random Vector3 within a specified range.
     * @param low The lower bound vector.
     * @param high The upper bound vector.
     * @return A new random Vector3.
     */
    public static Vector3 random(Vector3 low, Vector3 high) {
        return new Vector3(
                Math.random() * (high.getX() - low.getX()) + low.getX(),
                Math.random() * (high.getY() - low.getY()) + low.getY(),
                Math.random() * (high.getZ() - low.getZ()) + low.getZ()
        );
    }

    /**
     * Creates a copy of this vector.
     * @return A new Vector3 that is a copy of this vector.
     */
    @Override
    public Vector3 copy() {
        return new Vector3(this.X, this.Y, this.Z);
    }

    /**
     * Projects this 3D vector onto a 2D Cartesian plane based on the camera's properties.
     *
     * @param cam The camera defining the viewpoint and projection.
     * @return The resulting 2D {@link CartesianPoint}.
     * @see <a href="https://en.wikipedia.org/wiki/3D_projection#Mathematical_formula">3D Projection:(Perspective) Mathematical Formula</a>
     */
//    public CartesianPoint toPoint(Camera cam) {
//        Vector3 a = this;
//        Vector3 c = cam.getPosition();
//        Vector3 theta = cam.getRotation().toRadVector3();
//        Vector3 e = cam.getProjectionPlane();
//
//        double x = a.getX() - c.getX();
//        double y = a.getY() - c.getY();
//        double z = a.getZ() - c.getZ();
//
//        double sinX = Math.sin(theta.getX());
//        double sinY = Math.sin(theta.getY());
//        double sinZ = Math.sin(theta.getZ());
//        double cosX = Math.cos(theta.getX());
//        double cosY = Math.cos(theta.getY());
//        double cosZ = Math.cos(theta.getZ());
//
//        double dx = cosY * (sinZ * y + cosZ * x) - sinY * z;
//        double dy = sinX * (cosY * z + sinY * (sinZ * y + cosZ * x)) + cosX * (cosZ * y - sinZ * x);
//        double dz = cosX * (cosY * z + sinY * (sinZ * y + cosZ * x)) - sinX * (cosZ * y - sinZ * x);
//
//        if (dz < 1e-18) dz = 1e-6;
//
//        double scale = 1;
//        double bz = scale * ((e.getZ() / dz) * dx + e.getX());
//        double by = scale * ((e.getZ() / dz) * dy + e.getY());
//
//        return new CartesianPoint(bz, by);
//    }

    public CartesianPoint toPoint(Camera cam) {
        Vector3 translated = this.sub(cam.getPosition());

        Vector3 d = cam.getRotation().matrix().transform(translated);

        Vector3 e = cam.getProjectionPlane();

        double dz = d.getZ();
        if (dz < 1e-18) dz = 1e-6;
        double bz = (e.getZ() / dz) * d.getX() + e.getX();
        double by = (e.getZ() / dz) * d.getY() + e.getY();

        return new CartesianPoint(bz, by);
    }

    public ArrayList<Object> toArray() {
        return new ArrayList<>(List.of(X, Y, Z));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Double.compare(getX(), vector3.getX()) == 0 && Double.compare(getY(), vector3.getY()) == 0 && Double.compare(getZ(), vector3.getZ()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ());
    }

    public double distanceSquaredTo(CartesianPoint mousePos) {
        double dx = this.X - mousePos.x;
        double dy = this.Y - mousePos.y;
        double dz = 0;
        return dx * dx + dy * dy + dz * dz;
    }

    public static Vector3 reduceToVector3(ArrayList<Vector3> vectors, BiFunction<Vector3, Vector3, Vector3> reducer) {
        Vector3 result = vectors.getFirst();
        for (int i = 1; i < vectors.size(); i++) {
            result = reducer.apply(result, vectors.get(i));
        }
        return result;
    }

    /**
     * Reduces a list of Vector3 objects
     * @param vectors
     * @param reducer
     * @param initialValue
     * @return
     * @param <K>
     */
    public static <K> K reduce(ArrayList<Vector3> vectors, BiFunction<Vector3, K, K> reducer, K initialValue) {
        K init = null;
        if (initialValue != null) init = initialValue;
        for (int i = 1; i < vectors.size(); i++) {
            init = reducer.apply(vectors.get(i), init);
        }
        return init;
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "X=" + X +
                ", Y=" + Y +
                ", Z=" + Z +
                '}';
    }

    /**
     * Rotates this vector around a given axis by a specified angle using Rodrigues' rotation formula.
     * @param axis The axis to rotate around (should be a normalized vector).
     * @param angleDegrees The angle in degrees to rotate.
     * @return A new Vector3 representing the rotated vector.
     */
    public Vector3 rotateAroundAxis(Vector3 axis, double angleDegrees) {
        double angleRad = Math.toRadians(angleDegrees);
        double cosTheta = Math.cos(angleRad);
        double sinTheta = Math.sin(angleRad);
        Vector3 normalizedAxis = axis.normalize();

        double u = normalizedAxis.getX();
        double v = normalizedAxis.getY();
        double w = normalizedAxis.getZ();

        double xPrime = (u * (u * X + v * Y + w * Z)) * (1 - cosTheta) + X * cosTheta + (-w * Y + v * Z) * sinTheta;
        double yPrime = (v * (u * X + v * Y + w * Z)) * (1 - cosTheta) + Y * cosTheta + (w * X - u * Z) * sinTheta;
        double zPrime = (w * (u * X + v * Y + w * Z)) * (1 - cosTheta) + Z * cosTheta + (-v * X + u * Y) * sinTheta;

        return new Vector3(xPrime, yPrime, zPrime);
    }

    public Vector3 scale(double d) {
        return new Vector3(X * d, Y * d, Z * d);
    }

    @Override
    public int rows() {
        return 1;
    }

    @Override
    public int cols() {
        return 3;
    }

    @Override
    public double get(int row, int col) {
        return switch (col) {
            case 0 -> X;
            case 1 -> Y;
            case 2 -> Z;
            default -> 0;
        };
    }

    @Override
    public void set(int row, int col, double val) {
        // Vector3 cannot be mutated.
    }

    @Override
    public double[][] get() {
        return new double[][] {
                {X, Y, Z}
        };
    }
}
