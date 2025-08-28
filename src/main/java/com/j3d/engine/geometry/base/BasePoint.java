package com.j3d.engine.geometry.base;

public class BasePoint<T extends Number> {
    public final T x;
    public final T y;

    public BasePoint(T xcord, T ycord) {
        this.x = xcord;
        this.y = ycord;
    }

    public static boolean areCollinear(CartesianPoint p1, CartesianPoint p2, CartesianPoint p3) {
        double area = p1.x * (p2.y - p3.y) +
                p2.x * (p3.y - p1.y) +
                p3.x * (p1.y - p2.y);
        return Math.abs(area) < 1e-9; // Tolerance for floating-point precision
    }
}
