package com.j3d.engine.math.plane;

import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.utility.generic.tuple.SamePair;

import java.util.Objects;

/**
 * A plane defined by an origin point and two orthogonal vectors (v1 and v2) that lie within the plane.
 * These vectors define the local coordinate system of the plane.
 * @author Lehlogonolo Poole
 * @param origin The point on the plane
 * @param v1 The first orthogonal vector
 * @param v2 The second orthogonal vector
 * @implNote To check if a point lies on this specified plane, use {@link NormalPlane#onPlane(Vector3)}
 */
public record AxisPlane(
        Vector3 origin,
        Vector3 v1,
        Vector3 v2
) {
    public static AxisPlane usingOrigin(Vector3 origin, AxisPlane original) {
        return new AxisPlane(
                origin,
                original.v1(),
                original.v2()
        );
    }

    public Vector3 toWorld(CartesianPoint point) {
        return origin
                .add(v1.scale(point.x))
                .add(v2.scale(point.y));
    }
    /**
     * Normalises the vectors
     * @return A new plane with the normalised vectors
     */
    public AxisPlane normalize() {
        return new AxisPlane(
                origin,
                v1.normalize(),
                v2.normalize()
        );
    }

    /**
     * Converts this AxisPlane to a {@link NormalPlane}.
     * @return A new {@link NormalPlane} with the same origin and a normal derived from this AxisPlane.
     */
    public NormalPlane toNormalPlane() {
        return new NormalPlane(origin, v1.cross(v2).normalize());
    }

    /**
     * Returns a {@link SamePair} containing two references to this plane.
     * @return A {@link SamePair} of this plane.
     */
    public SamePair<AxisPlane> pair() {
        return new SamePair<>(this, this);
    }


    /**
     * Returns a {@link SamePair} containing two new {@link AxisPlane} instances, each with a different origin.
     * @param origin1 The origin for the first {@link AxisPlane}.
     * @param origin2 The origin for the second {@link AxisPlane}.
     * @return A {@link SamePair} of {@link AxisPlane} instances with the specified origins.
     * @implNote This method discards the original {@link #origin} in favour of the new ones.
     */
    public SamePair<AxisPlane> sameAxes(Vector3 origin1, Vector3 origin2) {
        return AxisPlane.sameAxes(
                v1, v2, origin1, origin2
        );
    }

    /**
     * Creates a {@link SamePair} of {@link AxisPlane} instances with the same basis vectors but different origins.
     * @param v1 The first orthogonal vector for both planes.
     * @param v2 The second orthogonal vector for both planes.
     * @param originA The origin for the first {@link AxisPlane}.
     * @param originB The origin for the second {@link AxisPlane}.
     * @return A {@link SamePair} of {@link AxisPlane} instances.
     */
    public static SamePair<AxisPlane> sameAxes(Vector3 v1, Vector3 v2, Vector3 originA, Vector3 originB) {
        return new SamePair<>(
                new AxisPlane(originA, v1, v2)
                , new AxisPlane(originB, v1, v2));
    }

    /**
     * Returns a plane representing the XZ-plane.
     * @param origin The origin of the plane.
     * @return The XZ-plane.
     */
    public static AxisPlane XZ(Vector3 origin) {
        return new AxisPlane(origin, Vector3.X, Vector3.Z);
    }
    /**
     * Returns a plane representing the XY-plane.
     * @param origin The origin of the plane.
     * @return The XY-plane.
     */
    public static AxisPlane XY(Vector3 origin) {
        return new AxisPlane(origin, Vector3.X, Vector3.Y);
    }
    /**
     * Returns a plane representing the ZY-plane.
     * @param origin The origin of the plane.
     * @return The ZY-plane.
     */
    public static AxisPlane ZY(Vector3 origin) {
        return new AxisPlane(origin, Vector3.Z, Vector3.Y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AxisPlane axisPlane = (AxisPlane) o;
        return
                Objects.equals(origin(), axisPlane.origin()) &&
                Objects.equals(v1(), axisPlane.v1()) && Objects.equals(v2(), axisPlane.v2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin(), v1(), v2());
    }

    @Override
    public String toString() {
        return
                "AxisPlane{" +
                "origin=" + origin +
                ", v1=" + v1 +
                ", v2=" + v2 +
                '}';

    }
}
