package com.j3d.utility;

/**
 * A generic Pair class that holds two related objects.
 *
 * @param <T> The type of the first object.
 * @param <U> The type of the second object.
 */
public class Pair<T, U> {
    /** The first object in the pair. */
    public final T first;
    /** The second object in the pair. */
    public final U second;

    /**
     * Constructs a Pair with the specified objects.
     *
     * @param first  The first object.
     * @param second The second object.
     */
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
}
