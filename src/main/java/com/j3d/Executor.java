package com.j3d;

import com.j3d.engine.geometry.geo2d.Winding;
import com.j3d.engine.geometry.geo2d.graphics.GCurve;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.AxisPlane;
import com.j3d.engine.geometry.geo3d.Sampler;
import com.j3d.engine.geometry.geo3d.Solids;
import com.j3d.engine.interact.input.keyboard.GlobalKeybinds;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.actions.Action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Executor is a class called by {@link EngineFrame#main(String[])} that just draws things ot the window
 */
public class Executor {
    /**
     * The sceneManager instance.
     */
    private final SceneManager sceneManager;

    private final Layer layer = new Layer("exec");

    /**
     * Default Constructor
     * @param r The SceneManager Instance.
     */
    public Executor(SceneManager r) {
        sceneManager = r;
    }

    /**
     * Runs the executor.
     */
    public void run(Graphics2D graphics2D) {
        StaticRefs.getSceneManager().layers.add(layer);

        Thing ngon = ngon(3);
        Thing cub = cube();
        StaticRefs.getCamera().lookAt(cub.getCentroid());
        Thing tris = threeTris();

        Thing solid = Solids.prism(
                20,4, layer,
                new AxisPlane(
                        Vector3.ZERO,
                        new Vector3(0, 0.2, 0.6),
                        new Vector3(0.1, 0.4, 0)
                ).sameAxes(Vector3.X(-10), Vector3.X(-2))
        );
        Thing genericSolid = Solids.prism(
                10,40, layer,
                AxisPlane.ZY(Vector3.ZERO)
                        .sameAxes(Vector3.X(-30), Vector3.X(-22))
        );
        Thing cone = cone(20, 20);

        ArrayList<Action<?>> actions = new ArrayList<>(List.of(
                cub.rotate(Vector3.Z, 45),
                cub.translate(new Vector3(4, 2, 3)),
                cub.scale(0.4),
                tris.translate(Vector3.X(14)),
                cub.rotate(new Vector3(2, 3, 1), 2),
                solid.translate(Vector3.X(-20)),
                ngon.rotate(Vector3.Y, 20), // 20 degrees
                ngon.translate(Vector3.X(40)),
                cone.rotate(Vector3.X(5), 5)
        ));
        actions.forEach(Action::run);
        actions.forEach(SceneManager.history::add);


    }

    public Thing cone(int max, int height) {
        GPoint centre = new GPoint(Vector3.ZERO);
        AxisPlane axisPlane = AxisPlane.XZ(centre.getPivot());

        ArrayList<GPoint> points = Sampler.ngon(
                5,
                axisPlane,
                max,
                GPoint::new
        );

        centre.setPivot(centre.getPivot().sub(Vector3.Y(height)));

        HashSet<GLine> lines = new HashSet<>();
        HashSet<GTri> tris = new HashSet<>();
        // connect lines to circle
        for (int i = 0; i < points.size(); i++) {
            GPoint p1 = points.get(i);
            GPoint p2 = points.get((i + 1) % points.size()); // Connect last point to first
            GLine lnInCirc = GLine.getInstance(lines, p1, p2); // the line that is part of the ngon
            GLine lineA = GLine.getInstance(lines, p1, centre);
            GLine lineB = GLine.getInstance(lines, p2, centre);
            lines.add(lnInCirc);
            lines.add(lineA);
            lines.add(lineB);
            GTri tri = new GTri(
                    Color.ORANGE,
                    lnInCirc,
                    lineA,
                    lineB,
                    new Winding(p1, centre, p2)
            );
            tris.add(tri);
        }

        return new Thing(StaticRefs.getSceneManager(), layer, "Cone")
                .addObjs(centre)
                .addObjs(points.toArray(new GPoint[0]))
                .addObjs(lines.toArray(new GLine[0]))
                .addObjs(tris.toArray(new GTri[0]));
    }

    public Thing ngon(int max) {
        GPoint centre = new GPoint(Vector3.ZERO);
        AxisPlane axisPlane = AxisPlane.XY(centre.getPivot());

        ArrayList<GPoint> points = Sampler.ngon(
                10,
                axisPlane,
                max,
                p -> {
                    GPoint point = new GPoint(p);
                    // Random colour
                    Random random = new Random();
                    int red = random.nextInt(256);   // 0 to 255
                    int green = random.nextInt(256);
                    int blue = random.nextInt(256);
                    //
                    Color randomColor = new Color(red, green, blue);
                    point.setColour(randomColor);
                    return point;
                }
        );
        HashSet<GLine> lines = new HashSet<>();
        ArrayList<GLine> triLines = new ArrayList<>();
        HashSet<GTri> tris = new HashSet<>();
        // connect lines to circle
        for (int i = 0; i < points.size(); i++) {
            GPoint p1 = points.get(i);
            GPoint p2 = points.get((i + 1) % points.size()); // Connect last point to first
            Solids.topFaceTri(lines, p1, p2, centre, tris);
        }

        return new Thing(StaticRefs.getSceneManager(), layer, "Circle")
                .addObjs(centre)
                .addObjs(points.toArray(new GPoint[0]))
                .addObjs(lines.toArray(new GLine[0]))
//                .addObjs(triLines.toArray(new GLine[0]))
                .addObjs(tris.toArray(new GTri[0]));
    }

    public void updatekeystrokeexample() {

        // How to update a keystroke ->
        // make the new keystroke
        KeyStroke newkeyStroke = KeyStroke.getKeyStroke(
                KeyEvent.VK_M,
                InputEvent.CTRL_DOWN_MASK,
                false
        );
        // get the key
        GlobalKeybinds SELECT_SUB = GlobalKeybinds.SELECT_SUBTRACT_DOWN;
        // get the old keystroke
        KeyStroke oldkeyStroke = SELECT_SUB.getKey().getKeyStroke();
        // update the keystroke
        SELECT_SUB.getKey().setKeyStroke(
                newkeyStroke
        );
        // rebind the keystroke
        KeyBindings.UpdatedJ3Key updatedJ3Key = StaticRefs.getGlobalKeybinds().rebindJ3KeyKeystroke(
                oldkeyStroke,
                SELECT_SUB.getKey()
        );

        if (updatedJ3Key.keyChangeSuccess) StaticRefs.getLog().println("WOHOOOO KEY CHANGE SUCCESS!!!! :)))");
    }

    /**
     * Creates three triangles stacked vertically along the Z axis.
     * This is mainly for depth testing.
     */
    public Thing threeTris() {
        GPoint A = new GPoint(new Vector3(10, 0, 0));
        GPoint B = new GPoint(new Vector3(0, 10, 0));
        GPoint C = new GPoint(new Vector3(10, 10, 0));
        GTri tri1 = new GTri(Color.ORANGE, A, B, C);

        GPoint A1 = new GPoint(new Vector3(10, 0, 10));
        GPoint B1 = new GPoint(new Vector3(0, 10, 10));
        GPoint C1 = new GPoint(new Vector3(10, 10, 10));
        GTri tri2 = new GTri(Color.PINK, A1, B1, C1);

        GPoint A2 = new GPoint(new Vector3(10, 0, 20));
        GPoint B2 = new GPoint(new Vector3(0, 10, 20));
        GPoint C2 = new GPoint(new Vector3(10, 10, 20));
        GTri tri3 = new GTri(Color.CYAN, A2, B2, C2);

        ArrayList<GTri> tris = new ArrayList<>();
        tris.add(tri1);
        tris.add(tri2);
        tris.add(tri3);

        GPoint a = new GPoint(new Vector3(30, -13, 20));
        GPoint b = new GPoint(new Vector3(-2, 51, 18));
        GPoint c = new GPoint(new Vector3(40, 2, -1));

        GCurve curve = new GCurve(
                a,b,c
        );

        curve.setColour(Color.ORANGE);

        return new Thing(sceneManager, layer, "Three Tris").addObjs(tri1, tri2, tri3,
                tri1.getLegA(), tri1.getLegB(), tri1.getLegC(),
                tri2.getLegA(), tri2.getLegB(), tri2.getLegC(),
                tri3.getLegA(), tri3.getLegB(), tri3.getLegC(),
                A, B, C,
                A1, B1, C1,
                A2, B2, C2,
                curve, a,b,c
        );
    }

    public Thing openedLetter() {
        GPoint P = new GPoint(new Vector3(0, 40, 0));
        GPoint Q = new GPoint(new Vector3(0, 20, 0));

        GPoint S = new GPoint(new Vector3(-20, 40, 20));
        GPoint R = new GPoint(new Vector3(-20, 20, 20));

        GPoint U = new GPoint(new Vector3(20, 40, 20));
        GPoint T = new GPoint(new Vector3(20, 20, 20));

        GTri SPR = new GTri(Color.ORANGE, S, P, R);
        GTri PRQ = new GTri(Color.ORANGE, P, R, Q);
        GTri PQT = new GTri(Color.ORANGE, P, Q, T);
        GTri PUT = new GTri(Color.ORANGE, P, U, T);

        GTri PRT = new GTri(Color.PINK, P, R, T);

        return new Thing(StaticRefs.getSceneManager(), null, "Letter")
                .addObjs(
                        P, Q, S, R, U, T,
                        SPR, PRQ, PQT, PUT, PRT,
                        SPR.getLegA(), SPR.getLegB(), SPR.getLegC(),
                        PRQ.getLegA(), PRQ.getLegB(), PRQ.getLegC(),
                        PQT.getLegA(), PQT.getLegB(), PQT.getLegC(),
                        PUT.getLegA(), PUT.getLegB(), PUT.getLegC(),
                        PRT.getLegA(), PRT.getLegB(), PRT.getLegC()
                );
    }

    public Thing test() {
        GPoint A = new GPoint(new Vector3(10, 0, 0));
        GPoint B = new GPoint(new Vector3(0, 10, 0));
        GPoint C = new GPoint(new Vector3(0, 0, 10));
        GTri triangl = new GTri(Color.ORANGE, A, B, C);
        StaticRefs.getLog().println(triangl.getId().toString());
        return new Thing(sceneManager, null, "Test").addObjs(triangl, triangl.getLegA(), triangl.getLegB(), triangl.getLegC(), A, B, C);
    }

    public void note(GPoint point, String label) {
        sceneManager.scheduleOverlap(
                point.getId(),
                g ->
                        sceneManager.drawText3D(
                                g, point.getPivot(),
                                label, StaticRefs.getCamera()
                        )
                );
    }

    public Thing cube() {
        // TODO: Cube triangles not winded properly.
        GPoint A = new GPoint(new Vector3(-5, 5, -5));
        note(A, "A");
        GPoint B = new GPoint(new Vector3(-5, -5, -5));
        note(B, "B");
        GPoint C = new GPoint(new Vector3(5, 5, -5));
        note(C, "C");
        GTri face1tri1 = new GTri(Color.ORANGE, C, B, A);
        GPoint D = new GPoint(new Vector3(5, -5, -5));
        GTri face1tri2 = new GTri(Color.ORANGE.darker(), B, C, D);
        GPoint E = new GPoint(new Vector3(5, 5, 5));
        GTri face2tri1 = new GTri(Color.PINK, D, C, E);
        GPoint F = new GPoint(new Vector3(5, -5, 5));
        GTri face2tri2 = new GTri(Color.PINK.darker(), D, E, F);
        GPoint H = new GPoint(new Vector3(-5, 5, 5));
        GTri face3tri1 = new GTri(Color.GREEN, F, E, H);
        GPoint G = new GPoint(new Vector3(-5, -5, 5));
        GTri face3tri2 = new GTri(Color.GREEN.darker(), F, H, G);
        GTri face4tri1 = new GTri(Color.BLUE, H, A, G);
        GTri face4tri2 = new GTri(Color.BLUE.darker(), A, G, B);
        GTri face5tri1 = new GTri(Color.LIGHT_GRAY, A, H, C);
        GTri face5tri2 = new GTri(Color.LIGHT_GRAY.darker(), H, C, E);
        GTri face6tri1 = new GTri(Color.YELLOW, D, F, B);
        GTri face6tri2 = new GTri(Color.YELLOW.darker(), F, B, G);

        return new Thing(sceneManager, layer, "Cube").addObjs(
                A, B, C, D, E, F, G, H, face1tri1, face1tri2, face2tri1, face2tri2, face3tri1, face3tri2, face4tri1, face4tri2,
                face5tri1, face5tri2, face6tri1, face6tri2,
                face1tri1.getLegA(), face1tri1.getLegB(), face1tri1.getLegC(),
                face1tri2.getLegA(), face1tri2.getLegB(), face1tri2.getLegC(),
                face2tri1.getLegA(), face2tri1.getLegB(), face2tri1.getLegC(),
                face2tri2.getLegA(), face2tri2.getLegB(), face2tri2.getLegC(),
                face3tri1.getLegA(), face3tri1.getLegB(), face3tri1.getLegC(),
                face3tri2.getLegA(), face3tri2.getLegB(), face3tri2.getLegC(),
                face4tri1.getLegA(), face4tri1.getLegB(), face4tri1.getLegC(),
                face4tri2.getLegA(), face4tri2.getLegB(), face4tri2.getLegC(),
                face5tri1.getLegA(), face5tri1.getLegB(), face5tri1.getLegC(),
                face5tri2.getLegA(), face5tri2.getLegB(), face5tri2.getLegC(),
                face6tri1.getLegA(), face6tri1.getLegB(), face6tri1.getLegC(),
                face6tri2.getLegA(), face6tri2.getLegB(), face6tri2.getLegC()
        ).solidify();
    }
}
