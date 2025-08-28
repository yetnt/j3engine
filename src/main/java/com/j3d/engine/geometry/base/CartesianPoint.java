package com.j3d.engine.geometry.base;

import com.j3d.engine.Renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CartesianPoint extends BasePoint<Double> {

    public CartesianPoint(double X, double Y) {
        super(X, Y);
    }

    public ScreenPoint toScreen(Renderer renderer) {
        double adjustedX = (x - renderer.cameraOffset.x) * renderer.SCALE;
        double adjustedY = (y - renderer.cameraOffset.y) * renderer.SCALE;

        int screenX = (int) (adjustedX + renderer.screenSize.width / 2);
        int screenY = (int) (renderer.screenSize.height / 2 - adjustedY);

        return new ScreenPoint(screenX, screenY);
    }

    public static CartesianPoint fromArray(ArrayList<? extends Number> arr) {
        if (arr.size() != 2) throw new RuntimeException("ArrayList was given more than 2 input values.");
        return new CartesianPoint(arr.getFirst().intValue(), arr.getLast().intValue());
    }

    public ArrayList<Double> toArray() {
        return new ArrayList<>(Arrays.asList(x, y));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CartesianPoint other)) return false;
        return Objects.equals(this.x, other.x) && Objects.equals(this.y, other.y);
    }
}
