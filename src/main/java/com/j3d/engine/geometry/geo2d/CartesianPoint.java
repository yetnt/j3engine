package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.base.BasePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * CartesianPoint, not to be confused with {@link ScreenPoint}, represents a point in 2D space.
 * (On the Cartesian Plane, where (0, 0) is the centre of the window.)
 * <p>
 * ALl 2d points should be calculated in {@link CartesianPoint}, but when you want to show it on the screen, converted to a {@link ScreenPoint}
 */
public class CartesianPoint extends BasePoint<Double> {

    private static final double EPSILON = 0.01;

    /**
     * Default Constructor
     */
    public CartesianPoint() {
        super(null, null);
    }

    /**
     * If this Cartesian Point is empty
     * @return boolean
     */
    public boolean isNotEmpty() {
        return x != null || y != null;
    }

    /**
     * Constructor with X and Y
     * @param X X-coordiante
     * @param Y Y-coordinate
     */
    public CartesianPoint(double X, double Y) {
        super(X, Y);
    }

    /**
     * Converts the Cartesian Point to a {@link ScreenPoint} such that it can be viewed on the user's window.
     * @param renderer The renderer instance.
     * @return A ScreenPoint
     */
    public ScreenPoint toScreen(Renderer renderer) {
        double adjustedX = (x - renderer.cameraOffset.x) * renderer.SCALE;
        double adjustedY = (y - renderer.cameraOffset.y) * renderer.SCALE;

        int screenX = (int) (adjustedX + renderer.screenSize.width / 2);
        int screenY = (int) (renderer.screenSize.height / 2 - adjustedY);

        return new ScreenPoint(screenX, screenY);
    }

    /**
     * Converts a 2 dimensional array into a cartesian point.
     * <p>
     * Generally used by Jaiva Implementation where points can be represented by [X, Y]
     * @param arr The array.
     * @return The new cartesian point
     */
    public static CartesianPoint fromList(ArrayList<? extends Number> arr) {
        if (arr == null || arr.size() != 2) {
            throw new IllegalArgumentException("Input list must contain exactly 2 numbers.");
        }
        // Use doubleValue() to preserve floating-point precision
        return new CartesianPoint(arr.get(0).doubleValue(), arr.get(1).doubleValue());
    }

    /**
     * Reverse of {@link CartesianPoint#fromList(ArrayList)}
     * @return A 2 Dimensional ArrayList of doubles.
     */
    public ArrayList<Double> toArray() {
        return new ArrayList<>(Arrays.asList(x, y));
    }

    @Override
    public String toString() {
        return ("{" + x + "; " + y + "}");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CartesianPoint other)) return false;
        return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON;
    }

    @Override
    public int hashCode() {
        // Use the same epsilon rounding for consistent hashing
        return Objects.hash(Math.round(x / EPSILON), Math.round(y / EPSILON));
    }

    public double distanceTo(CartesianPoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }


    public double distanceSquaredTo(CartesianPoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return dx * dx + dy * dy;
    }
}
