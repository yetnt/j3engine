package com.j3d.engine.math.rot;

import com.j3d.engine.math.matrix.Matrix3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the contract for generating individual rotation matrices for pitch, yaw, and roll.
 * <p>
 * This interface allows for different implementations of rotation logic, such as converting
 * from camera space to world space or vice-versa. By separating the creation of each
 * rotation matrix, it provides fine-grained control over how transformations are constructed.
 *
 * @author Lehlogonolo Poole
 * @see CamToWorldDirection
 * @see WorldToCamDirection
 */
public sealed interface RotationMatrixDirection permits CamToWorldDirection, WorldToCamDirection {
    /**
     * Creates a rotation matrix for pitch (rotation around the X-axis).
     *
     * @return A {@link Matrix3} representing the pitch rotation.
     */
    @NotNull
    @Contract(value=" -> new",pure=true)
    Matrix3 rotPitch();

    /**
     * Creates a rotation matrix for yaw (rotation around the Y-axis).
     *
     * @return A {@link Matrix3} representing the yaw rotation.
     */
    @NotNull
    @Contract(value=" -> new",pure=true)
    Matrix3 rotYaw();

    /**
     * Creates a rotation matrix for roll (rotation around the Z-axis).
     *
     * @return A {@link Matrix3} representing the roll rotation.
     */
    @NotNull
    @Contract(value=" -> new",pure=true)
    Matrix3 rotRoll();
    @NotNull
    @Contract(value=" -> new",pure=true)
    Matrix3 matrix();
}
