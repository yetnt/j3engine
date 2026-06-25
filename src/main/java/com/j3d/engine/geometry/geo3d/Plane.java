package com.j3d.engine.geometry.geo3d;

import com.j3d.engine.geometry.geo3d.matrix.Vector3;

/**
 * A plane is defined by two vectors. These 2 vectors need to be orthogonal to each other.
 * @author Lehlogonolo Poole
 * @param v1 The first vector
 * @param v2 The second vector
 */
public record Plane(
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
}
