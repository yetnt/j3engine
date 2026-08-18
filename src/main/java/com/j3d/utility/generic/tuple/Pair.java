package com.j3d.utility.generic.tuple;

import java.util.Objects;

/**
 * A generic Pair class that holds two related objects.
 *
 * @param <T> The type of the first object.
 * @param <U> The type of the second object.
 *
 * @author Lehlogonolo Poole
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public String toString() {
        return "Pair{" + "first=" + first + ", second=" + second + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
