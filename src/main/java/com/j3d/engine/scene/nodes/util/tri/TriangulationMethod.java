/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j3d.engine.scene.nodes.util.tri;

import com.j3d.StaticRefs;
import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.math.matrix.Vector3;
import java.util.ArrayList;

import com.j3d.engine.scene.SceneException;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author yetnt
 */
public abstract sealed class TriangulationMethod permits EarClipping, Delaunay, Seidel {

    public static final Fetch Fetch = new Fetch();

    public static final double COPLANAR_TOLERANCE = 1e-1;

    public record Tri(GPoint A, GPoint B, GPoint C) {

        @NotNull
        @Contract(" -> new")
        public Tri flip() {
            return new Tri(C, B, A);
        }

        @NotNull
        @Contract(" -> new")
        public Triangle toTriangle() {
            return new Triangle(A.toPoint(), B.toPoint(), C.toPoint());
        }
    }

    public record Triangulated(
            ArrayList<Vector3> newPoints,
            ArrayList<Tri> tris
    ) {}

    @Nullable
    public abstract Triangulated apply(@NotNull ArrayList<GPoint> v3s) throws InvalidTriangulationException;

    /**
     * Verifies that all points in the polygon lie along the same plane in 3D space.
     *
     * @param gPoints the list of polygon vertices
     * @throws InvalidTriangulationException if points are collinear or not coplanar
     */
    protected void verifyCoplanar(@NotNull ArrayList<GPoint> gPoints) throws InvalidTriangulationException {
        if (gPoints.size() < 3) return;

        Vector3 p0 = gPoints.getFirst().getPivot();
        Vector3 normal = null;

        for (int i = 1; i < gPoints.size() - 1; i++) {
            Vector3 v1 = gPoints.get(i).getPivot().sub(p0);
            for (int j = i + 1; j < gPoints.size(); j++) {
                Vector3 v2 = gPoints.get(j).getPivot().sub(p0);
                Vector3 cross = v1.cross(v2);
                if (cross.dot(cross) > 1e-9) {
                    normal = cross.normalize();
                    break;
                }
            }
            if (normal != null) break;
        }

        if (normal == null) {
            StaticRefs.getErrs().handleThenThrow(new InvalidTriangulationException("All polygon points are collinear.")
                    .code(702));
            return;
        }

        for (int i = 0; i < gPoints.size(); i++) {
            Vector3 pt = gPoints.get(i).getPivot();
            double dist = Math.abs(pt.sub(p0).dot(normal));
            if (dist > COPLANAR_TOLERANCE) {
                StaticRefs.getErrs().emitThenThrow(
                        new InvalidTriangulationException("Polygon points are not coplanar (point at index " + i + " deviates from plane by " + dist + ").")
                                .code(703)
                );
                return;
            }
        }
    }

    public static class InvalidTriangulationException extends SceneException implements J3DFatal {
        @Override
        public boolean terminate() {
            return false;
        }

        public InvalidTriangulationException(String message) {
            super("Could not triangulate polygon: " + message);
        }

        public static J3DError lessThan3Points() {
            return new InvalidTriangulationException("Polygon has less than 3 points!"){
                @Override
                public boolean terminate() {
                    return true;
                }
            }.code(701);
        }
    }
}
