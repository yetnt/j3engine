package com.j3d.engine.scene;

import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.*;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.gen.properties.HasProperties;
import com.j3d.jaiva.EngineObject;
import com.yetnt.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Converts this SceneObject to a {@link EngineObject} being a Jaiva-readable {@link java.util.ArrayList} of
     * data.
     * @implSpec Having this implemented also requires that the implementor or at least subclasses of
     * the implementor which are of different types, each have a public static {@code EngineObjectUtils}
     * class which assists in fetching the data stored via static methods. See {@link Layer} or {@link GObject}/{@link GCurve}
     * for examples.
     * @return An {@link EngineObject}
     */
    EngineObject toObject();

    /**
     * Returns the map of instance tags.
     * @implSpec This should always be stored as a constant field.
     * @return A {@link SceneObjectTags}
     */
    SceneObjectTags getTags();

    /**
     * Wrapper over {@link SceneObjectTags#put(String, Class, Object)} which allows putting a single tag
     * on this instance
     * @param name The name of the tag
     * @param tagProvider The class of the instance which is providing this tag.
     * @param value The tag value.
     */
    default void tag(String name, @NotNull Class<?> tagProvider, @NotNull Object value) {
        getTags().put(new Pair<>(name, tagProvider), value);
    }

}
