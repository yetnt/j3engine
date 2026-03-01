package com.j3d.utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A lightweight multimap backed by a HashMap from keys to ArrayList of values.
 * This class provides convenience methods to add values to a key's list and to
 * retrieve the list of values for a key. It intentionally extends HashMap<K, ArrayList<T>>
 * so all standard Map operations are available.
 * <p>
 * Note: This implementation is not thread-safe. Concurrent access should be
 * synchronized externally or a concurrent collection should be used.
 *
 * @param <K> the type of keys maintained by this multimap
 * @param <T> the type stored in the list
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
}
