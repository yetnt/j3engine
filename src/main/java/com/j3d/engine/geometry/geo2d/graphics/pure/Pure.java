package com.j3d.engine.geometry.geo2d.graphics.pure;

import com.j3d.engine.geometry.geo2d.graphics.Drawable;

public interface Pure extends Drawable {
    void invalidate();
    boolean isValid();
}
