package com.j3d.utility.generic;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts four input arguments and returns no result.
 * This is a four-arity specialization of {@link java.util.function.Consumer}.
 * Unlike most other functional interfaces, {@code QuadConsumer} is expected to
 * operate via side effects.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation.
 * @param <W> the type of the fourth argument to the operation.
 * @see Consumer
 * @author Lehlogonolo Poole
 */
@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {
    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @param w the fourth input argument
     */
    void accept(T t, U u, V v, W w);
}
