package com.j3d.engine.geometry.geo2d;

import java.util.ArrayList;
import java.util.HashSet;


public interface HasParents<T> {
    HashSet<T> getParents();
    void addParent(T parent);
    void removeParent(T parent);
    default void addParents(T... parents) {
        for (T parent : parents) {
            addParent(parent);
        }
    }
    default boolean hasParent() {
        return !getParents().isEmpty();
    };
}
