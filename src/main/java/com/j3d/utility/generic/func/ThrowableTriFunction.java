package com.j3d.utility.generic.func;

import java.util.Objects;

/**
 * Functional interface representing an operation that accepts three arguments and returns a valur
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 * @param <W> The type to return
 * @param <X> The exception this function could throw
 */
@FunctionalInterface
public interface ThrowableTriFunction<T, U, V, W, X extends Throwable> {
    W apply (T t, U u, V v) throws X;
}
