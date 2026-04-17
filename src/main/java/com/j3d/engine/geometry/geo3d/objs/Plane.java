package com.j3d.engine.geometry.geo3d.objs;

import com.j3d.engine.SceneManager;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;

// TODO: Implement.
public class Plane extends Thing {

    public Vector3 cornerA;
    public Vector3 cornerB;
    public Vector3 cornerC;
    public Vector3 cornerD;

    public Plane(SceneManager sceneManager, Layer l, Vector3 a, Vector3 b, Vector3 c, Vector3 d) {
        super(sceneManager, l, "Plane");
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
