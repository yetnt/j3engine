package com.j3d.engine.geometry;

import com.j3d.J3DSettings;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.CartesianPoint;

import java.awt.*;

/**
 * ScreenPoint, not to be confused with {@link CartesianPoint}, is a point on the actual screen (window) open.
 * Where (0, 0) is the top left corner of the window.
 * <p>
 * ALl 2d points should be calculated in {@link CartesianPoint}, but when you want to show it on the screen, converted to a {@link ScreenPoint}
 */
public class ScreenPoint extends BasePoint<Integer> {

    /**
     * Default Constructor
     * @param X The X-Value
     * @param Y The Y-Value
     */
    public ScreenPoint(int X, int Y) {
        super(X, Y);
    }

    /**
     * Converts this ScreenPoint back into it's CartesianPoint. (Accuracy is not guaranteed.)
     * @param renderer The Renderer Instance.
     * @return A CartesianPoint
     */
    public CartesianPoint toPoint(Renderer renderer) {
        double adjustedX = (x - renderer.screenSize.width / 2) / J3DSettings.SCALE;
        double adjustedY = (renderer.screenSize.height / 2 - y) / J3DSettings.SCALE;

        double cartesianX = adjustedX;
        double cartesianY = adjustedY;

        return new CartesianPoint(cartesianX, cartesianY);
    }

    /**
     * Returns the ScreenPoint as {@link Point}
     * @return A Point
     */
    public Point toSwingPoint() {
        return new Point(x, y);
    }

}
