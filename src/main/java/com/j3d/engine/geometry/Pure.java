package com.j3d.engine.geometry;

import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.math.matrix.Vector3;

import java.util.UUID;

public interface Pure {
    default <T extends Pure, R extends GObject> RenderState<T, R> toRenderState(R parent) {
        return new RenderState<>((T)this, parent);
    }

    Vector3 getPivot();

    UUID getId();
}
