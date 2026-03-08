package com.j3d.engine.geometry.geo3d.rot;

import com.j3d.engine.geometry.geo3d.matrix.MatrixInterface;

/**
 * Implements the {@link RotationMatrixDirection} interface to create rotation matrices
 * that transform a direction from camera space to world space.
 * <p>
 * This is the standard transformation used to orient an object (like a camera) in the world.
 * For example, applying these rotations to a base "forward" vector (e.g., [0, 0, 1]) will
 * point it in the correct direction in the world scene.
 * <p>
 * The order of multiplication typically matters. For a standard Tait-Bryan rotation (yaw, pitch, roll),
 * the combined matrix would be {@code M = Yaw * Pitch * Roll}.
 */
public class CamToWorldDirection implements RotationMatrixDirection {
    final private double pitch, yaw, roll;

    /**
     * Constructs a new {@code CamToWorldDirection} transformer.
     *
     * @param pitch The pitch angle in radians (rotation around the X-axis).
     * @param yaw   The yaw angle in radians (rotation around the Y-axis).
     * @param roll  The roll angle in radians (rotation around the Z-axis).
     */
    public CamToWorldDirection(double pitch, double yaw, double roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    /**
     * Creates a standard rotation matrix for pitch (rotation around the X-axis).
     *
     * @return A 3x3 {@link MatrixInterface} for pitch rotation.
     */
    @Override
    public MatrixInterface rotPitch() {
        double c = Math.cos(pitch);
        double s = Math.sin(pitch);
        return com.j3d.engine.geometry.geo3d.matrix.MatrixMath.matrixOf(
                new double[][]{
                        {1, 0, 0},
                        {0, c, -s},
                        {0, s, c}
                }
        );
    }

    /**
     * Creates a standard rotation matrix for yaw (rotation around the Y-axis).
     *
     * @return A 3x3 {@link MatrixInterface} for yaw rotation.
     */
    @Override
    public MatrixInterface rotYaw() {
        double c = Math.cos(yaw);
        double s = Math.sin(yaw);
        return com.j3d.engine.geometry.geo3d.matrix.MatrixMath.matrixOf(
                new double[][]{
                        {c, 0, s},
                        {0, 1, 0},
                        {-s, 0, c}
                }
        );
    }

    /**
     * Creates a standard rotation matrix for roll (rotation around the Z-axis).
     *
     * @return A 3x3 {@link MatrixInterface} for roll rotation.
     */
    @Override
    public MatrixInterface rotRoll() {
        double c = Math.cos(roll);
        double s = Math.sin(roll);
        return com.j3d.engine.geometry.geo3d.matrix.MatrixMath.matrixOf(
                new double[][]{
                        {c, -s, 0},
                        {s, c, 0},
                        {0, 0, 1}
                }
        );
    }
}
