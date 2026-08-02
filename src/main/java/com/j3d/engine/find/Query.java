package com.j3d.engine.find;

import com.j3d.engine.SceneObject;

import java.util.function.BiFunction;

/**
 * Represents a search query that can be applied to a {@link SceneObject}.
 * This functional interface extends {@link BiFunction} to define a predicate
 * that takes a {@link SceneObject} of type {@code T} and a value of type {@code V},
 * returning {@code true} if the object matches the query criteria, and {@code false} otherwise.
 * <p>
 *     If not using the static predicates defined by {@link Finder} this cna be used instead to create
 *     a custom search query. Provided it returns true
 * </p>
 *
 * @param <T> The type of {@link SceneObject} this query operates on.
 * @param <V> The type of the value used for comparison in the query.
 * @see Finder
 * @see SceneObject
 */
public interface Query<T extends SceneObject, V> extends BiFunction<T, V, Boolean> {
}
