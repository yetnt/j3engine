package com.j3d.utility.generic.func;

/**
 * Represents an operation that accepts three input arguments of the same type and returns no result.
 * This is a specialization of {@link TriConsumer} where all three input arguments are of the same type.
 * Unlike most other functional interfaces, {@code TrinaryConsumer} is expected to operate via side-effects.
 *
 * @param <T> the type of the input arguments to the operation
 *
 * @see TriConsumer
 * @author Lehlogonolo Poole
 */
@FunctionalInterface
public interface TrinaryConsumer<T> extends TriConsumer<T, T, T> {
    @Override
    default TrinaryConsumer<T> andThen(TriConsumer<? super T, ? super T, ? super T> after) {
        return (TrinaryConsumer<T>) TriConsumer.super.andThen(after);
    }
}
