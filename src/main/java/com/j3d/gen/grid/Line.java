package com.j3d.gen.grid;

import com.j3d.StaticRefs;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.ConversionProperties;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.ui.theme.J3DTheme;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

public class Line implements GridObject<GLine> {
    CartesianPoint p1, p2;

    public Line(CartesianPoint p1, CartesianPoint p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    private static int orientation(CartesianPoint p, CartesianPoint q, CartesianPoint r) {
        double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0;  // Collinear
        return (val > 0) ? 1 : 2; // Clockwise or Counterclockwise
    }

    public static boolean intersects(Line l1, Line l2) {
        CartesianPoint p1 = l1.p1, q1 = l1.p2;
        CartesianPoint p2 = l2.p1, q2 = l2.p2;

        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Special Cases for collinear points
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        // p1, q1 and q2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        // p2, q2 and p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        // p2, q2 and q1 are collinear and q1 lies on segment p2q2
        return o4 == 0 && onSegment(p2, q1, q2);// Doesn't fall in any of the above cases
    }

    private static boolean onSegment(CartesianPoint p, CartesianPoint q, CartesianPoint r) {
        return (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y));
    }


    public CartesianPoint getP1() {
        return p1;
    }

    public CartesianPoint getP2() {
        return p2;
    }

    @Override
    public void draw(Graphics2D graphics2D, ConversionProperties props) {
        // draw as line
        ScreenPoint sp1 = p1.toScreen(props);
        ScreenPoint sp2 = p2.toScreen(props);
        Stroke original = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(J3DTheme.TEXT_SECONDARY.color());
        graphics2D.drawLine(sp1.x, sp1.y, sp2.x, sp2.y);
        graphics2D.setColor(Color.black);
        graphics2D.setStroke(original);
    }

    @Override
    public void drawWorld(Graphics2D graphics2D, AxisPlane axisPlane) {
        // draw line
        Vector3 pos1 = axisPlane.toWorld(p1);
        Vector3 pos2 = axisPlane.toWorld(p2);

        ScreenPoint sp1 = pos1
                .toPoint(StaticRefs.getCamera())
                .toScreen();
        ScreenPoint sp2 = pos2
                .toPoint(StaticRefs.getCamera())
                .toScreen();

        graphics2D.setColor(J3DTheme.TEXT_SECONDARY.color());
        StaticRefs.getSceneManager().drawLine3D(
                graphics2D,
                pos1, pos2, StaticRefs.getCamera()
        );
    }

    public static void drawLine(Supplier<Color> col, Supplier<CartesianPoint> p1, Supplier<CartesianPoint> p2, Graphics2D g, ConversionProperties c) {
        ScreenPoint sp1 = p1.get().toScreen(c);
        ScreenPoint sp2 = p2.get().toScreen(c);

        Stroke original = g.getStroke();
        g.setStroke(new BasicStroke(3));
        g.setColor(col.get());
        g.drawLine(sp1.x, sp1.y, sp2.x, sp2.y);
        g.setColor(Color.black);
        g.setStroke(original);
    }

    @Override
    public GLine render(AxisPlane plane, ArrayList<GObject> objects) {
        GPoint p = objects.stream()
                .filter(o -> o instanceof GPoint)
                .map(o -> (GPoint)o)
                .filter(o -> plane.toWorld(p1).equals(o.getPivot()))
                .findFirst()
                .orElse(null);
        GPoint p2 = objects.stream()
                .filter(o -> o instanceof GPoint)
                .map(o -> (GPoint)o)
                .filter(o -> plane.toWorld(getP2()).equals(o.getPivot()))
                .findFirst()
                .orElse(null);
        if (p == null || p2 == null) return null;
        return new GLine(p, p2);
    }
}
