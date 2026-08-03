package com.j3d.engine.geometry.geo2d.pure;

import com.j3d.engine.draw.RenderState;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.util.UUID;

public interface Pure {
    default <T extends Pure, R extends GObject> RenderState<T, R> toRenderState(R parent) {
        return new RenderState<>((T)this, parent);
    }

    Vector3 getPivot();

    UUID getId();
}
