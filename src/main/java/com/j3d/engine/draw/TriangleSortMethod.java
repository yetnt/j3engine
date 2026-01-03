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
     * Triangles are sorted based on their depth values.
     */
    CAMDEPTHSORT,
    /**
     * Triangles are sorted like CamDepth then CamDist then lastly based on their unique identifiers (UUIDs).
     */
    DDUUIDSORT,
    /**
     * Triangles are sorted into buckets based on their depth values.
     */
    BUCKETSORT,
    /**
     * Triangles are sorted based on their visibility.
     */
    VISIBLESORT;
}
