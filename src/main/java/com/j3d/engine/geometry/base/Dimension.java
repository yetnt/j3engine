package com.j3d.engine.geometry.base;

/**
 * Dimension, like {@link BasePoint} is a 2 dimensional class, however holding width and height
 * instead of an X and Y value.
 */
public class Dimension {
    /**
     * The width of this dimension.
     */
    public final int width;
    /**
     * The height of this dimension.
     */
    public final int height;

    /**
     * Default constructor
     * @param width Given width
     * @param height Given height.
     */
    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "[" + width + " x " + height + "]";
    }
}