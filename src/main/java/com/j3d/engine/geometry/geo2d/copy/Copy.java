package com.j3d.engine.geometry.geo2d.copy;

import com.j3d.engine.geometry.geo2d.graphics.GObject;

import java.util.Objects;
import java.util.UUID;

public record Copy(
        UUID original,
        GObject copy
) {

    public boolean is(UUID id) {
        return original().equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Copy c = (Copy) o;
        return Objects.equals(original(), c.original());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(original());
    }
}
