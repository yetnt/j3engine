package com.j3d.engine.geometry.geo3d.rot;

import com.j3d.engine.geometry.geo3d.matrix.MatrixInterface;
import com.j3d.engine.geometry.geo3d.matrix.MatrixMath;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

/**
 * Represents an object's orientation in 3D space using Tait-Bryan angles (yaw, pitch, and roll).
 * <p>
 * This class stores angles in degrees and provides methods to generate transformation matrices
 * for converting vectors between world space and a local (camera) space. It acts as a factory
 * for {@link CamToWorldDirection} and {@link WorldToCamDirection} transformers.
 * <p>
 * The primary matrix generated via {@link RotationMatrixDirection#matrix()} is the view matrix, which transforms
 * coordinates from the world space into the camera's local space.
 * @author Lehlogonolo Poole
 * @see CamToWorldDirection
 * @see WorldToCamDirection
 * @see RotationMatrixDirection
 */
public class Rotation {
    /**
     * Pitch: Rotation around the X-axis, in degrees.
     */
    private double pitch;
    /**
     * Yaw: Rotation around the Y-axis, in degrees.
     */
    private double yaw;
    /**
     * Roll: Rotation around the Z-axis, in degrees.
     */
    private double roll;

    /**
     * Constructs a new Rotation object with the specified angles.
     *
     * @param pitch The pitch angle in degrees (rotation around the X-axis).
     * @param yaw   The yaw angle in degrees (rotation around the Y-axis).
     * @param roll  The roll angle in degrees (rotation around the Z-axis).
     */
    public Rotation(double pitch, double yaw, double roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    /**
     * Creates a transformer for converting directions from **world space to camera space**.
     *
     * @return A new {@link WorldToCamDirection} instance configured with this rotation's angles.
     */
    public WorldToCamDirection worldToCam() {
        return new WorldToCamDirection(
                Math.toRadians(pitch),
                Math.toRadians(yaw),
                Math.toRadians(roll)
        );
    }

    /**
     * Creates a transformer for converting directions from **camera space to world space**.
     *
     * @return A new {@link CamToWorldDirection} instance configured with this rotation's angles.
     */
    public CamToWorldDirection camToWorld() {
        return new CamToWorldDirection(
                Math.toRadians(pitch),
                Math.toRadians(yaw),
                Math.toRadians(roll)
        );
    }

    /**
     * Gets the yaw
     * @return The yaw angle in degrees.
     */
    public double getYaw() {
        return this.yaw;
    }

    /**
     * Gets the pitch
     * @return The pitch angle in degrees.
     */
    public double getPitch() {
        return this.pitch;
    }

    /**
     * Gets the roll
     * @return The roll angle in degrees.
     */
    public double getRoll() {
        return this.roll;
    }

    /**
     * Sets the pitch
     * @param pitch The new pitch angle in degrees.
     */
    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    /**
     * Sets the yaw
     * @param yaw The new yaw angle in degrees.
     */
    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    /**
     * Sets the roll
     * @param roll The new roll angle in degrees.
     */
    public void setRoll(double roll) {
        this.roll = roll;
    }

    /**
     * Converts the rotation angles from degrees to radians and returns them as a Vector3.
     * The components of the vector are (pitch, yaw, roll).
     *
     * @return A {@link Vector3} containing the pitch, yaw, and roll angles in radians.
     */
    public Vector3 toRadVector3() {
        return new Vector3(Math.toRadians(getPitch()), Math.toRadians(getYaw()), Math.toRadians(getRoll()));
    }

    /**
     * Creates a copy of this Rotation object.
     * @return A new Rotation object with the same pitch, yaw, and roll angles as this one.
     */
    public Rotation copy() {
        return new Rotation(pitch, yaw, roll);
    }
}
