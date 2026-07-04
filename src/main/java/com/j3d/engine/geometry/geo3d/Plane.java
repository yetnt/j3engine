package com.j3d.engine.geometry.geo3d;

import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.utility.generic.SamePair;

import java.util.Objects;

/**
 * A plane is defined by two vectors. These 2 vectors need to be orthogonal to each other.
 * @author Lehlogonolo Poole
 * @param v1 The first vector
 * @param v2 The second vector
 */
public record Plane  (
        Vector3 v1,
        Vector3 v2
) {
    /**
     * Normalises the vectors
     * @return A new plane with the normalised vectors
     */
    public Plane normalize() {
        return new Plane(
                v1.normalize(),
                v2.normalize()
        );
    }

    /**
     * Returns a {@link SamePair} containing two references to this plane.
     * @return A {@link SamePair} of this plane.
     */
    public SamePair<Plane> pair() {
        return new SamePair<>(this, this);
    }

    /**
     * Returns a plane representing the XZ-plane.
     * @return The XZ-plane.
     */
    public static Plane XZ() {
        return new Plane(Vector3.X(1), Vector3.Z(1));
    }
    /**
     * Returns a plane representing the XY-plane.
     * @return The XY-plane.
     */
    public static Plane XY() {
        return new Plane(Vector3.X(1), Vector3.Y(1));
    }
    /**
     * Returns a plane representing the ZY-plane.
     * @return The ZY-plane.
     */
    public static Plane ZY() {
        return new Plane(Vector3.Z(1), Vector3.Y(1));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Plane plane = (Plane) o;
        return Objects.equals(v1(), plane.v1()) && Objects.equals(v2(), plane.v2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(v1(), v2());
    }
}
