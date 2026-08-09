package com.j3d.gen.grid;

import com.j3d.engine.geometry.Drawable;
import com.j3d.engine.math.ConversionProperties;
import com.j3d.engine.math.Dim;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.utility.generic.Pair;

import java.awt.*;
import java.util.ArrayList;

public interface GridObject<T extends GObject> {
    void draw(Graphics2D graphics2D, ConversionProperties props);
    void drawWorld(Graphics2D graphics2D, AxisPlane axisPlane);
    T render(AxisPlane plane, ArrayList<GObject> objects);
}
