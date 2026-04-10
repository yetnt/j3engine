package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.geometry.geo2d.graphics.*;

import java.util.HashSet;

/**
 * An interface for objects that can be part of a composite structure, allowing them
 * to maintain references to their parent or owner objects.
 * <p>
 * This is particularly useful in a scene graph or a composite geometry system where
 * child objects (e.g., a {@link GPoint}) need to notify their parent objects
 * (e.g., a {@link GLine} or {@link GTri}) of changes. By implementing this interface,
 * an object can track multiple parents that depend on its state.
 *
 * @param <T> The type of the parent objects.
 * @author Lehlogonolo Poole
 */
public interface HasParents<T> {
    /**
     * Retrieves the set of all parent objects that this object belongs to.
     *
     * @return A {@link HashSet} containing all parent objects. Using a Set ensures
     *         that a parent can only be added once.
     */
    HashSet<T> getParents();

    /**
     * Adds a parent to this object's set of parents.
     *
     * @param parent The parent object to add.
     */
    void addParent(T parent);

    /**
     * Removes a parent from this object's set of parents.
     *
     * @param parent The parent object to remove.
     */
    void removeParent(T parent);

    /**
     * A convenience method to add multiple parent objects at once.
     *
     * @param parents A varargs array of parent objects to add.
     */
    default void addParents(T... parents) {
        for (T parent : parents) {
            addParent(parent);
        }
    }

    /**
     * Checks if this object has at least one parent.
     *
     * @return {@code true} if the set of parents is not empty, {@code false} otherwise.
     */
    default boolean hasParent() {
        return !getParents().isEmpty();
    }
}
