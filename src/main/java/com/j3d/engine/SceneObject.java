package com.j3d.engine;

import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.layer.Layer;

import java.util.UUID;

/**
 * A generic object within the scene. Either this is itself some object or an object that encapsulate others.
 * @see Layer
 * @see Thing
 * @see GTri
 * @see GLine
 * @see GPoint
 * @author Lehlogonolo Poole
 */
public interface SceneObject {
    /**
     * Retrieves the name of this scene object.
     * @return The name of the scene object as a String.
     */
    String getName();

    /**
     * Retrieves the unique identifier (UUID) of this scene object.
     * @return The UUID of the scene object.
     */
    UUID getId();
}
