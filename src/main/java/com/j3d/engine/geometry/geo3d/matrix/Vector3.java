package com.j3d.engine.geometry.geo3d.matrix;

import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.engine.geometry.geo3d.Camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Represents an immutable 3D vector or a point in 3D space.
 * <p>
 * This class also implements the {@link MatrixInterface}, treating the vector as a
 * 3x1 column matrix. This allows it to be used directly in matrix operations
 * defined in {@link MatrixMath}.
 * <p>
 * All vector operations (e.g., {@code add}, {@code sub}, {@code mult}) return a new
 * {@code Vector3} instance, preserving the immutability of the original.
 */
public class Vector3 implements MatrixInterface {
    private final double X;
    private final double Y;
    private final double Z;

    public Vector3(double[][] m) {
        X = m[0][0];
        Y = m[1][0];
        Z = m[2][0];
    }

    /**
     * Checks if the vector is not set to the special "empty" value.
     *
     * @return {@code true} if the vector holds meaningful data, {@code false} otherwise.
     */
    public boolean isNotEmpty() {
        return X != Double.MAX_VALUE || Y != Double.MAX_VALUE || Z != Double.MAX_VALUE;
    }

    /**
     * Constructs a zero vector (0, 0, 0).
     */
    public Vector3() {
        X = 0;
        Y = 0;
        Z = 0;
    }

    /**
     * Creates a {@code Vector3} from a 3x1 {@link MatrixInterface}.
     *
     * @param m The matrix to convert. Must have 3 rows and 1 column.
     * @return A new {@code Vector3} instance.
     * @throws RuntimeException if the matrix is not 3x1.
     */
    public static Vector3 of(MatrixInterface m) {
        if (m.rows() != 3 || m.cols() != 1) throw new RuntimeException("Matrix must be 3x1 to be converted to a Vector3");
        return new Vector3(m.get(0, 0), m.get(1, 0), m.get(2, 0));
    }

    /**
     * Constructs a vector with the specified x, y, and z components.
     *
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     */
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
     * Calculates the dot product of this vector and another.
     *
     * @param v The other vector.
     * @return The scalar dot product.
     */
    public double dot(Vector3 v) {
        return X * v.getX() + Y * v.getY() + Z * v.getZ();
    }

    /**
     * Normalizes the vector to have a magnitude of 1.
     *
     * @return A new, normalized {@code Vector3}.
     */
    public Vector3 normalize() {
        double magnitude = Math.sqrt(X * X + Y * Y + Z * Z);
        if (magnitude == 0) return new Vector3(0, 0, 0);
        return new Vector3(X / magnitude, Y / magnitude, Z / magnitude);
    }

    /**
     * Adds another vector to this one.
     *
     * @param v The vector to add.
     * @return A new {@code Vector3} representing the sum.
     */
    public Vector3 add(Vector3 v) {
        return new Vector3(this.X + v.X, this.Y + v.Y, this.Z + v.Z);
    }

    /**
     * Calculates the magnitude (length) of the vector.
     *
     * @return The magnitude of the vector.
     */
    public double magnitude() {
        return Math.sqrt(this.X * this.X + this.Y * this.Y + this.Z * this.Z);
    }

    /**
     * Multiplies the vector by a scalar value.
     *
     * @param scalar The scalar value to multiply by.
     * @return A new, scaled {@code Vector3}.
     */
    public Vector3 mult(double scalar) {
        return new Vector3(this.X * scalar, this.Y * scalar, this.Z * scalar);
    }

    /**
     * Performs component-wise multiplication (Hadamard product) with another vector.
     *
     * @param B The vector to multiply by.
     * @return A new {@code Vector3} with the component-wise product.
     */
    public Vector3 mult(Vector3 B) {
        return new Vector3(this.X * B.getX(), this.Y * B.getY(), this.Z * B.getZ());
    }

    /**
     * Divides the vector by a scalar value.
     *
     * @param scalar The scalar value to divide by.
     * @return A new {@code Vector3} with the divided values.
     */
    public Vector3 div(double scalar) {
        if (scalar == 0) throw new ArithmeticException("Cannot divide by zero.");
        return new Vector3(this.X / scalar, this.Y / scalar, this.Z / scalar);
    }

    /**
     * Calculates the cross product of this vector and another.
     *
     * @param B The other vector.
     * @return A new {@code Vector3} representing the cross product, which is orthogonal to both vectors.
     */
    public Vector3 cross(Vector3 B) {
        return new Vector3(
                Y * B.getZ() - Z * B.getY(),
                Z * B.getX() - X * B.getZ(),
                X * B.getY() - Y * B.getX()
        );
    }

    /**
     * Subtracts another vector from this one.
     *
     * @param B The vector to subtract.
     * @return A new {@code Vector3} representing the difference.
     */
    public Vector3 sub(Vector3 B) {
        return new Vector3(this.X - B.X, this.Y - B.Y, this.Z - B.Z);
    }

    /**
     * Calculates the Euclidean distance between this vector (as a point) and another.
     *
     * @param B The other vector.
     * @return The distance between the two points.
     */
    public double distance(Vector3 B) {
        return Math.sqrt(Math.pow(X - B.getX(), 2) + Math.pow(Y - B.getY(), 2) + Math.pow(Z - B.getZ(), 2));
    }

    /**
     * Generates a random {@code Vector3} with components within a specified range.
     *
     * @param low  The lower bound vector for the range.
     * @param high The upper bound vector for the range.
     * @return A new random {@code Vector3}.
     */
    public static Vector3 random(Vector3 low, Vector3 high) {
        return new Vector3(
                Math.random() * (high.getX() - low.getX()) + low.getX(),
                Math.random() * (high.getY() - low.getY()) + low.getY(),
                Math.random() * (high.getZ() - low.getZ()) + low.getZ()
        );
    }

    /**
     * Creates a deep copy of this vector.
     *
     * @return A new {@code Vector3} instance with the same X, Y, and Z values.
     */
    @Override
    public Vector3 copy() {
        return new Vector3(this.X, this.Y, this.Z);
    }

    /**
     * Projects this 3D vector onto a 2D Cartesian plane based on the camera's properties.
     * This method transforms the world-space vector into camera space and then projects it
     * onto the camera's 2D projection plane.
     *
     * @param cam The camera defining the viewpoint and projection.
     * @return The resulting 2D {@link CartesianPoint}.
     */
    public CartesianPoint toPoint(Camera cam) {
        Vector3 c = cam.getPosition();

        // Transform the point from world space to camera space
        Vector3 d = Vector3.of(MatrixMath.mult(
                cam.getRotation().worldToCam().matrix(),
                this.sub(c)
        ));

        Vector3 e = cam.getProjectionPlane();

        // Perform perspective projection
        double dz = d.getZ();
        if (dz < 1e-18) dz = 1e-6; // Avoid division by zero or near-zero
        double bz = (e.getZ() / dz) * d.getX() + e.getX();
        double by = (e.getZ() / dz) * d.getY() + e.getY();

        return new CartesianPoint(bz, by);
    }

    /**
     * Converts the vector's components to an {@code ArrayList} of Objects.
     *
     * @return An {@code ArrayList} containing the X, Y, and Z components in order.
     */
    public ArrayList<Object> toArray() {
        return new ArrayList<>(List.of(X, Y, Z));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Double.compare(vector3.X, X) == 0 && Double.compare(vector3.Y, Y) == 0 && Double.compare(vector3.Z, Z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y, Z);
    }

    /**
     * Calculates the squared 2D distance from this vector's X and Y components to a {@link CartesianPoint}.
     * The Z component is ignored. This is useful for fast 2D distance comparisons.
     *
     * @param point The 2D point.
     * @return The squared distance in the XY plane.
     */
    public double distanceSquaredTo(CartesianPoint point) {
        double dx = this.X - point.x;
        double dy = this.Y - point.y;
        return dx * dx + dy * dy;
    }

    /**
     * Reduces a list of vectors into a single vector using a binary reducer function.
     *
     * @param vectors The list of vectors to reduce.
     * @param reducer A {@link BiFunction} that combines two vectors into one.
     * @return The final reduced {@code Vector3}.
     */
    public static Vector3 reduceToVector3(ArrayList<Vector3> vectors, BiFunction<Vector3, Vector3, Vector3> reducer) {
        if (vectors == null || vectors.isEmpty()) return new Vector3();
        Vector3 result = vectors.getFirst();
        for (int i = 1; i < vectors.size(); i++) {
            result = reducer.apply(result, vectors.get(i));
        }
        return result;
    }

    /**
     * A generic reduction function for a list of vectors.
     *
     * @param vectors      The list of vectors to process.
     * @param reducer      The function to apply to each vector and the accumulated value.
     * @param initialValue The initial value for the reduction.
     * @param <K>          The type of the accumulated value.
     * @return The final accumulated value.
     */
    public static <K> K reduce(ArrayList<Vector3> vectors, BiFunction<Vector3, K, K> reducer, K initialValue) {
        K result = initialValue;
        if (vectors == null) return result;
        for (Vector3 vector : vectors) {
            result = reducer.apply(vector, result);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Vector3{X=%.4f, Y=%.4f, Z=%.4f}", X, Y, Z);
    }

    /**
     * Rotates this vector around a given axis by a specified angle using Rodrigues' rotation formula.
     *
     * @param axis         The axis to rotate around (should be a normalized vector).
     * @param angleDegrees The angle in degrees to rotate.
     * @return A new {@code Vector3} representing the rotated vector.
     */
    public Vector3 rotateAroundAxis(Vector3 axis, double angleDegrees) {
        double angleRad = Math.toRadians(angleDegrees);
        double cosTheta = Math.cos(angleRad);
        double sinTheta = Math.sin(angleRad);
        Vector3 k = axis.normalize(); // The axis of rotation

        // v' = v*cos(t) + (k x v)*sin(t) + k*(k . v)*(1-cos(t))
        Vector3 term1 = this.mult(cosTheta);
        Vector3 term2 = k.cross(this).mult(sinTheta);
        Vector3 term3 = k.mult(k.dot(this) * (1 - cosTheta));

        return term1.add(term2).add(term3);
    }

    /**
     * Scales the vector by a scalar value. Alias for {@link #mult(double)}.
     *
     * @param d The scalar value to multiply by.
     * @return A new, scaled {@code Vector3}.
     */
    public Vector3 scale(double d) {
        return new Vector3(X * d, Y * d, Z * d);
    }

    /**
     * @return The number of rows, which is always 3 for a Vector3.
     */
    @Override
    public int rows() {
        return 3;
    }

    /**
     * @return The number of columns, which is always 1 for a Vector3.
     */
    @Override
    public int cols() {
        return 1;
    }

    /**
     * Gets the value at a specific row in this 3x1 column matrix.
     *
     * @param row The row index (0 for X, 1 for Y, 2 for Z).
     * @param col The column index (must be 0).
     * @return The value of the corresponding component.
     */
    @Override
    public double get(int row, int col) {
        if (col != 0) throw new IndexOutOfBoundsException("Column index must be 0 for a Vector3.");
        return switch (row) {
            case 0 -> X;
            case 1 -> Y;
            case 2 -> Z;
            default -> throw new IndexOutOfBoundsException("Row index must be between 0 and 2 for a Vector3.");
        };
    }

    /**
     * Returns the underlying data as a 2D array (3x1).
     *
     * @return A new 2D double array {@code {{X}, {Y}, {Z}}}.
     */
    @Override
    public double[][] get() {
        return new double[][]{
                {X},
                {Y},
                {Z}
        };
    }
}
