package com.j3d.engine.geometry.base;

/**
 * Base Point is a generic 2D point base class
 * @param <T> The type of the given point, which can either be an {@link Integer} or {@link Double} or any other thing that extends {@link Number}
 */
public abstract class BasePoint<T extends Number> {
    /**
     * Tge X-Value
     */
    public final T x;
    /**
     * The Y-Value
     */
    public final T y;

    /**
     * Default Constructor Initializing the point.
     * @param xcord The x-coordinate.
     * @param ycord The y-coordinate
     */
    public BasePoint(T xcord, T ycord) {
        this.x = xcord;
        this.y = ycord;
    }

    /**
     * A static class that checks if 3 {@link CartesianPoint}s lie on the same line (Are collinear)
     * @param p1 Point 1
     * @param p2 Point 2
     * @param p3 Poit 3
     * @return A boolean value indicating whether the points are collinear or not.
     */
    public static boolean areCollinear(CartesianPoint p1, CartesianPoint p2, CartesianPoint p3) {
        double area = p1.x * (p2.y - p3.y) +
                p2.x * (p3.y - p1.y) +
                p3.x * (p1.y - p2.y);
        return Math.abs(area) < 1e-9; // Tolerance for floating-point precision
    }
}
