package com.j3d.engine.geometry;

import com.j3d.engine.Renderer;
import com.j3d.engine.events.ObjectType;
import com.j3d.engine.geometry.base.CartesianPoint;
import java.util.*;
import java.util.List;

import com.j3d.engine.geometry.base.BasePoint;

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

    @Override
    public void draw(Renderer renderer) {
        renderer.getGraphics().fillPolygon(
                new int[] {
                        LegA.getStartPoint().toScreen(renderer).x,
                        LegA.getEndPoint().toScreen(renderer).x,
                        LegB.getEndPoint().toScreen(renderer).x
                },
                new int[] {
                        LegA.getStartPoint().toScreen(renderer).y,
                        LegA.getEndPoint().toScreen(renderer).y,
                        LegB.getEndPoint().toScreen(renderer).y
                },
                3
        );
    }

    /**
     * Private method to draw a triangle from 3 lines.
     * @param r Renderer Instance
     * @param leg1 Leg 1
     * @param leg2 Leg 2
     * @param leg3 Leg 3
     */
    private void drawTri(Renderer r, GLine leg1, GLine leg2, GLine leg3) {
        LegA = leg1;
        LegB = leg2;
        LegC = leg3;
        draw(r);
    }

    /**
     * Constructs a new GTri from 3 points.
     * @param renderer Renderer Instance
     * @param A Point A
     * @param B Point B
     * @param C Point C
     */
    public GTri(Renderer renderer, GPoint A, GPoint B, GPoint C) {
        super(renderer);
        attach(A, ObjectType.NODE);
        attach(B, ObjectType.NODE);
        attach(C, ObjectType.NODE);
        A.attach(this, ObjectType.PARENT);
        B.attach(this, ObjectType.PARENT);
        C.attach(this, ObjectType.PARENT);
            // draw the triangle.

        drawTri(
                renderer,
                new GLine(renderer, A, B),
                new GLine(renderer, B, C),
                new GLine(renderer, C, A)
        );
        setPivot(new CartesianPoint(
                (A.getPivot().x + B.getPivot().x + C.getPivot().x) / 3,
                (A.getPivot().y + B.getPivot().y + C.getPivot().y) / 3
        ));
    }

    public GTri(Renderer r, GLine A, GLine B, GLine C) {
        super(r);
        CartesianPoint[] points = {
                A.getStartPoint(), A.getEndPoint(),
                B.getStartPoint(), B.getEndPoint(),
                C.getStartPoint(), C.getEndPoint()
        };

        // Count how many times each unique point appears
        Map<CartesianPoint, Integer> pointCount = new HashMap<>();
        for (CartesianPoint p : points) {
            pointCount.merge(p, 1, Integer::sum);
        }

        // A valid triangle should have exactly 3 unique points, each appearing twice
        if (pointCount.size() != 3 || pointCount.values().stream().anyMatch(count -> count != 2)) {
            throw new IllegalArgumentException("Lines do not form a closed triangle.");
        }

        // Optional: Check for collinearity
        List<CartesianPoint> vertices = new ArrayList<>(pointCount.keySet());
        if (BasePoint.areCollinear(vertices.get(0), vertices.get(1), vertices.get(2))) {
            throw new IllegalArgumentException("Points are collinear—no triangle formed.");
        }

        drawTri(
                r,
                A, B, C
        );
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
        CartesianPoint A = LegA.getStartPoint();
        CartesianPoint B = LegA.getEndPoint();
        CartesianPoint C = LegB.getEndPoint(); // assuming LegB connects B → C

        return 0.5 * Math.abs(
                A.x * (B.y - C.y) +
                        B.x * (C.y - A.y) +
                        C.x * (A.y - B.y)
        );
    }
}
