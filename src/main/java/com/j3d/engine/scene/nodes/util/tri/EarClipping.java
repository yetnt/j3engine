package com.j3d.engine.scene.nodes.util.tri;


import com.j3d.StaticRefs;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.math.plane.NormalPlane;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.base.Winding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;


public final class EarClipping extends TriangulationMethod {
    @NotNull
    @Override
    public Triangulated apply(@NotNull ArrayList<GPoint> gPoints) throws InvalidTriangulationException {
        if (gPoints.size() < 3) StaticRefs.getErrs().emitThenThrow(InvalidTriangulationException.lessThan3Points());
        verifyCoplanar(gPoints);

        ArrayList<Vector3> newPoints = new ArrayList<>(); // stays empty for plain ear clipping
        ArrayList<Tri> tris = new ArrayList<>();

        if (gPoints.size() == 3) {
            tris.add(new Tri(gPoints.getFirst(), gPoints.get(1), gPoints.get(2)));
            return new Triangulated(newPoints, tris);
        }

        Vector3 normal = Winding.normal(
                gPoints.getFirst().getPivot(),
                gPoints.get(1).getPivot(),
                gPoints.get(2).getPivot());
        NormalPlane normPlane = new NormalPlane(gPoints.getFirst().getPivot(), normal);
        AxisPlane axisPlane = normPlane.toAxisPlane();

        Map<UUID, GPoint> lookup = new HashMap<>();
        ArrayList<CartesianPoint> workingPoints = new ArrayList<>();
        for (GPoint g : gPoints) {
            CartesianPoint cp = axisPlane.fromWorld(g.getPivot());
            UUID id = UUID.randomUUID();
            cp.setId(id);
            lookup.put(id, g);
            workingPoints.add(cp);
        }

        while (workingPoints.size() > 3) {
            boolean earFound = false;

            for (int i = 0; i < workingPoints.size(); i++) {
                int prevIdx = (i - 1 + workingPoints.size()) % workingPoints.size();
                int nextIdx = (i + 1) % workingPoints.size();

                CartesianPoint prev = workingPoints.get(prevIdx);
                CartesianPoint curr = workingPoints.get(i);
                CartesianPoint next = workingPoints.get(nextIdx);

                if (!isConvex(prev, curr, next)) continue;

                boolean isEarClean = true;
                for (int j = 0; j < workingPoints.size(); j++) {
                    if (j == prevIdx || j == i || j == nextIdx) continue;
                    if (isPointInTriangle(workingPoints.get(j), prev, curr, next)) {
                        isEarClean = false;
                        break;
                    }
                }

                if (isEarClean) {
                    tris.add(new Tri(
                            lookup.get(prev.getId()),
                            lookup.get(curr.getId()),
                            lookup.get(next.getId())));
                    workingPoints.remove(i);
                    earFound = true;
                    break;
                }
            }

            if (!earFound) StaticRefs.getErrs().emitThenThrow(
                    new InvalidTriangulationException("Self-intersecting or broken polygon.")
                            .code(4201)
            ); // self-intersecting or broken polygon
        }

        // The last 3 remaining points form the final triangle
        tris.add(new Tri(
                lookup.get(workingPoints.get(0).getId()),
                lookup.get(workingPoints.get(1).getId()),
                lookup.get(workingPoints.get(2).getId())));

        return new Triangulated(newPoints, tris);
    }

    private boolean isConvex(@NotNull CartesianPoint p1, @NotNull CartesianPoint p2, @NotNull CartesianPoint p3) {
        double dx1 = p2.getX() - p1.getX();
        double dy1 = p2.getY() - p1.getY();
        double dx2 = p3.getX() - p2.getX();
        double dy2 = p3.getY() - p2.getY();
        
        return (dx1 * dy2 - dy1 * dx2) > 0;
    }

    private boolean isPointInTriangle(@NotNull CartesianPoint p, @NotNull CartesianPoint a, @NotNull CartesianPoint b, @NotNull CartesianPoint c) {
        double v0x = c.getX() - a.getX(), v0y = c.getY() - a.getY();
        double v1x = b.getX() - a.getX(), v1y = b.getY() - a.getY();
        double v2x = p.getX() - a.getX(), v2y = p.getY() - a.getY();

        double dot00 = v0x * v0x + v0y * v0y;
        double dot01 = v0x * v1x + v0y * v1y;
        double dot02 = v0x * v2x + v0y * v2y;
        double dot11 = v1x * v1x + v1y * v1y;
        double dot12 = v1x * v2x + v1y * v2y;

        double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }
}
