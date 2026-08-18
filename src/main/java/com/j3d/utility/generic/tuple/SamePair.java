package com.j3d.utility.generic.tuple;

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
}
