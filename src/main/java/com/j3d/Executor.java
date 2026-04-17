package com.j3d;

import com.j3d.engine.geometry.constraints.concrete.MidpointConstraint;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.interact.input.keyboard.DefaultKeys;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.actions.Action;
import com.j3d.utility.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

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
        Static.sceneManager.layers.add(layer);

        Thing cub = cube();
        Static.camera.lookAt(cub.getCentroid());
        Thing tris = threeTris();

        Pair<Thing, MidpointConstraint> line = someLine();
        ArrayList<Action<?>> actions = new ArrayList<>(List.of(
                cub.rotate(new Vector3(0, 0, 1), 45),
                cub.translate(new Vector3(4, 2, 3)),
                cub.scale(0.4),
                tris.translate(new Vector3(14, 0, 0)),
                cub.rotate(new Vector3(2, 3, 1), 2),
                line.first.translate(new Vector3(80, -10, 0))
        ));
        actions.forEach(Action::run);
        actions.forEach(SceneManager.history::add);
        line.second.applyConstraint();

    }

    public Pair<Thing, MidpointConstraint> someLine() {
        GPoint A = new GPoint(new Vector3(10, 0, 0));
        GPoint B = new GPoint(new Vector3(30, 0, 0));
        GPoint C = new GPoint(new Vector3(-10, 10, 0));
        // D will be the midpt between A and B
        GPoint D = new GPoint(new Vector3(0, 0, 0));

        GLine line = new GLine(A, B);
        GLine line2 = new GLine(C, D);

        MidpointConstraint mdpc = new MidpointConstraint(D, line);
        D.getConstraints().addConstraint(
                mdpc
        );
        mdpc.applyConstraint();

        return new Pair<>(new Thing(Static.sceneManager, layer, "constrainted")
                .addObjs(A, B, C, D, line, line2), mdpc);
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
        DefaultKeys SELECT_SUB = DefaultKeys.SELECT_SUBTRACT_DOWN;
        // get the old keystroke
        KeyStroke oldkeyStroke = SELECT_SUB.getKey().getKeyStroke();
        // update the keystroke
        SELECT_SUB.getKey().setKeyStroke(
                newkeyStroke
        );
        // rebind the keystroke
        KeyBindings.UpdatedJ3Key updatedJ3Key = Static.keybinds.rebindJ3KeyKeystroke(
                oldkeyStroke,
                SELECT_SUB.getKey()
        );

        if (updatedJ3Key.keyChangeSuccess) Static.log.println("WOHOOOO KEY CHANGE SUCCESS!!!! :)))");
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

        return new Thing(sceneManager, layer, "Three Tris").addObjs(tri1, tri2, tri3,
                tri1.getLegA(), tri1.getLegB(), tri1.getLegC(),
                tri2.getLegA(), tri2.getLegB(), tri2.getLegC(),
                tri3.getLegA(), tri3.getLegB(), tri3.getLegC(),
                A, B, C,
                A1, B1, C1,
                A2, B2, C2
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

        return new Thing(Static.sceneManager, null, "Letter")
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
        Static.log.println(triangl.getId().toString());
        return new Thing(sceneManager, null, "Test").addObjs(triangl, triangl.getLegA(), triangl.getLegB(), triangl.getLegC(), A, B, C);
    }

    public Thing cube() {
        GPoint A = new GPoint(new Vector3(-5, 5, -5));
        GPoint B = new GPoint(new Vector3(-5, -5, -5));
        GPoint C = new GPoint(new Vector3(5, 5, -5));
        GTri face1tri1 = new GTri(Color.ORANGE, A, B, C);
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

        );
    }
}
