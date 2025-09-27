package com.j3d.engine.geometry.base;

/**
 * Point3D is a simple class that holds 3 integer values, x, y, and z.
 * <p>
 *     In it's current state, it is not used by the engine, but will be when all 2d geometry is implemented and battle tested.
 *</p>
 */
public class Point3D {
    public final int x;
    public final int y;
    public final int z;

    public Point3D(int X, int Y, int Z) {
        x = X;
        y = Y;
        z = Z;
    }
}
