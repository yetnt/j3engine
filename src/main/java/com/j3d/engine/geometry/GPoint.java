package com.j3d.engine.geometry;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.base.CartesianPoint;
import com.j3d.engine.geometry.base.ScreenPoint;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class GPoint extends GObject {

    private void drawPoint(Renderer renderer, CartesianPoint cartesianPoint) {
        ScreenPoint p = cartesianPoint.toScreen(renderer);
        renderer.getGraphics().fillOval(p.x, p.y, 5, 5);

        setPivot(cartesianPoint);
    }

    public GPoint(Renderer renderer, CartesianPoint cartesianPoint) {
        drawPoint(renderer, cartesianPoint);
    }

    public void update(Renderer renderer, CartesianPoint pivot) {
        super.setPivot(pivot);
        drawPoint(renderer, pivot);
    }

    @Override
    public ArrayList<Object> toArray() {
        ArrayList<Object> arr =  super.toArray();
        arr.addFirst("GPOINT");
        return arr;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GPoint other)) return false;
        return Objects.equals(this.getPivot(), other.getPivot());
    }
}
