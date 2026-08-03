package com.j3d.engine.math;

/**
 * Dim, like {@link BasePoint} is a 2 dimensional class, however holding width and height
 * instead of an X and Y value.
 * <p>
 *     This could just as well be {@link java.awt.Dimension} but come on now wheres
 *     the fun in using what java gives you already?
 * </p>
 * @author Lehlogonolo Poole
 * @see java.awt.Dimension
 */
public class Dim {
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
    public Dim(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor using {@link java.awt.Dimension}
     * @param preferredSize Given {@link java.awt.Dimension}
     */
    public Dim(java.awt.Dimension preferredSize) {
        this.width = preferredSize.width;
        this.height = preferredSize.height;
    }

    @Override
    public String toString() {
        return "[" + width + " x " + height + "]";
    }
}