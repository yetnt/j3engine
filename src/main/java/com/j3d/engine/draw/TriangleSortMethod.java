package com.j3d.engine.draw;

public enum TriangleSortMethod {

    /**
     * No sorting is applied to the triangles.
     */
    NONE,
    /**
     * Triangles are sorted based on their distance from the camera.
     */
    CAMDISTSORT,
    /**
     * Triangles are sorted into buckets based on their depth values.
     */
    BUCKETSORT;
}
