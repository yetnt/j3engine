package com.j3d.engine.interact.selection;

import com.j3d.Main;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Vector3;

import java.awt.*;

/**
 * A SelectionQuery represents a selection made by the user.
 * It is represented as a rectangle defined by two ScreenPoints (the corners).
 * It can be used to check whether certain GObjects are within the selection.
 */
public class SelectionQuery extends Rectangle {
    public SelectionType type;
    public ScreenPoint[] points = new ScreenPoint[2];
    public SelectionQuery(ScreenPoint i, ScreenPoint ii, SelectionType t) {
        super(Math.min(i.x, ii.x), Math.min(i.y, ii.y), Math.abs(i.x - ii.x), Math.abs(i.y - ii.y));
        points[0] = i;
        points[1] = ii;
        type = t;
    }

    /**
     * Whether the Selection has the given GObject.
     * @param obj The GObject to check.
     * @param soft If false, we strictly check whether the GObject is fully contained within the selection.
     *             Otherwise, we check whether the selection does so much as intersect with the GObject.
     */
    public boolean has(GObject obj, boolean soft) {
        return switch (obj) {
            case GTri t -> has(t, soft);
            case GLine l -> has(l, soft);
            case GPoint p -> has(p);
            default -> throw new IllegalStateException("Unexpected value: " + obj);
        };
    }

    /**
     * Whether the Selection has the triangle.
     * @param triangle The triangle to check.
     * @param soft If false, we strictly check whether the triangle is fully contained within the selection.
     *             Otherwise, we check whether the selection does so much as
     *             intersect with ay of the lines of the triangle.
     * @return true if all the points are within the triangle
     */
    public boolean has(GTri triangle, boolean soft) {
        GPoint[] pts = new GPoint[3];
        pts[0] = triangle.getLegA().getStart();
        pts[1] = triangle.getLegB().getStart();
        pts[2] = triangle.getLegC().getStart();
        if (soft) {
            if (has(triangle.getLegA(), true) ||
                    has(triangle.getLegB(), true) ||
                    has(triangle.getLegC(), true)) return true;

            for (GPoint pt : pts) {
                if (has(pt)) return true;
            }
            return false;
        }

        for (GPoint pt : pts) {
            if (!has(pt)) return false;
        }
        return true;
    }

    /**
     * Whether the selection has the given line.
     * @param line The line to check.
     * @param soft If false, we strictly check whether the line is fully contained within the selection.
     *             Otherwise, we check if the line intersects with any of the lines of the selection.
     * @return true if the selection has the line. False otherwise.
     */
    public boolean has(GLine line, boolean soft) {
        if (soft) {
            if (intersectsWith(line)) return true;
            return has(line.getStart()) || has(line.getEnd());
        } else {
            return has(line.getStart()) && has(line.getEnd());
        }
    }

    /**
     * Whether the selection has the given point.
     * Unlike the other overrides which have a soft parameter. A point is either inside or outside the selection.
     * @param point The point to check.
     * @return true if the selection has the point. False otherwise.
     */
    public boolean has(GPoint point) {
        return contains(point.getPivot().toPoint(Main.camera).toScreen(Main.renderer).toSwingPoint());
    }

    public boolean intersectsWith(GLine line) {
        ScreenPoint A = line.getStart().getPivot().toPoint(Main.camera).toScreen(Main.renderer);
        ScreenPoint B = line.getEnd().getPivot().toPoint(Main.camera).toScreen(Main.renderer);

        ScreenPoint rectA = points[0];
        ScreenPoint rectB = new ScreenPoint(points[0].x, points[1].y);
        ScreenPoint rectC = points[1];
        ScreenPoint rectD = new ScreenPoint(points[1].x, points[0].y);

        return segmentsIntersect(A, B, rectA, rectB) ||
                segmentsIntersect(A, B, rectB, rectC) ||
                segmentsIntersect(A, B, rectC, rectD) ||
                segmentsIntersect(A, B, rectD, rectA);
    }

    private boolean segmentsIntersect(ScreenPoint p1, ScreenPoint p2, ScreenPoint q1, ScreenPoint q2) {
        int o1 = orientation(p1, p2, q1);
        int o2 = orientation(p1, p2, q2);
        int o3 = orientation(q1, q2, p1);
        int o4 = orientation(q1, q2, p2);

        // General case
        if (o1 != o2 && o3 != o4) return true;

        // Special cases
        if (o1 == 0 && onSegment(p1, q1, p2)) return true;
        if (o2 == 0 && onSegment(p1, q2, p2)) return true;
        if (o3 == 0 && onSegment(q1, p1, q2)) return true;
        return o4 == 0 && onSegment(q1, p2, q2);
    }

    private int orientation(ScreenPoint a, ScreenPoint b, ScreenPoint c) {
        int val = (b.y - a.y) * (c.x - b.x) - (b.x - a.x) * (c.y - b.y);
        if (val == 0) return 0; // colinear
        return (val > 0) ? 1 : 2; // clockwise or counterclockwise
    }

    private boolean onSegment(ScreenPoint a, ScreenPoint b, ScreenPoint c) {
        return Math.min(a.x, c.x) <= b.x && b.x <= Math.max(a.x, c.x) &&
                Math.min(a.y, c.y) <= b.y && b.y <= Math.max(a.y, c.y);
    }
}
