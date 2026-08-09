package com.j3d.engine.math;

import com.j3d.engine.math.matrix.Vector3;

import java.awt.*;

/**
 * ScreenPoint, not to be confused with {@link CartesianPoint}, is a point on the actual screen (window) open.
 * Where (0, 0) is the top left corner of the window.
 * <p>
 *     All 2d points should be calculated in {@link CartesianPoint}, but when you want to show it on the screen, converted to a {@link ScreenPoint}
 * </p>
 * @author Lehlogonolo Poole
 * @see #toScreen()
 * @see Vector3
 * @see BasePoint
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
     * @return A CartesianPoint
     */
    public CartesianPoint toPoint() {
        return
                toPoint(ConversionProperties.global());
    }

    public CartesianPoint toPoint(ConversionProperties conversionProperties) {
        double adjustedX = ((x)- conversionProperties.size().width / 2.0) / conversionProperties.scale();
        double adjustedY = (conversionProperties.size().height / 2.0 - y) / conversionProperties.scale();
        return new CartesianPoint(adjustedX, adjustedY);
    }

    /**
     * Returns the ScreenPoint as {@link Point}
     * @return A Point
     */
    public Point toSwingPoint() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "SP[" +
                "x=" + x +
                ", y=" + y +
                ']';
    }
}
