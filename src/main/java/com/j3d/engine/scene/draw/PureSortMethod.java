package com.j3d.engine.scene.draw;

import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.gen.settings.classes.SceneProperties;

/**
 * Enum used by the engine to decipher which triangle sorting method
 * to use. This is governed by the following setting:
 * {@link SceneProperties#triangleSortMethod}
 * @author Lehlogonolo Poole
 * @see SceneProperties#triangleSortMethod
 * @see SceneRenderer
 * @see SortMethod
 * @see GTri
 */
public enum PureSortMethod {

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
