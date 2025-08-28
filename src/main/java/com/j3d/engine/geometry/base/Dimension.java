package com.j3d.engine.geometry.base;

public class Dimension {
    public final int width;
    public final int height;

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "[" + width + " x " + height + "]";
    }
}