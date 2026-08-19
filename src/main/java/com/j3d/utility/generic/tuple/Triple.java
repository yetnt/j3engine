package com.j3d.utility.generic.tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
/**
 * An immutable record representing a tuple of three elements of the same type.
 * This record provides a convenient way to group three related objects together.
 *
 * @param <T> The type of the elements in the triple.
 */
public record Triple<T>(T v1, T v2, T v3) {
    /**
     * Converts this Triple into an {@link ArrayList} containing its three elements.
     * The order of elements in the list will be v1, v2, v3.
     *
     * @return An {@link ArrayList} containing the elements of this Triple.
     */
    public ArrayList<T> toArrayList() {
        return new ArrayList<>(List.of(v1, v2, v3));
    }

    /**
     * Applies a given mapping function to each element of this Triple,
     * returning a new Triple with the transformed elements.
     *
     * @param <T2> The type of the elements in the new Triple after mapping.
     * @param mapper The function to apply to each element.
     * @return A new {@link Triple} containing the results of applying the mapper function to each element.
     */
    public <T2> Triple<T2> map(Function<T, T2> mapper) {
        return new Triple<>(
                mapper.apply(v1),
                mapper.apply(v2),
                mapper.apply(v3)
        );
    }

    public static <T, V> void forEachPair(Triple<T> t1, Triple<V> t2, BiConsumer<T, V> forEachConsumer) {
        forEachConsumer.accept(t1.v1, t2.v1);
        forEachConsumer.accept(t1.v2, t2.v2);
        forEachConsumer.accept(t1.v3, t2.v3);
    }
}
