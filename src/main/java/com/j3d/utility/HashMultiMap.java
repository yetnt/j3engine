package com.j3d.utility;

import java.util.ArrayList;
import java.util.HashMap;

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
