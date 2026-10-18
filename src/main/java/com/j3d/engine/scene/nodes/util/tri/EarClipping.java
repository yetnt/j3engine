package com.j3d.engine.scene.nodes.util.tri;


import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.math.plane.NormalPlane;
import com.j3d.engine.scene.nodes.geometry.base.Winding;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class EarClipping extends Triangulation {
    @Nullable
    @Override
    public Triangulated apply(@NotNull ArrayList<Vector3> v3s) {
        
        if (v3s.size() < 3) return null; 
        
        // method doesnt enforce points are sequential the implementor shouldve done that
        // also doesnt enforce on same plane. implementors problem
        
        ArrayList<Vector3> newPoints = new ArrayList<>(); // any newly created points by either segment intersections or likewise
        ArrayList<Segment> segments = new ArrayList<>(); // list of records which just store 2 V3s that should be joined.
        
        if (v3s.size() == 3) {
            segments.add(new Segment(v3s.get(0), v3s.get(1)));
            segments.add(new Segment(v3s.get(1), v3s.get(2)));
            segments.add(new Segment(v3s.get(2), v3s.get(0)));
            return new Triangulated(newPoints, segments);
        }
        
        Vector3 normal = Winding.normal(v3s.getFirst(), v3s.get(1), v3s.get(2));
        NormalPlane normPlane = new NormalPlane(v3s.getFirst(), normal); // use random vector as plane origin. shouldnt matter in this case
        AxisPlane axisPlane = normPlane.toAxisPlane();
        
        ArrayList<CartesianPoint> planarPoints = v3s
                .stream()
                .map(v3 -> {
                    CartesianPoint cp = axisPlane.fromWorld(v3);
                    cp.setId(v3.getId()); // so we can map existing v3s to their cartesian point versions.
                    return cp;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        
     
        // We work on a copy of planarPoints so we can destructively remove ear tips
        ArrayList<CartesianPoint> workingPoints = new ArrayList<>(planarPoints);
        
        // Build an O(1) map to quickly recover the original Vector3 reference using the ID stored in CartesianPoint
        Map<UUID, Vector3> worldLookup = v3s.stream()
                .collect(Collectors.toMap(Vector3::getId, v3 -> v3));

        while (workingPoints.size() > 3) {
            boolean earFound = false;

            for (int i = 0; i < workingPoints.size(); i++) {
                int prevIdx = (i - 1 + workingPoints.size()) % workingPoints.size();
                int currIdx = i;
                int nextIdx = (i + 1) % workingPoints.size();

                CartesianPoint prev = workingPoints.get(prevIdx);
                CartesianPoint curr = workingPoints.get(currIdx);
                CartesianPoint next = workingPoints.get(nextIdx);

                // 1. Check if the corner is convex
                if (isConvex(prev, curr, next)) {
                    
                    // 2. Ensure no other remaining polygon point lies inside this candidate triangle
                    boolean isEarClean = true;
                    for (int j = 0; j < workingPoints.size(); j++) {
                        if (j == prevIdx || j == currIdx || j == nextIdx) continue;
                        
                        if (isPointInTriangle(workingPoints.get(j), prev, curr, next)) {
                            isEarClean = false;
                            break;
                        }
                    }

                    // 3. If it's a valid ear, slice the corner off
                    if (isEarClean) {
                        Vector3 wPrev = worldLookup.get(prev.getId());
                        Vector3 wNext = worldLookup.get(next.getId());

                        // Add the diagonal segment splitting this ear from the rest of the shape
                        segments.add(new Segment(wPrev, wNext));

                        // Remove the ear tip from our active processing loop
                        workingPoints.remove(currIdx);
                        earFound = true;
                        break; 
                    }
                }
            }

            // Abort if the polygon structure is self-intersecting or broken
            if (!earFound) {
                return null;
            }
        }

        // Return the structural information; newPoints can stay empty for simple Ear Clipping 
        // as standard polygon slicing does not require synthesizing new geometric coordinates.
        return new Triangulated(newPoints, segments);
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
