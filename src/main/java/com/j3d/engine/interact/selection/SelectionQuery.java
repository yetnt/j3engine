package com.j3d.engine.interact.selection;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.geometry.Segment;
import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.nodes.geometry.*;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * A SelectionQuery represents a selection made by the user.
 * It is represented as a rectangle defined by two ScreenPoints (the corners).
 * It can be used to check whether certain GObjects are within the selection.
 * @see SelectionType
 * @see SelectionManager
 * @see SelectionMouseOwner
 * @see SelectionUI
 * @see SelectionUtils
 * @author Lehlogonolo Poole
 */
public class SelectionQuery extends Rectangle {
    public SelectionType type;
    public ScreenPoint[] points = new ScreenPoint[2];

    /**
     * Constructs a SelectionQuery object with the given screen points and selection type.
     * @param i The first screen point.
     * @param ii The second screen point.
     * @param t The type of selection.
     */
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
            case GTri t -> has(t.toTriangle(), soft);
            case GLine l -> has(l.toSegment(), soft);
            case GPoint p -> has(p.toPoint());
            case GCurve curve -> has(curve, soft);
            default -> throw new IllegalStateException("Unexpected value: " + obj);
        };
    }

    public boolean has(GCurve curve, boolean soft) {
        ArrayList<RenderState<Segment, GObject>> segments = curve.getDecomposeList();
        if (soft) {
            // any segment can match to be valid
            return
                    segments
                            .stream()
                            .map(RenderState::getPure)
                            .anyMatch(s -> has(s, false));
        } else {
            // all segments must be within
            return
                    segments
                            .stream()
                            .map(RenderState::getPure)
                            .allMatch(s -> has(s, false));
        }
    }

    /**
     * Whether the Selection has the triangle.
     * @param triangle The triangle to check.
     * @param soft If false, we strictly check whether the triangle is fully contained within the selection.
     *             Otherwise, we check whether the selection does so much as
     *             intersect with ay of the lines of the triangle.
     * @return true if all the points are within the triangle
     */
    public boolean has(Triangle triangle, boolean soft) {
        Vector3[] pts = new Vector3[3];
        pts[0] = triangle.getP1();
        pts[1] = triangle.getP2();
        pts[2] = triangle.getP3();
        if (soft) {
            if (has(new Segment(pts[0], pts[1]), true) ||
                    has(new Segment(pts[1], pts[2]), true) ||
                    has(new Segment(pts[2], pts[0]), true)) return true;

            for (Vector3 pt : pts) {
                if (has(pt)) return true;
            }
            return false;
        }

        for (Vector3 pt : pts) {
            if (!has(pt)) return false;
        }
        return true;
    }

    public boolean has(Segment line, boolean soft) {
        if (soft) {
            if (intersectsWith(line)) return true;
        }
        return has(line.getStart()) && has(line.getEnd());
    }

    public boolean has(Vector3 point) {
        return contains(point.toPoint(StaticRefs.getCamera()).toScreen().toSwingPoint());
    }

    public boolean intersectsWith(Segment line) {
        ScreenPoint A = line.getStart().toPoint(StaticRefs.getCamera()).toScreen();
        ScreenPoint B = line.getEnd().toPoint(StaticRefs.getCamera()).toScreen();

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
