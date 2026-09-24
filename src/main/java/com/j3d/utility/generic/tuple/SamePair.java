package com.j3d.utility.generic.tuple;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A specialized {@link Pair} where both elements are of the same type.
 *
 * @param <T> The type of both elements in the pair.
 *
 * @author Lehlogonolo Poole
 */
public class SamePair<T> extends Pair<T, T> {
    /**
     * Constructs a new {@code SamePair} with the given first and second elements.
     *
     * @param first The first element of the pair.
     * @param second The second element of the pair.
     */
    public SamePair(T first, T second) {
        super(first, second);
    }

    public SamePair(Pair<T, T> samePair) {
        super(samePair.first, samePair.second);
    }

    public <U> SamePair<U> map(Function<T, U> function) {
        return new SamePair<U>(function.apply(first), function.apply(second));
    }

    public <U> U mapTo(BiFunction<T, T, U> function) {
        return function.apply(first, second);
    }
}
