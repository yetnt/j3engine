package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.Renderer;
import com.j3d.engine.events.EventBroadcast;
import com.j3d.engine.events.EventEmitter;
import com.j3d.engine.events.EventType;
import com.j3d.engine.events.ObjectType;

import java.awt.*;
import java.util.*;

import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Vector3;

/**
 * GTri represents a Triangle. What'd you expect kau.
 */
public class GTri extends GObject{
    /**
     * Leg A, connected to Leg B and Leg C
     */
    private GLine LegA;
    /**
     * Leg B, conected to Leg A and Leg C
     */
    private GLine LegB;
    /**
     * Leg C, connected to Leg A and Leg B
     */
    private GLine LegC;

    /**
     * The normal of the triangle.
     */
    public Vector3 normal;

    public static class Event extends EventBroadcast {

        public GLine[] LegA = new GLine[2];
        public GLine[] LegB = new GLine[2];
        public GLine[] LegC = new GLine[2];

        /**
         * Default Constructor for EventBroadcast
         *
         * @param e The initiator of the broadcast.
         * @param r The Renderer instance.
         */
        public Event(EventEmitter e, Renderer r) {
            super(e, r);
        }
    }

    @Override
    public void draw(Renderer renderer, Graphics2D graphics2D, Camera cam) {
        graphics2D.setColor(col);
        graphics2D.fillPolygon(
                new int[] {
                        LegA.getStartPoint().toPoint(cam).toScreen(renderer).x,
                        LegA.getEndPoint().toPoint(cam).toScreen(renderer).x,
                        LegB.getEndPoint().toPoint(cam).toScreen(renderer).x
                },
                new int[] {
                        LegA.getStartPoint().toPoint(cam).toScreen(renderer).y,
                        LegA.getEndPoint().toPoint(cam).toScreen(renderer).y,
                        LegB.getEndPoint().toPoint(cam).toScreen(renderer).y
                },
                3
        );
        // The following code draws the normal
        graphics2D.setColor(Color.RED);
        renderer.drawLine3D(graphics2D, getPivot(), getPivot().add(normal.mult(0.5)), cam);
    }

    @Override
    public void onEvent(EventType event, EventBroadcast properties) {
        switch (event) {
            case NODE_UPDATED: {
                GLine.Event prop = (GLine.Event) properties;// In this case the line already moved itself, so we just need to redraw the tri.
                break;
            }
            case NODE_DELETED: {
                // Low-key, just delete ourselves. What is a triangle with 2 lines?
                deleteSelf(properties.renderer);
            }
        }
    }

    /**
     * Calculates the normal vector of the triangle given three vertices.
     * @param A The first vertex of the triangle.
     * @param B The second vertex of the triangle.
     * @param C The third vertex of the triangle.
     */
    public void calcNormal(Vector3 A, Vector3 B, Vector3 C) {
        Vector3 AB = B.sub(A);
        Vector3 AC = C.sub(A);
        normal = AB.cross(AC).normalize();
    }

    /**
     * Constructs a new GTri from 3 points.
     *
     * @param c The colour
     * @param A Point A
     * @param B Point B
     * @param C Point C
     */
    public GTri(Color c, GPoint A, GPoint B, GPoint C) {
        super(c);
//        attach(A, ObjectType.NODE);
//        attach(B, ObjectType.NODE);
//        attach(C, ObjectType.NODE);
//        A.attach(this, ObjectType.PARENT);
//        B.attach(this, ObjectType.PARENT);
//        C.attach(this, ObjectType.PARENT);
            // draw the triangle.

        LegA = new GLine(A, B);
        LegB = new GLine(B, C);
        LegC = new GLine(C, A);

        attach(LegA, ObjectType.NODE);
        attach(LegB, ObjectType.NODE);
        attach(LegC, ObjectType.NODE);
        LegA.attach(this, ObjectType.PARENT);
        LegB.attach(this, ObjectType.PARENT);
        LegC.attach(this, ObjectType.PARENT);

        setPivot(A.getPivot().add(B.getPivot()).add(C.getPivot()).div(3));

        calcNormal(A.getPivot(), B.getPivot(), C.getPivot());
    }

    public GTri(Color c, GLine A, GLine B, GLine C) {
        super(c);
        Vector3[] points = {
                A.getStartPoint(), A.getEndPoint(),
                B.getStartPoint(), B.getEndPoint(),
                C.getStartPoint(), C.getEndPoint()
        };

        // Count how many times each unique point appears
        Map<Vector3, Integer> pointCount = new HashMap<>();
        for (Vector3 p : points) {
            pointCount.merge(p, 1, Integer::sum);
        }

        // A valid triangle should have exactly 3 unique points, each appearing twice
        if (pointCount.size() != 3 || pointCount.values().stream().anyMatch(count -> count != 2)) {
            throw new IllegalArgumentException("Lines do not form a closed triangle.");
        }

        // Optional: Check for collinearity (This was used when points were defined on the 2d space.)
//        List<Vector3> vertices = new ArrayList<>(pointCount.keySet());
//        if (BasePoint.areCollinear(vertices.get(0), vertices.get(1), vertices.get(2))) {
//            throw new IllegalArgumentException("Points are collinear—no triangle formed.");
//        }

        attach(A, ObjectType.NODE);
        attach(B, ObjectType.NODE);
        attach(C, ObjectType.NODE);
        A.attach(this, ObjectType.PARENT);
        B.attach(this, ObjectType.PARENT);
        C.attach(this, ObjectType.PARENT);

        LegA = A;
        LegB = B;
        LegC = C;

        setPivot(A.getStartPoint().add(B.getStartPoint()).add(C.getStartPoint()).div(3));

        calcNormal(A.getStartPoint(), B.getStartPoint(), C.getStartPoint());
    }


    public GLine getLegA() {
        return LegA;
    }

    public GLine getLegB() {
        return LegB;
    }

    public GLine getLegC() {
        return LegC;
    }

    public void setLegA(GLine legA) {
        LegA = legA;
    }

    public void setLegB(GLine legB) {
        LegB = legB;
    }

    public void setLegC(GLine legC) {
        LegC = legC;
    }

    public double area() {
        Vector3 A = LegA.getStartPoint();
        Vector3 B = LegA.getEndPoint();
        Vector3 C = LegB.getEndPoint(); // assuming LegB connects B → C

        return Math.abs((B.getX() - A.getX()) * (C.getY() - A.getY()) - (B.getY() - A.getY()) * (C.getX() - A.getX())) / 2;
    }

    @Override
    public String toString() {
        return col.toString() + " GTri";
    }
}
