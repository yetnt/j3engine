package com.j3d.engine.math.plane;

import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.utility.generic.SamePair;

import java.util.Objects;

/**
 * A plane is defined by a normal vector and a point on the plane (origin).
 * @author Lehlogonolo Poole
 * @param origin The point on the plane
 * @param normal The normal vector pointing outwards from the plane
 */
public record NormalPlane(
        Vector3 origin,
        Vector3 normal
) {

    /**
     * Converts this {@code NormalPlane} into an {@code AxisPlane}.
     * This method calculates two orthogonal vectors (x and y) that lie within the plane,
     * forming a local coordinate system for the plane.
     * @return A new {@code AxisPlane} representing the same plane.
     */
    public AxisPlane toAxisPlane() {
        Vector3 ref = Math.abs(normal.getY()) < 0.9
                ? Vector3.Y(1)
                : Vector3.X(1);
        Vector3 x = normal.cross(ref).normalize();
        Vector3 y = normal.cross(x).normalize();
        return new AxisPlane(origin, x, y);
    }

    /**
     * Checks if a given 3D point lies on this plane.
     * @param pos The 3D point to check.
     * @return {@code true} if the point is on the plane (within a small epsilon tolerance), {@code false} otherwise.
     */
    public boolean onPlane(Vector3 pos) {
        return Math.abs(pos.dot(normal)) < EPSILON;
    }

    /**
     * Returns a {@code SamePair} containing two references to this {@code NormalPlane}.
     * This can be useful in contexts where a pair of identical objects is required.
     * @return A {@code SamePair} with both elements being this {@code NormalPlane}.
     */
    public SamePair<NormalPlane> pair() {
        return new SamePair<>(this, this);
    }

    /**
     * Creates a {@code NormalPlane} from three points in 3D space.
     * The normal vector is calculated using the cross product of two vectors formed by the points,
     * and is then normalised. The origin of the plane is set to point A.
     * @param A The first point.
     * @param B The second point.
     * @param C The third point.
     * @return A new {@code NormalPlane} defined by the three points.
     */
    public static NormalPlane from(Vector3 A, Vector3 B, Vector3 C) {
        Vector3 normal = (B.sub(A)).cross(C.sub(A)).normalize();
        return new NormalPlane(A, normal);
    }

    /**
     * Creates a {@code NormalPlane} from a {@code GTri} (triangle).
     * The origin of the plane is set to the pivot point of the first leg's first point of the triangle,
     * and the normal vector is taken directly from the triangle's normal.
     * @param tri The {@code GTri} from which to create the {@code NormalPlane}.
     * @return A new {@code NormalPlane} defined by the triangle.
     */
    public static NormalPlane from(GTri tri) {
        return new NormalPlane(
                tri.getLegA().getA().getPivot(),
                tri.normal()
        );
    }

    /**
     * Creates a {@code NormalPlane} that is parallel to the XZ-plane.
     * The normal vector is {@code Vector3.Y} (pointing along the positive Y-axis).
     * @param origin The origin point on the plane.
     * @return A new {@code NormalPlane} parallel to the XZ-plane.
     */
    public static NormalPlane XZ(Vector3 origin) {
        return new NormalPlane(origin, Vector3.Y);
    }

    /**
     * Creates a {@code NormalPlane} that is parallel to the XY-plane.
     * The normal vector is {@code Vector3.Z} (pointing along the positive Z-axis).
     * @param origin The origin point on the plane.
     * @return A new {@code NormalPlane} parallel to the XY-plane.
     */
    public static NormalPlane XY(Vector3 origin) {
        return new NormalPlane(origin, Vector3.Z);
    }

    /**
     * Creates a {@code NormalPlane} that is parallel to the ZY-plane (or YZ-plane).
     * The normal vector is {@code Vector3.X} (pointing along the positive X-axis).
     * @param origin The origin point on the plane.
     * @return A new {@code NormalPlane} parallel to the ZY-plane.
     */
    public static NormalPlane ZY(Vector3 origin) {
        return new NormalPlane(origin, Vector3.X);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NormalPlane that = (NormalPlane) o;
        return Objects.equals(origin(), that.origin()) && Objects.equals(normal(), that.normal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin(), normal());
    }

    @Override
    public String toString() {
        return
                "NormalPlane{" +
                "origin=" + origin +
                ", normal=" + normal +
                '}';
    }

    public static final double EPSILON = 1e-9;
}
