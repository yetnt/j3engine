package com.j3d.gen.grid;

import com.j3d.engine.math.convert.Conversion;
import com.j3d.engine.math.convert.ConversionWithOffset;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.scene.nodes.geometry.GObject;

import java.awt.*;
import java.util.ArrayList;

public interface GridObject<T extends GObject> {
    void draw(Graphics2D graphics2D, ConversionWithOffset props);
    void drawWorld(Graphics2D graphics2D, AxisPlane axisPlane);
    T render(AxisPlane plane, ArrayList<GObject> objects);
}
