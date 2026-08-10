package com.j3d.engine.scene.nodes;

import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.scene.nodes.geometry.*;
import com.j3d.engine.scene.nodes.geometry.base.Winding;
import com.j3d.engine.scene.nodes.util.Solids;
import com.j3d.ui.theme.J3DTheme;

import java.util.ArrayList;
import java.util.List;

public abstract class Creator {
    public static ArrayList<GObject> cube() {
        return Solids
                .prism(10, 4,
                        // The origin of this XZ wont be used so set to null
                        AxisPlane.XZ(null).sameAxes(
                                // explicitly set the origins.
                                new Vector3(0, 0, 0),
                                new Vector3(0, 10, 0)
                        ), false
                );
    }

    public static ArrayList<GObject> curve() {
        GPoint start = new GPoint(new Vector3(-10, 0, -10));
        GPoint control = new GPoint(new Vector3(-10, 0, 10));
        GPoint end = new GPoint(new Vector3(10, 0, 10));

        return new ArrayList<>(List.of(
                start, control, end,
                new GCurve(start, control, end)
        ));
    }

    public static ArrayList<GObject> triangle() {
        GPoint start = new GPoint(new Vector3(-10, 0, -10));
        GPoint mid = new GPoint(new Vector3(-10, 0, 10));
        GPoint end = new GPoint(new Vector3(10, 0, 10));

        GLine line1 = new GLine(start, mid);
        GLine line2 = new GLine(mid, end);
        GLine line3 = new GLine(end, start);

        return new ArrayList<>(List.of(
                start, mid, end,
                line1, line2, line3,
                new GTri(J3DTheme.TEXT_PRIMARY.color(), line1, line2, line3, new Winding(start, mid, end))
        ));
    }

    public static ArrayList<GObject> point() {
        return new ArrayList<>(List.of(new GPoint(new Vector3(0, 0, 0))));
    }
}
