package com.j3d.utility.generic.collection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A HashMap who maps 1 key to multiple values.
 * <p>
 *     This class provides methods like {@link #putValue(Object, Object)} to add a single value to the collection of values
 *     associated with a key, and {@link #getValues(Object)} to retrieve all values for a given key.
 * </p>
 *
 * @param <K> the type of keys maintained by this multimap
 * @param <T> the type stored in the list
 * @author Lehlogonolo Poole
 */
public class HashMultiMap<K, T> extends HashMap<K, ArrayList<T>> {

    public  HashMultiMap() {
        super();
    }
    /**
     * Adds a value to the list of values associated with the specified key.
     * If the key does not already exist in the map, a new list is created for it.
     *
     * @param key   The key to which the value should be added.
     * @param value The value to add to the list associated with the key.
     */
    public void putValue(K key, T value) {
        this.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * Retrieves the list of values associated with the specified key.
     *
     * @param key The key whose associated values are to be returned.
     * @return The list of values associated with the specified key, or {@code null} if the key does not exist in the map.
     */
    public ArrayList<T> getValues(K key) {
        return this.get(key);
    }

    public boolean holdsSingletonLists() {
        return values().stream()
                .filter(value -> value.size() == 1)
                .toList().size() == values().size();
    }
}
