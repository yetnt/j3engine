package com.j3d.gen.properties;

import java.util.Objects;

/**
 * Represents a key for a property, defining its name, description, expected type,
 * the class that provides the property, and whether it's a constant.
 *
 * @param <T> The expected type of the property's value.
 * @param <G> The type of the class that provides this property.
 * @param name The unique name of the property.
 * @param description A brief description of the property's purpose.
 * @param expectedClass The {@link Class} object representing the expected type of the property's value.
 * @param providerClass The {@link Class} object representing the class that provides this property.
 * @param constant A boolean indicating whether this property's value is constant (true) or mutable (false).
 * @implSpec A {@link PropertyKey} is used to merge similar {@link Property} instances for batch editing.
 * A property, is similar to another if it passes {@link #equals(Object)}
 */

public record PropertyKey<T, G>(
        String name,
        String description,
        Class<T> expectedClass,
        Class<G> providerClass,
        boolean constant
) {

    /**
     * Indicates whether some other object is "equal to" this one.
     * The equality check for PropertyKey only considers {@code name}, {@code expectedClass}, and
     * {@code providerClass}.

     * @param o   the reference object with which to compare.
     * @return   {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
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
