package com.j3d.gen.grid;

import com.j3d.StaticRefs;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.ConversionProperties;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.ui.theme.J3DTheme;

import java.awt.*;
import java.util.ArrayList;

public class Point implements GridObject<GPoint> {
    CartesianPoint point;

    public Point(CartesianPoint point) {
        this.point = point;
    }

    public CartesianPoint getPoint() {
        return point;
    }

    @Override
    public void draw(Graphics2D graphics2D, ConversionProperties props) {
        // draw as circle.
        ScreenPoint sp = point.toScreen(props);
        int size = 10;

        Stroke original = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(J3DTheme.TEXT_SECONDARY.color());
        graphics2D.fillOval(sp.x - size / 2, sp.y - size / 2, size, size);
        graphics2D.setColor(Color.black);
        graphics2D.setStroke(original);
    }

    @Override
    public void drawWorld(Graphics2D graphics2D, AxisPlane axisPlane) {
        // draw dot.
        Vector3 pos = axisPlane.toWorld(point);
        ScreenPoint sp = pos
                .toPoint(StaticRefs.getCamera())
                .toScreen();

        int size = GPoint.DIAMETER;
        graphics2D.setColor(J3DTheme.TEXT_SECONDARY.color());
        graphics2D.fillOval(sp.x - size / 2, sp.y - size / 2, size, size);
        graphics2D.setColor(Color.black);
    }

    @Override
    public GPoint render(AxisPlane plane, ArrayList<GObject> objects) {
        return new GPoint(plane.toWorld(point));
    }
}
