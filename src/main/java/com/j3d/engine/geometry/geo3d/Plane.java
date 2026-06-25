package com.j3d.engine.geometry.geo3d;

import com.j3d.engine.geometry.geo3d.matrix.Vector3;

public record Plane(
        Vector3 v1,
        Vector3 v2
) {
    public Plane normalize() {
        return new Plane(
                v1.normalize(),
                v2.normalize()
        );
    }
}
