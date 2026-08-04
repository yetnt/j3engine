package com.j3d.engine.math.matrix;

import com.j3d.StaticRefs;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.scene.Camera;
import com.j3d.gen.settings.Settings;

import javax.management.ConstructorParameters;
import java.beans.ConstructorProperties;
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
 * @author Lehlogonolo Poole
 * @see MatrixInterface
 * @see MatrixMath
 * @see Matrix3
 * @see CartesianPoint
 * @implNote There are constants for recurring Vector3 objects.
 * <ul>
 *     <li>{@link #X}/{@link #NX}</li>
 *     <li>{@link #Y}/{@link #NY}</li>
 *     <li>{@link #Z}/{@link #NZ}</li>
 *     <li>{@link #UNIT}</li>
 *     <li>{@link #ZERO}</li>
 * </ul>
 */
public class Vector3 implements MatrixInterface {
    private final double x;
    private final double y;
    private final double z;

    /**
     * Constructs a new vector 3 from a 2d matrix double array
     * @param m The 2d matrix double array
     * @implSpec The matrix must be 3x1
     */
    public Vector3(double[][] m) {
        x = m[0][0];
        y = m[1][0];
        z = m[2][0];
    }


    /**
     * Constructs a vector with the specified x, y, and z components.
     *
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     */
    @ConstructorProperties({"getX", "getY", "getZ"})
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a zero vector (0, 0, 0).
     */
    public Vector3() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vector3(boolean empty) {
        if (empty) {
            x = Double.MAX_VALUE;
            y = Double.MAX_VALUE;
            z = Double.MAX_VALUE;
        } else {
            x = 0;
            y = 0;
            z = 0;
        }
    }

    /**
     * Checks if the vector is not set to the special "empty" value.
     *
     * @return {@code true} if the vector holds meaningful data, {@code false} otherwise.
     */
    public boolean isNotEmpty() {
        return x != Double.MAX_VALUE || y != Double.MAX_VALUE || z != Double.MAX_VALUE;
    }


    /**
     * Creates a {@code Vector3} from a 3x1 {@link MatrixInterface}.
     *
     * @param m The matrix to convert. Must have 3 rows and 1 column.
     * @return A new {@code Vector3} instance.
     * @throws RuntimeException if the matrix is not 3x1.
     */
    public static Vector3 of(MatrixInterface m) {
        if (m.rows() != 3 || m.cols() != 1) StaticRefs.getErrs().handle(
                MatrixException.exactDimensionException(Vector3.Y, m)
        );
        return new Vector3(m.get(0, 0), m.get(1, 0), m.get(2, 0));
    }

    /**
     * @return The X component of the vector.
     */
    public double getX() {
        return x;
    }

    /**
     * @return The Y component of the vector.
     */
    public double getY() {
        return y;
    }

    /**
     * @return The Z component of the vector.
     */
    public double getZ() {
        return z;
    }

    /**
     * Calculates the dot product of this vector and another.
     *
     * @param v The other vector.
     * @return The scalar dot product.
     */
    public double dot(Vector3 v) {
        return x * v.getX() + y * v.getY() + z * v.getZ();
    }

    /**
     * Normalizes the vector to have a magnitude of 1.
     *
     * @return A new, normalized {@code Vector3}.
     */
    public Vector3 normalize() {
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude == 0) return Vector3.ZERO;
        return new Vector3(x / magnitude, y / magnitude, z / magnitude);
    }

    /**
     * Adds another vector to this one.
     *
     * @param v The vector to add.
     * @return A new {@code Vector3} representing the sum.
     */
    public Vector3 add(Vector3 v) {
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    /**
     * Calculates the magnitude (length) of the vector.
     *
     * @return The magnitude of the vector.
     */
    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     * Multiplies the vector by a scalar value.
     *
     * @param scalar The scalar value to multiply by.
     * @return A new, scaled {@code Vector3}.
     */
    public Vector3 mult(double scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    /**
     * Performs component-wise multiplication (Hadamard product) with another vector.
     *
     * @param B The vector to multiply by.
     * @return A new {@code Vector3} with the component-wise product.
     */
    public Vector3 mult(Vector3 B) {
        return new Vector3(this.x * B.getX(), this.y * B.getY(), this.z * B.getZ());
    }

    /**
     * Divides the vector by a scalar value.
     *
     * @param scalar The scalar value to divide by.
     * @return A new {@code Vector3} with the divided values.
     */
    public Vector3 div(double scalar) {
        if (scalar == 0) throw new ArithmeticException("Cannot divide by zero.");
        return new Vector3(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    /**
     * Calculates the cross product of this vector and another.
     *
     * @param B The other vector.
     * @return A new {@code Vector3} representing the cross product, which is orthogonal to both vectors.
     */
    public Vector3 cross(Vector3 B) {
        return new Vector3(
                y * B.getZ() - z * B.getY(),
                z * B.getX() - x * B.getZ(),
                x * B.getY() - y * B.getX()
        );
    }

    /**
     * Subtracts another vector from this one.
     *
     * @param B The vector to subtract.
     * @return A new {@code Vector3} representing the difference.
     */
    public Vector3 sub(Vector3 B) {
        return new Vector3(this.x - B.x, this.y - B.y, this.z - B.z);
    }

    /**
     * Calculates the Euclidean distance between this vector (as a point) and another.
     *
     * @param B The other vector.
     * @return The distance between the two points.
     */
    public double distance(Vector3 B) {
        return Math.sqrt(Math.pow(x - B.getX(), 2) + Math.pow(y - B.getY(), 2) + Math.pow(z - B.getZ(), 2));
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
        return new Vector3(this.x, this.y, this.z);
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
        // this setting is default to 18. So 10e-18
        if (dz < Math.pow(10, -Settings.cameraProperties.nearZeroProjectionPower.getValue()))
            dz = 1e-6; // Avoid division by zero or near-zero
        double bz = (e.getZ() / dz) * d.getX() + e.getX();
        double by = (e.getZ() / dz) * d.getY() + e.getY();

        return new CartesianPoint(bz, by);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Double.compare(vector3.x, x) == 0 &&
                Double.compare(vector3.y, y) == 0 &&
                Double.compare(vector3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    /**
     * Calculates the squared 2D distance from this vector's X and Y components to a {@link CartesianPoint}.
     * The Z component is ignored. This is useful for fast 2D distance comparisons.
     *
     * @param point The 2D point.
     * @return The squared distance in the XY plane.
     */
    public double distanceSquaredTo(CartesianPoint point) {
        double dx = this.x - point.x;
        double dy = this.y - point.y;
        return dx * dx + dy * dy;
    }

    /**
     * Reduces a list of vectors into a single vector using a binary reducer function.
     *
     * @param vectors The list of vectors to reduce.
     * @param reducer A {@link BiFunction} that combines two vectors into one.
     * @return The final reduced {@code Vector3}.
     */
    public static Vector3 reduceToVector3(List<Vector3> vectors, BiFunction<Vector3, Vector3, Vector3> reducer) {
        if (vectors == null || vectors.isEmpty()) return Vector3.ZERO;
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
        return String.format("Vector3{X=%.4f, Y=%.4f, Z=%.4f}", x, y, z);
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
        return new Vector3(x * d, y * d, z * d);
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
        if (col != 0) StaticRefs.getErrs().handle(
                MatrixException.indexOutOfBounds(
                        this, "column", col, 0, 0
                )
        );
        return switch (row) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> {
                StaticRefs.getErrs().handle(
                        MatrixException.indexOutOfBounds(
                                this, "row", row, 0, 2
                        )
                );
                yield -1;
            }
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
                {x},
                {y},
                {z}
        };
    }

    /**
     * Converts this Vector3 to command palette syntax
     * @return the string version of this vector3.
     */
    public String toCommandPaletteString() {
        return String.format("(%f, %f, %f)", x, y, z);
    }

    /**
     * Creates a vector along the X-axis with the specified magnitude.
     *
     * @param x The magnitude along the X-axis.
     * @return A new {@code Vector3} (x, 0, 0).
     */
    public static Vector3 X(double x) {
        return new Vector3(x, 0, 0);
    }

    /**
     * Creates a vector along the Y-axis with the specified magnitude.
     *
     * @param y The magnitude along the Y-axis.
     * @return A new {@code Vector3} (0, y, 0).
     */
    public static Vector3 Y(double y) {
        return new Vector3(0, y, 0);
    }

    /**
     * Creates a vector along the Z-axis with the specified magnitude.
     *
     * @param z The magnitude along the Z-axis.
     * @return A new {@code Vector3} (0, 0, z).
     */
    public static Vector3 Z(double z) {
        return new Vector3(0, 0, z);
    }

    /**
     * A static constant representing the unit vector along the positive Y-axis (0, 1, 0).
     */
    public static Vector3 Y = Vector3.Y(1);

    /**
     * A static constant representing the unit vector along the positive X-axis (1, 0, 0).
     */
    public static Vector3 X = Vector3.X(1);

    /**
     * A static constant representing the unit vector along the positive Z-axis (0, 0, 1).
     */
    public static Vector3 Z = Vector3.Z(1);

    /**
     * A static constant representing the unit vector along the negative Y-axis (0, -1, 0).
     */
    public static Vector3 NY = Vector3.Y(-1);

    /**
     * A static constant representing the unit vector along the negative X-axis (-1, 0, 0).
     */
    public static Vector3 NX = Vector3.X(-1);

    /**
     * A static constant representing the unit vector along the negative Z-axis (0, 0, -1).
     */
    public static Vector3 NZ = Vector3.Z(-1);

    /**
     * A static constant representing the zero vector (0, 0, 0).
     */
    public static Vector3 ZERO = new Vector3();
    /**
     * A static constant representing the unit vector (1, 1, 1).
     */
    public static Vector3 UNIT = new Vector3(1, 1, 1);
}
