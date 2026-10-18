package com.j3d.storage.files.protocol.proj;

import com.j3d.engine.scene.nodes.geometry.GObject;
import com.yetnt.utils.collection.HashMultiMap;
import com.yetnt.utils.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * A utility class that extends {@link Pair} to store two maps related to some {@link GObject}.
 * It holds a {@link HashMultiMap} to map parent thing IDs to a list of geometric objects,
 * and a {@link HashMap} to map individual geometric object IDs to the objects themselves.
 * @implNote Rather than calling {@link #first} and {@link #second}, this class
 * allows you to call the convenience methods instead, being {@link #getThingParentMap()}
 * and {@link #getIdMap()} respectively
 *
 * @param <R> The key type used to identify values in both maps. This is abstracted since
 *           {@link PF3} uses strings, but {@link PF4} uses the actual {@link UUID} value
 * @param <T> The type of {@link GObject} stored in the maps.
 */
class SuperMap<R, T extends GObject> extends Pair<
        HashMultiMap<R, T>,
        HashMap<R, T>
        > {
    /**
     * Constructs a new {@code SuperMap} with the given parent map and ID map.
     *
     * @param thingParentMap A {@link HashMultiMap} mapping parent thing IDs to geometric objects.
     * @param idMap A {@link HashMap} mapping geometric object IDs to geometric objects.
     */
    public SuperMap(HashMultiMap<R, T> thingParentMap, HashMap<R, T> idMap) {
        super(thingParentMap, idMap);
    }
    /**
     * Returns the {@link HashMultiMap} that maps parent thing IDs to geometric objects.
     *
     * @return The parent thing map.
     */
    public HashMultiMap<R, T> getThingParentMap() {
        return first;
    }
    /**
     * Returns the {@link HashMap} that maps geometric object IDs to geometric objects.
     *
     * @return The ID map.
     */
    public HashMap<R, T> getIdMap() {
        return second;
    }
    /**
     * Retrieves a list of geometric objects associated with a specific parent thing ID.
     * If no objects are found for the given thing ID, an empty {@link ArrayList} is returned.
     *
     * @param thingId The ID of the parent thing.
     * @return An {@link ArrayList} of geometric objects belonging to the specified thing.
     */
    public ArrayList<T> fromThing(R thingId) {
        HashMultiMap<R, T> map = getThingParentMap();
        ArrayList<T> values = map.getValues(thingId);
        return values == null ? new ArrayList<>() : values;
    }
}