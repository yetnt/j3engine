package com.j3d.utility.generic.func;

import java.util.Objects;

/**
 * Functional interface representing an operation that accepts three arguments and returns no result.
 * This is similar to Consumer and BiConsumer but for three parameters.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);

    /**
     * Returns a composed TriConsumer that performs, in sequence, this operation followed by the after operation.
     * @param after the operation to perform after this operation
     * @return a composed TriConsumer
     * @throws NullPointerException if after is null
     */
    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> {
            accept(t, u, v);
            after.accept(t, u, v);
        };
    }
}
