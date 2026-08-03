package com.j3d.engine.scene;

import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.gen.properties.HasProperties;

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
public interface SceneObject extends HasProperties {
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
