package com.j3d.utility.generic.func;

import java.util.Objects;

/**
 * Functional interface representing an operation that accepts three arguments and returns no result.
 * This is similar to Consumer and BiConsumer but for three parameters.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 * @param <W> The type to return
 */
@FunctionalInterface
public interface TriFunction<T, U, V, W> {
    W apply (T t, U u, V v);
}
