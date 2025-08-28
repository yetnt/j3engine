package com.j3d.engine.geometry;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.base.CartesianPoint;
import com.j3d.engine.geometry.base.ScreenPoint;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.j3d.engine.geometry.base.BasePoint;

public class GTri extends GObject{
    private GLine LegA;
    private GLine LegB;
    private GLine LegC;

    public GTri(Renderer renderer, GPoint A, GPoint B, GPoint C) {

            // draw the triangle.
        ScreenPoint screenA = A.getPivot().toScreen(renderer);
        ScreenPoint screenB = B.getPivot().toScreen(renderer);
        ScreenPoint screenC = C.getPivot().toScreen(renderer);
        renderer.getGraphics().drawPolygon(
                new int[] {screenA.x, screenB.x, screenC.x},
                new int[] {screenA.y, screenB.y, screenC.y},
                2);
        LegA = new GLine(renderer, A, B);
        LegB = new GLine(renderer, B, C);
        LegC = new GLine(renderer, C, A);
        setPivot(new CartesianPoint(
                (A.getPivot().x + B.getPivot().x + C.getPivot().x) / 3,
                (A.getPivot().y + B.getPivot().y + C.getPivot().y) / 3
        ));
    }

    public GTri(Renderer r, GLine A, GLine B, GLine C) {
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

        this.LegA = A;
        this.LegB = B;
        this.LegC = C;
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
