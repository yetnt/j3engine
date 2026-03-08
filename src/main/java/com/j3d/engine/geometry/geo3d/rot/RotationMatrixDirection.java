package com.j3d.engine.geometry.geo3d.rot;

import com.j3d.engine.geometry.geo3d.matrix.MatrixInterface;

/**
 * Defines the contract for generating individual rotation matrices for pitch, yaw, and roll.
 * <p>
 * This interface allows for different implementations of rotation logic, such as converting
 * from camera space to world space or vice-versa. By separating the creation of each
 * rotation matrix, it provides fine-grained control over how transformations are constructed.
 *
 * @see CamToWorldDirection
 * @see WorldToCamDirection
 */
public interface RotationMatrixDirection {
    /**
     * Creates a rotation matrix for pitch (rotation around the X-axis).
     *
     * @return A {@link MatrixInterface} representing the pitch rotation.
     */
    MatrixInterface rotPitch();

    /**
     * Creates a rotation matrix for yaw (rotation around the Y-axis).
     *
     * @return A {@link MatrixInterface} representing the yaw rotation.
     */
    MatrixInterface rotYaw();

    /**
     * Creates a rotation matrix for roll (rotation around the Z-axis).
     *
     * @return A {@link MatrixInterface} representing the roll rotation.
     */
    MatrixInterface rotRoll();
}
