package com.j3d.engine.geometry.geo3d;

import com.j3d.engine.geometry.base.BasePoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vector3 is a simple class that holds 3 double values, x, y, and z.
 * <p>
 *     In it's current state, it is not used by the engine, but will be when all 2d geometry is implemented and battle tested.
 *</p>
 */
public class Vector3 {
    private final double X;
    private final double Y;
    private final double Z;

    public boolean isNotEmpty() {
        return X != Double.MAX_VALUE || Y != Double.MAX_VALUE || Z != Double.MAX_VALUE;
    }

    public Vector3() {
        X = Double.MAX_VALUE;
        Y = Double.MAX_VALUE;
        Z = Double.MAX_VALUE;
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
     * Normalizes the given vector.
     * @param v The vector to normalize.
     * @return A new normalized Vector3.
     */
    public static Vector3 normalize(Vector3 v) {
        double magnitude = Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
        return new Vector3(v.getX() / magnitude, v.getY() / magnitude, v.getZ() / magnitude);
    }

    /**
     * Projects this 3D vector onto a 2D Cartesian plane based on the camera's properties.
     *
     * @param camera The camera defining the viewpoint and projection.
     * @return The resulting 2D {@link CartesianPoint}.
     */
    public CartesianPoint toPoint(Camera camera) {
        // camera properties
        Vector3 cameraPos = camera.getPosition();
        Rotation cameraRot = camera.getRotation();
        Vector3 projectionPlane = camera.getProjectionPlane();

        // translate the point relative to the camera's position
        double pointX = this.getX() - cameraPos.getX();
        double pointY = this.getY() - cameraPos.getY();
        double pointZ = this.getZ() - cameraPos.getZ();

        // rotate the point based on the camera's rotation (Tait-Bryan angles)
        double sYaw = Math.sin(cameraRot.getYaw());
        double cYaw = Math.cos(cameraRot.getYaw());
        double sPitch = Math.sin(cameraRot.getPitch());
        double cPitch = Math.cos(cameraRot.getPitch());
        double sRoll = Math.sin(cameraRot.getRoll());
        double cRoll = Math.cos(cameraRot.getRoll());

        // Apply rotation matrix (Yaw, then Pitch, then Roll)
        // This is a combined rotation matrix multiplication.
        double d_x = cYaw * (sPitch * pointY + cPitch * pointZ) - sYaw * pointX;
        double common = sYaw * (sPitch * pointY + cPitch * pointZ) + cYaw * pointX;
        double d_y = sRoll * (cPitch * pointY - sPitch * pointZ) + cRoll * common;
        double d_z = cRoll * (cPitch * pointY - sPitch * pointZ) - sRoll * common;

        // Apply perspective projection
        // Check to avoid division by zero if a point is at the same z-level as the camera's focal point
        if (d_z <= 0) {
            // This point is behind or on the camera plane, it cannot be projected.
            // Returning a point far off-screen is one way to handle this.
            return new CartesianPoint(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        /*
        if (d_z < 1e-5) { // Use a small epsilon for a near clipping plane
            // This point is behind or on the camera plane, it cannot be projected.
            // Returning a point far off-screen is one way to handle this.
            return new CartesianPoint(Double.MAX_VALUE, Double.MAX_VALUE);
        }
         */

        double b_x = (projectionPlane.getZ() / d_z) * d_x - projectionPlane.getX();
        double b_y = (projectionPlane.getZ() / d_z) * d_y - projectionPlane.getY();

        return new CartesianPoint(b_x, b_y);
    }

    public CartesianPoint toPoint2(Camera cam) {
        Vector3 a = this;
        Vector3 c = cam.getPosition();
        Vector3 theta = cam.getRotation().toRadVector3();
        Vector3 e = cam.getProjectionPlane();

        double x = a.getX() - c.getX();
        double y = a.getY() - c.getY();
        double z = a.getZ() - c.getZ();

        double sinX = Math.sin(theta.getX());
        double sinY = Math.sin(theta.getY());
        double sinZ = Math.sin(theta.getZ());
        double cosX = Math.cos(theta.getX());
        double cosY = Math.cos(theta.getY());
        double cosZ = Math.cos(theta.getZ());

        double dx = cosY * (sinZ * y + cosZ * x) - sinY * z;
        double dy = sinX * (cosY * z + sinY * (sinZ * y + cosZ * x)) + cosX * (cosZ * y - sinZ * x);
        double dz = cosX * (cosY * z + sinY * (sinZ * y + cosZ * x)) - sinX * (cosZ * y - sinZ * x);

        if (dz < 1e-18) return new CartesianPoint(Double.MAX_VALUE, Double.MIN_VALUE); // or skip rendering this point

        double scale = 1;
        double bz = scale * ((e.getZ() / dz) * dx + e.getX());
        double by = scale * ((e.getZ() / dz) * dy + e.getY());

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

    public Vector3 add(Vector3 v) {
        return new Vector3(this.X + v.X, this.Y + v.Y, this.Z + v.Z);
    }

    public double magnitude() {
        return Math.sqrt(this.X * this.X + this.Y * this.Y + this.Z * this.Z);
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "X=" + X +
                ", Y=" + Y +
                ", Z=" + Z +
                '}';
    }
}
