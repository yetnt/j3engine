package com.j3d.engine.draw.tris;

import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.settings.classes.ScenePropertiesSettings;
import com.j3d.ui.engine.EngineFrame;

/**
 * Enum used by the engine to decipher which triangle sorting method
 * to use. This is governed by the following setting:
 * {@link ScenePropertiesSettings#triangleSortMethod}
 * @author Lehlogonolo Poole
 * @see ScenePropertiesSettings#triangleSortMethod
 * @see TriStateArea
 * @see SortMethod
 * @see GTri
 */
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
