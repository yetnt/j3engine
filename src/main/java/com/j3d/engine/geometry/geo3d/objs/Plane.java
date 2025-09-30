package com.j3d.engine.geometry.geo3d.objs;

import com.j3d.Main;
import com.j3d.engine.Layer;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;

import java.awt.*;

public class Plane extends Thing {

    public Vector3 cornerA;
    public Vector3 cornerB;
    public Vector3 cornerC;
    public Vector3 cornerD;

    public Plane(Renderer renderer, Layer l, Vector3 a, Vector3 b, Vector3 c, Vector3 d) {
        super(renderer, l);
        // Create GTri 1 from cornerA to cornerB to cornerC
        // Create GTri 2 from cornerB to cornerC to cornerD
        GPoint pointB = new GPoint(b);
        GPoint pointC = new GPoint(c);
        GTri tri1 = new GTri(
                Color.BLUE,
                new GPoint(a),
                pointB,
                pointC
        );
        GTri tri2 = new GTri(
                Color.BLUE,
                pointB,
                pointC,
                new GPoint(d)
        );
        addObjs(tri1, tri2);
    }
}
