package com.j3d.gen.properties;

import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.layer.Layer;

import java.util.ArrayList;

/**
 * An interface for objects that have a collection of properties.
 * This allows for generic handling of objects that expose configurable attributes.
 * @see GObject
 * @see Thing
 * @see Layer
 */
public interface HasProperties {
    /**
     * Returns an {@link ArrayList} of {@link Property} objects associated with this instance.
     * Each property can hold a value of a specific type and have a specific key type.
     * @return an ArrayList of properties.
     */
    ArrayList<Property<?, ?>> getProperties();
}