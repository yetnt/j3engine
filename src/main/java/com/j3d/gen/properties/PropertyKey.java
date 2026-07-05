package com.j3d.gen.properties;

import java.util.Objects;

public record PropertyKey<T, G>(
        String name,
        String description,
        Class<T> expectedClass,
        Class<G> providerClass,
        boolean constant
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PropertyKey<?, ?> that = (PropertyKey<?, ?>) o;
        return Objects.equals(name(), that.name()) && Objects.equals(expectedClass(), that.expectedClass()) && Objects.equals(providerClass(), that.providerClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), expectedClass(), providerClass());
    }
}
