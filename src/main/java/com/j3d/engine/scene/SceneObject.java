package com.j3d.engine.scene;

import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.*;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.gen.properties.HasProperties;
import com.j3d.jaiva.EngineObject;

import java.util.UUID;

/**
 * A generic object within the scene. Either this is itself some object or an object that encapsulate others.
 * @see Layer
 * @see Thing
 * @see GObject
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
     * @apiNote This depends on the scene object. Like for {@link Layer} it's id is effectively useless.
     */
    UUID getId();

    EngineObject toObject();
}
