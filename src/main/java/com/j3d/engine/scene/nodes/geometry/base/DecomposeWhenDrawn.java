package com.j3d.engine.scene.nodes.geometry.base;

import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.geometry.Pure;

import java.util.ArrayList;

public interface DecomposeWhenDrawn<T extends Pure> {
    void decompose();
    ArrayList<RenderState<T, GObject>> getDecomposeList();
    default void invalidateAll() {
        getDecomposeList().forEach(RenderState::invalidate);
    }
}
