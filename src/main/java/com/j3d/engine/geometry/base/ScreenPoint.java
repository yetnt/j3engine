package com.j3d.engine.geometry.base;

import com.j3d.engine.Renderer;

public class ScreenPoint extends BasePoint<Integer> {

    public ScreenPoint(int X, int Y) {
        super(X, Y);
    }

    public CartesianPoint toPoint(Renderer renderer) {
        double adjustedX = (x - renderer.screenSize.width / 2) / renderer.SCALE;
        double adjustedY = (renderer.screenSize.height / 2 - y) / renderer.SCALE;

        double cartesianX = adjustedX + renderer.cameraOffset.x;
        double cartesianY = adjustedY + renderer.cameraOffset.y;

        return new CartesianPoint(cartesianX, cartesianY);
    }

}
