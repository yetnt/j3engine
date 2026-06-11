package com.j3d.utility.generic;

import java.util.Objects;

/**
 * A generic class representing a triple of three elements of the same type.
 * @param <T> The type of the elements in the triple.
 * @author Lehlogonolo Poole
 */
public class Triple<T> {
    final T first;
    final T second;
    final T third;

    public Triple(T a, T b, T c) {
        first = a;
        second = b;
        third = c;
    }

    /**
     * Returns the first element of the triple.
     * @return The first element.
     */
    public T getFirst() {
        return first;
    }

    /**
     * Returns the second element of the triple.
     * @return The second element.
     */
    public T getSecond() {
        return second;
    }

    /**
     * Returns the third element of the triple.
     * @return The third element.
     */
    public T getThird() {
        return third;
    }

    @Override
    public String toString() {
        return "Triple{" +
               "first=" + first +
               ", second=" + second +
               ", third=" + third +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?> triple = (Triple<?>) o;
        return Objects.equals(getFirst(), triple.getFirst()) && Objects.equals(getSecond(), triple.getSecond()) && Objects.equals(getThird(), triple.getThird());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst(), getSecond(), getThird());
    }
}
