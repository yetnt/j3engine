package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.draw.RenderState;
import com.j3d.engine.geometry.geo2d.graphics.Drawable;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.pure.Pure;

import java.util.ArrayList;

public interface DecomposeWhenDrawn<T extends Pure> {
    void decompose();
    ArrayList<RenderState<T, GObject>> getDecomposeList();
}
