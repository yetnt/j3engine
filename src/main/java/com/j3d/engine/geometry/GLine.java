package com.j3d.engine.geometry;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.base.CartesianPoint;
import com.j3d.engine.geometry.base.ScreenPoint;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.List;

public class GLine extends GObject {
    private CartesianPoint startPoint;
    private CartesianPoint endPoint;

    public GLine(Renderer renderer, GPoint A, GPoint B) {

        ScreenPoint screenA = A.getPivot().toScreen(renderer);
        ScreenPoint screenB = B.getPivot().toScreen(renderer);
        renderer.getGraphics().drawLine(screenA.x, screenA.y, screenB.x, screenB.y);

        // right now graphics is unused, but all constructors should hold a graphics just incase.
        startPoint = A.getPivot();
        endPoint = B.getPivot();
        // set the pivot to the midpoint of the line.
        setPivot(new CartesianPoint(
                (A.getPivot().x + B.getPivot().x) / 2,
                (A.getPivot().y + B.getPivot().y) / 2
        ));
    }

    public void setEndPoint(CartesianPoint end) {
        this.endPoint = end;
    }

    public CartesianPoint getEndPoint() {
        return endPoint;
    }

    public void setStartPoint(CartesianPoint start) {
        this.startPoint = start;
    }

    public CartesianPoint getStartPoint() {
        return startPoint;
    }

    public double length() {
        return Math.sqrt(
                Math.pow(startPoint.x- endPoint.x, 2) + Math.pow(startPoint.y- endPoint.y, 2)
        );
    }
}
