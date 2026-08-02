package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.geometry.geo2d.graphics.Drawable;
import com.j3d.engine.geometry.geo2d.graphics.pure.Pure;

import java.util.ArrayList;

public interface DecomposeWhenDrawn<T extends Pure> {
    void decompose();
    ArrayList<T> getDecomposeList();
}
