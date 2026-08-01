package com.j3d.engine.geometry.geo2d.copy;

import com.j3d.engine.geometry.geo2d.graphics.GObject;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a copy of a {@link GObject}, linking it back to its original.
 * <p>
 * The equality and hash code stuff are based solely on the {@code original} UUID,
 * meaning two {@code Copy} instances are considered equal if they refer to the same original object,
 * regardless of the actual {@code GObject} instance they hold as a copy.
 * </p>
 *
 * @param original The {@link UUID} of the original {@link GObject} from which this copy was made.
 * @param copy     The {@link GObject} instance that is the actual copy.
 * @author Lehlogonolo Poole
 */
public record Copy(
        UUID original,
        GObject copy
) {

    /**
     * Checks if this {@code Copy} instance refers to a specific original {@link GObject}
     * by comparing its {@code original} UUID with the given ID.
     *
     * @param id The {@link UUID} of the original {@link GObject} to check against.
     * @return {@code true} if this copy's original ID matches the given ID, {@code false} otherwise.
     */
    public boolean is(UUID id) {
        return original().equals(id);
    }

    /**
     * Compares this {@code Copy} object with the specified object for equality.
     *
     * @param o The object to compare with.
     * @return {@code true} if the specified object is equal to this {@code Copy} object, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        // Standard record equals implementation would check all components.
        // This custom implementation focuses only on the 'original' UUID.
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Copy other = (Copy) o;
        return Objects.equals(this.original, other.original);
    }

    /**
     * Returns a hash code value for this {@code Copy} object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        // Standard record hashCode implementation would use all components.
        // This custom implementation focuses only on the 'original' UUID.
        return Objects.hashCode(this.original);
    }
}
