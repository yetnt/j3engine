package com.j3d.engine.scene.find;

import com.j3d.engine.scene.SceneManager;
import com.j3d.engine.scene.SceneObject;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.engine.scene.nodes.layer.LayerList;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * The {@code Finder} class provides utility methods for searching and locating
 * {@link SceneObject}s within the engine's hierarchical structure of {@link Layer}s,
 * {@link Thing}s, and {@link GObject}s.
 * @see FindResult
 * @see Query
 * @see SceneManager
 * @see Layer
 * @see Thing
 * @see GObject
 * @author Lehlogonolo Poole
 */
public class Finder {
    /**
     * Supplies the {@link LayerList} containing all active layers in the engine.
     * This allows the Finder to access the current state of layers dynamically.
     */
    private Supplier<LayerList> layerListSupplier;

    /**
     * Constructs a new {@code Finder} instance.
     *
     * @param layerListSupplier A {@link Supplier} that provides the {@link LayerList}
     *                          from which objects will be searched. This allows the
     *                          Finder to always work with the most current layer data.
     */
    public Finder(Supplier<LayerList> layerListSupplier) {
        this.layerListSupplier = layerListSupplier;
    }
    /**
     * Retrieves the current {@link LayerList} from the configured supplier.
     *
     * @return The {@link LayerList} containing all active layers.
     */
    private LayerList getLayers() {
        return layerListSupplier.get();
    }

    /**
     * Creates a {@link Query} that matches {@link SceneObject}s based on their name.
     * This query compares the name of a {@link SceneObject} with a given string value.
     *
     * @param <T> The type of {@link SceneObject} to query.
     * @return A {@link Query} instance that takes a {@link SceneObject} and a {@link String} (name)
     *         and returns {@code true} if their names are equal.
     */
    public static <T extends SceneObject> Query<T, String> nameQuery() {
        return (t, v) -> t.getName().equals(v);
    }

    /**
     * Creates a {@link Query} that matches {@link SceneObject}s based on their unique identifier (UUID).
     * This query compares the ID of a {@link SceneObject} with a given {@link UUID} value.
     *
     * @param <T> The type of {@link SceneObject} to query.
     * @return A {@link Query} instance that takes a {@link SceneObject} and a {@link UUID} (ID)
     *         and returns {@code true} if their IDs are equal.
     */
    public static <T extends SceneObject> Query<T, UUID> idQuery() {
        return (t, v) -> t.getId().equals(v);
    }

    /**
     * Creates a {@link Query} that matches {@link SceneObject}s based on instance equality.
     * This query uses the {@code equals} method to compare two {@link SceneObject} instances.
     *
     * @param <T> The type of {@link SceneObject} to query.
     * @return A {@link Query} instance that takes two {@link SceneObject}s and returns {@code true}
     *         if they are considered equal by their {@code equals} method.
     */
    public static <T extends SceneObject> Query<T, T> instanceQuery() {
        return Object::equals;
    }

    /**
     * Searches through all layers, things, and GObjects to find the first {@link SceneObject}
     * of a specified type that satisfies a given query.
     * This method traverses the entire scene graph and returns the first match found,
     * or an empty {@link FindResult} if no object matches.
     *
     * @implSpec This should probably be used if looking for a singular result over using {@link #find(Class, Query, Object)}
     * as find will attempt to traverse the entire scene graph where as find first will short-circuit and return the first match found.
     *
     * @param <T> The type of {@link SceneObject} to search for.
     * @param <V> The type of the value used by the {@link Query} for comparison.
     * @param clazz The {@link Class} object representing the type {@code T}. This is used
     *              to filter objects by their runtime type.
     * @param query The {@link Query} to apply to each {@link SceneObject}. It defines
     *              the condition for an object to be considered a match.
     * @param value The value to be used by the {@link Query} for comparison.
     * @return A {@link FindResult} containing the first matching {@link SceneObject}
     *         and its hierarchical location (layer, thing, GObject indices),
     *         or an empty {@link FindResult} if no object is found.
     * @see FindResult
     * @see Query
     * @see SceneObject
     * @see Layer
     * @see Thing
     * @see GObject
     */
    public <T extends SceneObject, V> FindResult findFirst(Class<T> clazz, Query<T, V> query, V value) {
        // search through each layer
        for (Layer l : getLayers()) {
            if (l.getName().equals(Layer.BACKGROUND_ID)) continue;
            // if the layer isnt what we're looking for:
            if (!clazz.isInstance(l)) {
                // search inside thing
                for (Thing t : l) {
                    // if the thing isnt what we're looking for:
                    if (!clazz.isInstance(t)) {
                        // search inside GObject
                        for (GObject g : t.getObjects()) {
                            // if the GObject isnt what we're looking for:
                            if (!clazz.isInstance(g)) {
                                // then youre looking for something that just wouldn't exist in this list.
                                return new FindResult();
                            } else {
                                // if we do find a GObject, apply the query
                                if (query.apply((T) g, value)) {
                                    return new FindResult(
                                            l, getLayers().indexOf(l),
                                            t, getLayers().indexOf(t),
                                            g, t.getObjects().indexOf(g)
                                    );
                                }
                            }
                        }
                        // end of GObject for
                    } else {
                        // if we do find a thing, apply the query
                        if (query.apply((T) t, value)) {
                            return new FindResult(l, getLayers().indexOf(l), t, getLayers().indexOf(t));
                        }
                    }
                }
                // end of thing for
            } else {
                // if we do find a layer, apply the query
                if (query.apply((T) l, value)) {
                    return new FindResult(l, getLayers().indexOf(l));
                }
            }
        }
        // end of layer for
        return new FindResult();
    }

    /**
     * Searches through all layers, things, and GObjects to find all {@link SceneObject}s
     * of a specified type that satisfy a given query.
     * This method traverses the entire scene graph and collects all matching objects.
     *
     * @param <T> The type of {@link SceneObject} to search for.
     * @param <V> The type of the value used by the {@link Query} for comparison.
     * @param clazz The {@link Class} object representing the type {@code T}. This is used
     *              to filter objects by their runtime type.
     * @param query The {@link Query} to apply to each {@link SceneObject}. It defines
     *              the condition for an object to be considered a match.
     * @param value The value to be used by the {@link Query} for comparison.
     * @return An {@link ArrayList} of {@link FindResult}s, each containing a reference
     *         to a matching {@link SceneObject} and its hierarchical location (layer, thing, GObject indices).
     *         Returns an empty list if no objects are found.
     * @see FindResult
     * @see Query
     * @see SceneObject
     */
    public <T extends SceneObject, V> ArrayList<FindResult> find(Class<T> clazz, Query<T, V> query, V value) {
        ArrayList<FindResult> results = new ArrayList<>();
        // search through each layer
        for (Layer l : getLayers()) {
            if (l.getName().equals(Layer.BACKGROUND_ID)) continue;
            // if the layer isnt what we're looking for:
            if (!clazz.isInstance(l)) {
                // search inside thing
                for (Thing t : l) {
                    // if the thing isnt what we're looking for:
                    if (!clazz.isInstance(t)) {
                        // search inside GObject
                        for (GObject g : t.getObjects()) {
                            // if the GObject isnt what we're looking for:
                            if (!clazz.isInstance(g)) {
                                // then youre looking for something that just wouldn't exist in this list.
                                continue;
                            } else {
                                // if we do find a GObject, apply the query
                                if (query.apply((T) g, value)) {
                                    results.add(new FindResult(
                                            l, getLayers().indexOf(l),
                                            t, getLayers().indexOf(t),
                                            g, t.getObjects().indexOf(g)
                                    ));
                                }
                            }
                        }
                        // end of GObject for
                    } else {
                        // if we do find a thing, apply the query
                        if (query.apply((T) t, value)) {
                            results.add(new FindResult(l, getLayers().indexOf(l), t, getLayers().indexOf(t)));
                        }
                    }
                }
                // end of thing for
            } else {
                // if we do find a layer, apply the query
                if (query.apply((T) l, value)) {
                    results.add(new FindResult(l, getLayers().indexOf(l)));
                }
            }
        }
        // end of layer for
        return  results;
    }

}
