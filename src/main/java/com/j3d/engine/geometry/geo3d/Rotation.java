package com.j3d.engine.geometry.geo3d;

/**
 * Represents a rotation in 3D space using Tait-Bryan angles (a type of Euler angle).
 * This class explicitly stores pitch, yaw, and roll to avoid ambiguity.
 */
public class Rotation {
    /**
     * Pitch: Rotation around the X-axis.
     */
    private double pitch;
    /**
     * Yaw: Rotation around the Y-axis.
     */
    private double yaw;
    /**
     * Roll: Rotation around the Z-axis.
     */
    private double roll;

    /**
     * Constructs a new Rotation object with the specified yaw, pitch, and roll values.
     *
     * @param pitch The pitch component of the rotation (rotation around the X-axis).
     * @param yaw   The yaw component of the rotation (rotation around the Y-axis).
     * @param roll  The roll component of the rotation (rotation around the Z-axis).
     */
    public Rotation(double pitch, double yaw, double roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public double getYaw() {
        return this.yaw;
    }

    public double getPitch() {
        return this.pitch;
    }

    public double getRoll() {
        return this.roll;
    }

     public void setPitch(double pitch) { this.pitch = pitch; }
     public void setYaw(double yaw) { this.yaw = yaw; }
     public void setRoll(double roll) { this.roll = roll; }

    /**
     * Converts the rotation angles from degrees to radians and returns them as a Vector3.
     * The order of components in the returned Vector3 is (roll_radians, pitch_radians, yaw_radians).
     *
     * @return A {@link Vector3} containing the roll, pitch, and yaw angles in radians.
     * @see Math#toRadians(double)
     * @see #getRoll()
     * @see #getPitch()
     * @see #getYaw()
     * @see Vector3
     */
    public Vector3 toRadVector3() {
        return new Vector3(Math.toRadians(getRoll()), Math.toRadians(getPitch()), Math.toRadians(getYaw()));
    }
}
